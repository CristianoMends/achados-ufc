package com.edu.achadosufc.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.data.repository.LoginRepository
import com.edu.achadosufc.data.repository.UserPreferencesRepository
import com.edu.achadosufc.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

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


    init {
        viewModelScope.launch {
            val initialKeepLoggedIn = userPreferencesRepository.keepLoggedIn.first()
            val initialUserId = userPreferencesRepository.userId.firstOrNull()

            _keepLoggedIn.value = initialKeepLoggedIn

            if (initialKeepLoggedIn && initialUserId != null) {
                _loading.value = true
                try {
                    val user =
                        userRepository.findById(initialUserId)
                    if (user != null) {
                        _loggedUser.value = user
                    } else {
                        _error.value = "Sessão expirada ou usuário inválido. Faça login novamente."
                        userPreferencesRepository.clearUserId()
                        userPreferencesRepository.updateKeepLoggedIn(false)
                    }
                } catch (e: Exception) {
                    _error.value = "Erro ao tentar login automático: ${e.message}"
                    userPreferencesRepository.clearUserId()
                    userPreferencesRepository.updateKeepLoggedIn(false)
                } finally {
                    _loading.value = false
                }
            } else if (initialKeepLoggedIn && initialUserId == null) {
                userPreferencesRepository.updateKeepLoggedIn(false)
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

    fun updateKeepLoggedInPreference(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateKeepLoggedIn(value)
            _keepLoggedIn.value = value
            if (!value) {
                userPreferencesRepository.clearUserId()
            }
        }
    }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _error.value = "Preencha todos os campos"
            return
        }

        _loading.value = true
        _error.value = null
        _loggedUser.value = null

        viewModelScope.launch {
            try {
                val res = loginRepository.login(_email.value, _password.value)

                if (res != null) {
                    val user = userRepository.findByUsername(_email.value)

                    if (user != null) {
                        _loggedUser.value = user
                        sessionManager.saveAuthToken(res.access_token)
                        if (_keepLoggedIn.value) {
                            userPreferencesRepository.saveUserId(user.id)
                        } else {
                            userPreferencesRepository.clearUserId()
                        }
                    } else {
                        _error.value = "Login bem-sucedido, mas dados do usuário não encontrados."
                    }
                } else {
                    _error.value = "Usuário ou senha inválidos"
                }
            } catch (e: Exception) {
                if (e.message?.contains("failed to connect", ignoreCase = true) == true ||
                    e.message?.contains("unable to resolve host", ignoreCase = true) == true ||
                    e.message?.contains("timeout", ignoreCase = true) == true
                ) {
                    _error.value = "Servidor indisponível. Tente novamente mais tarde."
                } else {
                    _error.value = e.message ?: "Erro desconhecido ao tentar fazer login."
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            _loggedUser.value = null

            userPreferencesRepository.clearUserId()

            userPreferencesRepository.updateKeepLoggedIn(false)
            _keepLoggedIn.value = false

            _loading.value = false
        }
    }
}