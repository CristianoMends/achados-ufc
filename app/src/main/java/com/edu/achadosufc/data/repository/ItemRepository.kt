package com.edu.achadosufc.data.repository

import android.util.Log
import com.edu.achadosufc.data.dao.ItemDao
import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.toItem
import com.edu.achadosufc.data.model.toItemEntity
import com.edu.achadosufc.data.service.ItemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.UnknownHostException

class ItemRepository(
    private val api: ItemService,
    private val itemDao: ItemDao
) {

    fun getAllItemsFromLocalDb(): Flow<List<Item>> {
        return itemDao.getAllItems()
            .map { itemEntities ->
                itemEntities.map { it.toItem() }
            }
    }

    suspend fun fetchAndSaveAllItems() {
        try {
            val response = api.getAll()
            if (response.isSuccessful) {
                val itemsFromApi = response.body() ?: emptyList()
                val itemEntities = itemsFromApi.map { it.toItemEntity() }
                itemDao.insertAllItems(itemEntities)
            } else {
                val errorBody = response.errorBody()?.string()
                throw HttpException(response)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun fetchItemByIdAndSave(itemId: Int): Item? {
        try {
            val response = api.getItemById(itemId)
            if (response != null && response.isSuccessful) {
                val itemFromApi = response.body()
                itemFromApi?.let {
                    itemDao.insertItem(it.toItemEntity())
                    return it
                }
            }
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error fetching item by ID and saving: ${e.message}")
        }
        return null
    }


    suspend fun getItemById(itemId: Int): Item? {
        val localItem = itemDao.getItemById(itemId)?.toItem()
        if (localItem != null) {
            return localItem
        }

        try {
            val response = api.getItemById(itemId)
            if (response != null) {
                if (response.isSuccessful) {
                    val itemFromApi = response.body()
                    itemFromApi?.let {
                        itemDao.insertItem(it.toItemEntity())
                        return it
                    } ?: run {
                        return null
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    throw HttpException(response)
                }
            }
        } catch (e: UnknownHostException) {
            return null
        } catch (e: Exception) {
            throw e
        }
        return null
    }

    fun getItemsByUserIdFromLocalDb(userId: Int): Flow<List<Item>> {
        return itemDao.getItemsByUserId(userId).map { entities ->
            entities.map { it.toItem() }
        }
    }

    suspend fun getItemByIdFromLocalDb(itemId: Int): Item? {
        return itemDao.getItemById(itemId)?.toItem()
    }

    suspend fun clearLocalDatabase(){
        try {
            itemDao.clearAllItems()
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error clearing local database: ${e.message}")
            throw e
        }
    }

    suspend fun fetchAndSaveItemsByUserId(userId: Int) {
        try {
            val res = this.api.getAll()

            if (res.isSuccessful) {
                val itemsFromApi = res.body()?.filter { it.user.id == userId }
                val itemEntities = itemsFromApi?.map { it.toItemEntity() }
                if (itemEntities != null) {
                    itemDao.insertAllItems(itemEntities)
                }

            } else {
                val errorBody = "Erro fetching items for user $userId: No items found"
                throw HttpException(
                    retrofit2.Response.error<List<Item>>(
                        404,
                        okhttp3.ResponseBody.create(null, errorBody)
                    )
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }
    /*
        suspend fun getItemById(itemId: Int): Item {
            val items = getAllItems()
            if (items.isEmpty()) {
                throw Exception("No items available. Please fetch items first.")
            }
            return items.firstOrNull { it.id == itemId }
                ?: throw NoSuchElementException("Item with id $itemId not found")
        }*/
    /*
        suspend fun getAllItemsByApi(): List<Item> {
            try {
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
        }*/

    suspend fun create(
        token: String,
        title: String,
        description: String,
        location: String,
        file: MultipartBody.Part,
        isFound: Boolean,
    ) {

        val response = api.create(
            token = token,
            title = title.toRequestBody(MultipartBody.FORM),
            description = description.toRequestBody(MultipartBody.FORM),
            location = location.toRequestBody(MultipartBody.FORM),
            file = file,
            isFound = isFound
        )

        if (response != null) {
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Falha ao criar o item: resposta vazia")
            } else {
                val errorBody = response.errorBody()?.string()
                throw HttpException(response)
            }
        }
    }

    /*
        suspend fun getAllByUserId(userId: Int): List<Item> {
            val items = getAllItemsByApi()
            return items.filter { it.user.id == userId }
        }*/

    fun sendInteractionNotification(itemId: Int) {
        Log.i(
            "ItemRepository",
            "Notificação de interação para item $itemId (funcionalidade não implementada no backend)"
        )
    }


}