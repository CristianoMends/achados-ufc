package com.edu.achadosufc.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository
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
        viewModelScope.launch {
            itemRepository.getItemsByUserIdFromLocalDb(userId)
                .catch { e ->
                    _errorMessage.value = "Erro ao ler publicações locais: ${e.message}"
                }
                .collectLatest { itemsFromDb ->
                    _items.value = itemsFromDb
                }
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                itemRepository.fetchAndSaveItemsByUserId(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao sincronizar publicações: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getItemById(id: Int) {
        viewModelScope.launch {
            _selectedItem.value = itemRepository.getItemByIdFromLocalDb(id)
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

    fun notifyItemOwner(itemId: Int) {
        viewModelScope.launch {
            try {
                itemRepository.sendInteractionNotification(itemId)

                println("Sucesso: Solicitação de notificação enviada para o item $itemId.")
            } catch (e: Exception) {
                setErrorMessage("Falha ao notificar: ${e.message}")
                println("Erro ao enviar notificação: ${e.message}")
            }
        }
    }

}