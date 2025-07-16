package com.edu.achadosufc.data.service

import com.edu.achadosufc.data.model.Item
import com.edu.achadosufc.data.model.ItemRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ItemService {
    @GET("items")
    suspend fun getAll(): Response<List<Item>>

    @Multipart
    @POST("items")
    suspend fun create(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("location") location: RequestBody,
        @Part file: okhttp3.MultipartBody.Part,
        @Part("isFound") isFound: Boolean
    ): Response<Item>?

    @GET("items/{itemId}")
    suspend fun getItemById(
        @Path("itemId") itemId: Int
    ): Response<Item>?

}