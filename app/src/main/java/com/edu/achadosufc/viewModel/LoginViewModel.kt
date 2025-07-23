package com.edu.achadosufc.viewModel


import android.net.ConnectivityManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.data.repository.LoginRepository
import com.edu.achadosufc.data.UserPreferences
import com.edu.achadosufc.data.repository.UserRepository
import com.edu.achadosufc.data.service.ChatSocketService
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.context.GlobalContext

class LoginViewModel(

    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val userPreferencesRepository: UserPreferences,
    private val context: Context
) : ViewModel() {

    private val chatSocketService = GlobalContext.get().get<ChatSocketService>()

    private val sessionManager: SessionManager = SessionManager(context)
    private val _loggedUser = MutableStateFlow<UserResponse?>(null)
    val loggedUser: StateFlow<UserResponse?> = _loggedUser
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _confirmButtonAction = MutableStateFlow<(() -> Unit)?>(null)
    val confirmButtonAction: StateFlow<(() -> Unit)?> = _confirmButtonAction
    private val _isAutoLoginCheckComplete = MutableStateFlow(false)
    val isAutoLoginCheckComplete: StateFlow<Boolean> = _isAutoLoginCheckComplete.asStateFlow()

    init {

        viewModelScope.launch {
            val initialUserId = userPreferencesRepository.userId.first()


            if (initialUserId != null) {
                _loading.value = true
                try {
                    val userRemote = userRepository.fetchUserByIdAndSave(userId = initialUserId)
                        ?: throw Exception("Usuário não encontrado no servidor.")

                    _loggedUser.value = userRemote
                    chatSocketService.connect()

                } catch (e: retrofit2.HttpException) {
                    if (e.code() >= 500) {
                        _error.value = "Servidor indisponível. Tente novamente mais tarde."
                        _confirmButtonAction.value = { clearErrorMessage() }
                    } else {
                        _error.value = "Sessão inválida. Faça login novamente."
                        _confirmButtonAction.value = {

                            launch {
                                logout()
                            }
                        }
                    }

                } catch (e: Exception) {
                    val userLocal = userRepository.getUserByIdLocal(initialUserId).first()

                    if (userLocal != null) {
                        _loggedUser.value = userLocal
                    } else {
                        _error.value = "Sessão inválida. Faça login novamente."
                        _confirmButtonAction.value = {
                            launch {
                                logout()
                            }
                        }
                    }
                } finally {
                    _loading.value = false
                    _isAutoLoginCheckComplete.value = true
                }
            } else {
                _isAutoLoginCheckComplete.value = true
            }
        }

    }

    suspend fun loginWithGoogle(idToken: String) {
        logoutOnFirebase()

        if (!isInternetAvailable()){
            _error.value = "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            _confirmButtonAction.value = { clearErrorMessage() }
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                val res = loginRepository.loginWithGoogle(idToken)

                if (res != null) {

                    val firebaseUser = FirebaseAuth.getInstance().currentUser
                    userRepository.fetchUserByEmailAndSave(firebaseUser?.email ?: "")

                    val user = userRepository.getUserByEmailLocal(firebaseUser?.email ?: "")

                    _loggedUser.value = user
                    if (user != null) {
                        sessionManager.saveUserLoggedIn(user.id)
                        userPreferencesRepository.saveUserId(user.id)
                    }
                    chatSocketService.connect()
                } else {
                    _error.value = "Falha ao autenticar com o Google no servidor."
                    logoutOnFirebase()
                }
            } catch (e: Exception) {
                _error.value = "Erro no login com Google"
                logoutOnFirebase()
            } finally {
                _loading.value = false
            }
        }
    }


    fun clearErrorMessage() {
        _error.value = null
    }


    fun onEmailChanged(value: String) {
        _email.value = value
        _error.value = null
    }


    fun onPasswordChanged(value: String) {

        _password.value = value

        _error.value = null

    }


    fun clearLoginFields() {
        _email.value = ""
        _password.value = ""
        _error.value = null

    }

    fun saveUserIdOnSession(userId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserId(userId)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return loginRepository.getGoogleSignInClient(context)
    }

    suspend fun login() {
        logoutOnFirebase()
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Preencha todos os campos"
            _confirmButtonAction.value = { clearErrorMessage() }
            return
        }

        if (!isInternetAvailable()) {
            _error.value = "Sem conexão com a internet. Verifique sua conexão e tente novamente."
            _confirmButtonAction.value = { clearErrorMessage() }
            return
        }

        _loading.value = true
        _error.value = null
        _loggedUser.value = null

        viewModelScope.launch {
            userPreferencesRepository.clearUserId()
            try {
                val res = loginRepository.login(_email.value, _password.value)
                if (res != null) {
                    val user = userRepository.fetchUserByUsernameAndSave(_email.value)
                    if (user != null) {
                        _loggedUser.value = user
                        sessionManager.saveAuthToken(res.access_token)
                        sessionManager.saveUserLoggedIn(user.id)
                        userPreferencesRepository.saveUserId(user.id)
                        chatSocketService.connect()
                    } else {
                        _error.value = "Login bem-sucedido, mas dados do usuário não encontrados."
                        _confirmButtonAction.value = { clearErrorMessage() }
                    }

                } else {

                    if (!isInternetAvailable()) {
                        _error.value =
                            "Sem conexão com a internet. Verifique sua conexão e tente novamente."
                        _confirmButtonAction.value = { clearErrorMessage() }
                    } else {
                        _error.value = "Usuário ou senha inválidos"
                        _confirmButtonAction.value = { clearLoginFields() }
                    }
                }

            } catch (e: retrofit2.HttpException) {
                if (e.code() >= 500) {
                    _error.value = "Servidor indisponível. Tente novamente mais tarde. ${e.code()} ${e.message()}"
                    _confirmButtonAction.value = { clearErrorMessage() }
                } else {
                    _error.value = "Erro ao tentar fazer login: ${e.message()}"
                    _confirmButtonAction.value = { clearErrorMessage() }
                }
            } catch (e: Exception) {

                if (e.message?.contains("failed to connect", ignoreCase = true) == true ||
                    e.message?.contains("unable to resolve host", ignoreCase = true) == true ||
                    e.message?.contains("timeout", ignoreCase = true) == true

                ) {
                    _error.value = "Servidor indisponível. Tente novamente mais tarde."
                    _confirmButtonAction.value = { clearErrorMessage() }

                } else {
                    _error.value = e.message ?: "Erro desconhecido ao tentar fazer login."
                    _confirmButtonAction.value = { clearErrorMessage() }
                }

            } finally {
                _loading.value = false
            }
        }
    }


    fun setErrorMessage(message: String) {
        _error.value = message
        _confirmButtonAction.value = { clearErrorMessage() }
    }


    suspend fun logout() {
        logoutOnFirebase()
        chatSocketService.disconnect()
        sessionManager.clearSession()
        userPreferencesRepository.clearUserId()

        _loading.value = true
        _error.value = null
        _loggedUser.value = null
        _loading.value = false
        _isAutoLoginCheckComplete.value = true
        clearLoginFields()
    }

    private suspend fun logoutOnFirebase(){
        FirebaseAuth.getInstance().signOut()
        try {
            val googleSignInClient = loginRepository.getGoogleSignInClient(context)
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Erro ao fazer signOut do Google", e)
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }
}