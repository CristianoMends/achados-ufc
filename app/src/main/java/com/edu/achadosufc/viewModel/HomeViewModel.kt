package com.edu.achadosufc.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class HomeViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            itemRepository.getAllItemsFromLocalDb()
                .catch { e ->
                    _errorMessage.value = "Erro ao ler itens do banco de dados local: ${e.message}"
                    Log.e("HomeViewModel", "Erro ao ler DB local:", e)
                    _items.value = emptyList()
                }
                .collectLatest { fetchedItems ->
                    _items.value = fetchedItems.sortedByDescending { it.id }
                    Log.d("HomeViewModel", "Itens atualizados do DB local: ${fetchedItems.size} itens.")

                    if (fetchedItems.isEmpty() && !_isLoading.value) {
                        fetchItemsFromNetwork()
                    }
                }
        }
    }

    fun fetchItemsFromNetwork() {
        if (_isLoading.value) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                itemRepository.fetchAndSaveAllItems()

                if (_items.value.isEmpty()) {
                    _errorMessage.value = "Nenhum item encontrado online."
                }
            } catch (e: UnknownHostException) {
                _errorMessage.value = "Sem conexão com a internet. Exibindo dados locais, se houver."
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar itens da rede: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}