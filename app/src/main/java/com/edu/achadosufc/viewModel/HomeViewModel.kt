package com.edu.achadosufc.viewModel

import androidx.lifecycle.ViewModel
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    suspend fun getItems() {
        _items.value = itemRepository.getAllItems()
    }
}
