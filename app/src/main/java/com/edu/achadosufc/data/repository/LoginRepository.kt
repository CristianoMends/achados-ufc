package com.edu.achadosufc.data.repository

import android.content.Context
import com.edu.achadosufc.data.SessionManager
import com.edu.achadosufc.data.model.Login
import com.edu.achadosufc.data.model.LoginResponse
import com.edu.achadosufc.data.service.AuthService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class LoginRepository(
    context: Context,
    private val api: AuthService
) {
    private val sessionManager: SessionManager = SessionManager(context)


    suspend fun login(
        username: String,
        password: String,
    ): LoginResponse? {

        val response = api.login(Login(username, password))

        val loginResponse = response.body()

        if (loginResponse != null) {
            sessionManager.saveAuthToken(loginResponse.access_token)
        }

        return loginResponse

    }
}