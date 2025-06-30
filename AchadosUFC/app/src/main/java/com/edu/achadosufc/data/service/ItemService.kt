package com.edu.achadosufc.data.service

import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.ItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST


interface ItemService {
    @GET("items")
    suspend fun getAll(): Response<List<Item>>

    @Multipart
    @POST("items")
    suspend fun create(@Body item: ItemRequest): Response<Item>?

}