package com.edu.achadosufc.data.service

import com.edu.achadosufc.data.model.GoogleTokenRequest
import com.edu.achadosufc.data.model.Login
import com.edu.achadosufc.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body request: Login): Response<LoginResponse>

    @POST("auth/google")
    suspend fun loginWithGoogle(@Body tokenRequest: GoogleTokenRequest): Response<LoginResponse>
}
