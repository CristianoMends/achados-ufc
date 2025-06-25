package com.edu.achadosufc.model.item

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemRepository {
    private val api: ItemService
    private val items: MutableList<Item> = mutableListOf()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ItemService::class.java)
    }


    suspend fun getItemById(itemId: Int): Item {
        val items = getAllItems()
        if (items.isEmpty()) {
            throw Exception("No items available. Please fetch items first.")
        }
        return items.firstOrNull { it.id == itemId }
            ?: throw NoSuchElementException("Item with id $itemId not found")
    }

    suspend fun getAllItems(): List<Item> {
        try {
            if (this.items.isNotEmpty()) {
                return this.items
            }
            val res = this.api.getAll()

            if (res.isSuccessful) {
                val responseBody = res.body()
                responseBody?.let {
                    this.items.clear()
                    this.items.addAll(it)
                }
                return responseBody ?: listOf()
            } else {
                val errorBody = res.errorBody()?.string()
                throw Exception("Error fetching items: $errorBody")
            }
        } catch (e: Exception) {
            throw Exception("Error fetching items: ${e.message}")
        }
    }

    suspend fun getAllByUserId(userId: Int): List<Item> {
        val items = getAllItems()
        return items.filter { it.user.id == userId }
    }

}