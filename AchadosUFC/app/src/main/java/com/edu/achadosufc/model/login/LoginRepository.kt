package com.edu.achadosufc.model.login

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginRepository {
    private val api: AuthService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(AuthService::class.java)
    }

    suspend fun login(
        username: String,
        password: String,
    ): LoginResponse? {

        try {
            val response = api.login(Login(username, password))

            if (response.isSuccessful) {
                val loginResponse = response.body()

                return loginResponse
            } else {
                val errorBody = response.errorBody()?.string()

                return null
            }
        } catch (e: Exception) {

            return null
        }

    }
}