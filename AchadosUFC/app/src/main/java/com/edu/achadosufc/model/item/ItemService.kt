package com.edu.achadosufc.model.item

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ItemService {
    @GET("items")
    suspend fun getAll(): Response<List<Item>>

    @Multipart
    @POST("items")
    fun create(
        @Part("file") file: RequestBody,
        @Part("description") description: RequestBody,
        @Part("title") title: RequestBody,
        @Part("location") location: RequestBody,
        @Part("isFound") isFound: RequestBody,
        @Part("userId") userId: RequestBody
    ): Response<Item>?
}