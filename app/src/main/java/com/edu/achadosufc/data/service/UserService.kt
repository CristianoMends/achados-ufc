package com.edu.achadosufc.data.service

import com.edu.achadosufc.data.model.UserRequest
import com.edu.achadosufc.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {

    @GET("users")
    suspend fun getAllUsers(): Response<List<UserResponse>>

    @GET("users/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): Response<UserResponse>

    @POST("users")
    suspend fun createUser(@Body user: UserRequest): Response<UserResponse>

    @GET("users/search")
    suspend fun getUserByEmail(@Query("email") email: String): Response<UserResponse?>

}