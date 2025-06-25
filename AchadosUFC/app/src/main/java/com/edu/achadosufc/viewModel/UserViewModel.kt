package com.edu.achadosufc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.model.item.Item
import com.edu.achadosufc.model.item.ItemRepository
import com.edu.achadosufc.model.user.UserRepository
import com.edu.achadosufc.model.user.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val itemRepository: ItemRepository = ItemRepository()
) : ViewModel() {

    private val _selectedUser = MutableStateFlow<UserResponse?>(null)
    val selectedUser: StateFlow<UserResponse?> = _selectedUser

    private val _userItems = MutableStateFlow<List<Item>>(emptyList())
    val userItems: StateFlow<List<Item>> = _userItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun getUserDetailsAndItems(userId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        _selectedUser.value = null
        _userItems.value = emptyList()

        viewModelScope.launch {
            try {
                val user = userRepository.findById(userId)
                if (user != null) {
                    _selectedUser.value = user
                    val items = itemRepository.getAllByUserId(userId)
                    _userItems.value = items
                } else {
                    _errorMessage.value = "Usuário não encontrado."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar detalhes do usuário ou itens: ${e.message}"
            } finally {
                _isLoading.value = false
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