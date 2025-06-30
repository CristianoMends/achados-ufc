package com.edu.achadosufc.data.repository

import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.ItemRequest
import com.edu.achadosufc.data.service.ItemService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemRepository {
    private val api: ItemService
    private val items: MutableList<Item> = mutableListOf()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://achados-ufc-api-hch7.vercel.app/")
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

    suspend fun create(item: ItemRequest) {

        try {
            val res = this.api.create(item)

            if (res != null) {
                if (res.isSuccessful) {
                    val responseBody = res.body()
                    responseBody?.let {
                        this.items.add(it)
                    }
                    responseBody ?: throw Exception("Item creation failed")
                } else {
                    val errorBody = res?.errorBody()?.string()
                    throw Exception("Error creating item: $errorBody")
                }
            }
        } catch (e: Exception) {
            throw Exception("Error creating item: ${e.message}")
        }
    }

    suspend fun getAllByUserId(userId: Int): List<Item> {
        val items = getAllItems()
        return items.filter { it.user.id == userId }
    }

    fun sendInteractionNotification(itemId: Int) {

    }

}