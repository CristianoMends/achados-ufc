package com.edu.achadosufc.viewModel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.data.repository.LoginRepository
import com.edu.achadosufc.data.repository.UserPreferencesRepository
import com.edu.achadosufc.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class LoginViewModel(

    private val userRepository: UserRepository,
    private val loginRepository: LoginRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    context: Context
) : ViewModel() {


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
    private val _keepLoggedIn = MutableStateFlow(false)
    val keepLoggedIn: StateFlow<Boolean> = _keepLoggedIn
    private val _confirmButtonAction = MutableStateFlow<(() -> Unit)?>(null)
    val confirmButtonAction: StateFlow<(() -> Unit)?> = _confirmButtonAction
    private val _isAutoLoginCheckComplete = MutableStateFlow(false)
    val isAutoLoginCheckComplete: StateFlow<Boolean> = _isAutoLoginCheckComplete.asStateFlow()

    init {

        viewModelScope.launch {
            val initialKeepLoggedIn = userPreferencesRepository.keepLoggedIn.first()
            val initialUserId = userPreferencesRepository.userId.first()

            _keepLoggedIn.value = initialKeepLoggedIn

            if (initialKeepLoggedIn && initialUserId != null) {
                _loading.value = true
                try {
                    val userRemote = userRepository.fetchUserByIdAndSave(userId = initialUserId)
                        ?: throw Exception("Usuário não encontrado no servidor.")

                    _loggedUser.value = userRemote

                } catch (e: Exception) {
                    val userLocal = userRepository.getUserByIdLocal(initialUserId).first()

                    if (userLocal != null) {
                        _loggedUser.value = userLocal
                    } else {
                        _error.value = "Sessão inválida. Faça login novamente."
                        _confirmButtonAction.value = { logout() }
                    }
                } finally {
                    _loading.value = false
                    _isAutoLoginCheckComplete.value = true
                }
            } else {
                _isAutoLoginCheckComplete.value = true
            }
        }


        viewModelScope.launch {

            userPreferencesRepository.keepLoggedIn.collect { value ->

                _keepLoggedIn.value = value

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

    fun updateKeepLoggedInPreference(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateKeepLoggedIn(value)
            if (!value) {
                userPreferencesRepository.clearUserId()
            }
        }
    }

    fun saveUserIdOnSession(userId: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveUserId(userId)
        }
    }

    fun updateKeepLoggedInOnSession(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateKeepLoggedIn(value)
            _keepLoggedIn.value = value
        }
    }


    fun login() {

        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Preencha todos os campos"
            _confirmButtonAction.value = { clearErrorMessage() }
            return

        }

        _loading.value = true
        _error.value = null
        _loggedUser.value = null
        viewModelScope.launch {

            try {
                val res = loginRepository.login(_email.value, _password.value)
                if (res != null) {
                    val user = userRepository.fetchUserByUsernameAndSave(_email.value)
                    if (user != null) {
                        _loggedUser.value = user
                        sessionManager.saveAuthToken(res.access_token)
                        sessionManager.saveUserLoggedIn(user.id)
                        val expiresInMillis = 60 * 60 * 1000
                        val expirationTime = System.currentTimeMillis() + expiresInMillis
                        sessionManager.saveExpirationTime(expirationTime)
                        if (_keepLoggedIn.value) {
                            userPreferencesRepository.saveUserId(user.id)

                        } else {
                            userPreferencesRepository.clearUserId()

                        }

                    } else {

                        _error.value = "Login bem-sucedido, mas dados do usuário não encontrados."
                        _confirmButtonAction.value = { clearErrorMessage() }
                    }

                } else {

                    _error.value = "Usuário ou senha inválidos"
                    _confirmButtonAction.value = { clearLoginFields() }

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


    fun logout() {

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _loggedUser.value = null

            sessionManager.clearSession()
            userPreferencesRepository.clearUserId()
            userPreferencesRepository.updateKeepLoggedIn(false)
            _keepLoggedIn.value = false
            _loading.value = false
            clearLoginFields()
        }

    }

}