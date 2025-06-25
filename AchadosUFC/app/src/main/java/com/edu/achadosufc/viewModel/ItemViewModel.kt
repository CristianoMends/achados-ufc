package com.edu.achadosufc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.model.item.Item
import com.edu.achadosufc.model.item.ItemRepository
import com.edu.achadosufc.model.user.UserResponse // Se não for usado, pode ser removido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository = ItemRepository()
) : ViewModel() {
    private var _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _selectedItem = MutableStateFlow<Item?>(null)
    val selectedItem: StateFlow<Item?> = _selectedItem

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setSelectedItem(item: Item) {
        _selectedItem.value = item
    }

    fun clearSelectedItem() {
        _selectedItem.value = null
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun getItemsByUser(userId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val res = itemRepository.getAllByUserId(userId)
                _items.value = res
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar itens: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getItemDetails(itemId: Int) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val fetchedItem = itemRepository.getItemById(itemId)
                _selectedItem.value = fetchedItem
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar detalhes do item: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}