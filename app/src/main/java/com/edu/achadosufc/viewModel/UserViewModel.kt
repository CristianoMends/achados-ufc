package com.edu.achadosufc.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.data.repository.ItemRepository
import com.edu.achadosufc.data.UserPreferences
import com.edu.achadosufc.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class UserViewModel(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository,
    private val userPreferencesRepository: UserPreferences,
    context: Context
) : ViewModel() {

    private val sessionManager =
        SessionManager(context)

    private val _selectedUser = MutableStateFlow<UserResponse?>(null)
    val selectedUser: StateFlow<UserResponse?> = _selectedUser

    private val _userItems = MutableStateFlow<List<Item>>(emptyList())
    val userItems: StateFlow<List<Item>> = _userItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loggedUser = MutableStateFlow<UserResponse?>(null)
    val loggedUser: StateFlow<UserResponse?> = _loggedUser

    fun getCurrentUser(): Int {
        return sessionManager.fetchUserLoggedIn()
    }

    init {
        _selectedUser.value = null
        _isLoading.value = true
        viewModelScope.launch {
            userPreferencesRepository.userId.collectLatest { loggedUserId ->
                if (loggedUserId != null && loggedUserId != -1) {
                    userRepository.getUserByIdLocal(loggedUserId)
                        .catch { e ->
                            _errorMessage.value =
                                "Erro ao carregar usuário logado do DB local: ${e.message}"
                            Log.e("UserViewModel", "Erro ao ler usuário logado do DB local", e)
                            _loggedUser.value = null
                            _isLoading.value = false
                        }
                        .collectLatest { userFromDb ->
                            _loggedUser.value = userFromDb
                            if (userFromDb == null) {
                                refreshLoggedInUser(loggedUserId)
                                _isLoading.value = false
                            }
                        }
                } else {
                    _loggedUser.value = null
                    _isLoading.value = false
                }
            }
        }


        viewModelScope.launch {
            _isLoading.value = true
            _selectedUser.collectLatest { user ->
                user?.id?.let { userIdInt ->

                    itemRepository.getItemsByUserIdFromLocalDb(userIdInt)
                        .catch { e ->
                            _errorMessage.value =
                                "Erro ao ler publicações do usuário do DB local: ${e.message}"
                            Log.e(
                                "UserViewModel",
                                "Erro ao ler DB local para usuário $userIdInt",
                                e
                            )
                            _userItems.value = emptyList()
                            _isLoading.value = false
                        }
                        .collectLatest { fetchedItems ->
                            _userItems.value = fetchedItems



                            if (fetchedItems.isEmpty() && !_isLoading.value) {
                                refreshUserItems(userIdInt)
                                _isLoading.value = true
                            }
                        }
                }
            }
        }

    }

    fun cleanAllData() {
        _selectedUser.value = null
        _loggedUser.value = null
        _userItems.value = emptyList()
        _isLoading.value = false
        _errorMessage.value = null
    }

    fun refreshLoggedInUser(userId: Int) {
        viewModelScope.launch {
            try {
                userRepository.fetchUserByIdAndSave(userId)
                Log.d("UserViewModel", "Usuário logado $userId sincronizado da rede.")

            } catch (e: UnknownHostException) {
            } catch (e: Exception) {
            }
        }
    }

    fun getUserDetailsAndItems(userId: Int) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {

            try {
                userRepository.fetchUserByIdAndSave(userId)
            } catch (e: Exception) {
            }

            userRepository.getUserByIdLocal(userId)
                .catch { e ->
                    _errorMessage.value =
                        "Erro ao carregar detalhes do usuário do DB local: ${e.message}"
                    _selectedUser.value = null
                }
                .collectLatest { userFromDb ->
                    _selectedUser.value = userFromDb
                }
        }
    }

    private fun refreshUserDetailFromNetwork(userId: Int) {
        viewModelScope.launch {
            try {

                userRepository.fetchUserByIdAndSave(userId)
            } catch (e: UnknownHostException) {
                _errorMessage.value =
                    "Sem conexão para atualizar detalhes do usuário. Exibindo dados locais."
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao sincronizar detalhes do usuário: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun refreshUserItems(userId: Int) {
        viewModelScope.launch {
            try {
                itemRepository.fetchAndSaveItemsByUserId(userId)
                Log.d("UserViewModel", "Sincronização de publicações do usuário $userId iniciada.")
            } catch (e: UnknownHostException) {
                _errorMessage.value =
                    "Sem conexão para sincronizar publicações do usuário. Exibindo locais."
                Log.w(
                    "UserViewModel",
                    "Sem conexão para sincronizar publicações do usuário $userId",
                    e
                )
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao sincronizar publicações do usuário: ${e.message}"
                Log.e("UserViewModel", "Erro ao sincronizar publicações do usuário $userId", e)
            }
        }
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
        _userItems.value = emptyList()
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}