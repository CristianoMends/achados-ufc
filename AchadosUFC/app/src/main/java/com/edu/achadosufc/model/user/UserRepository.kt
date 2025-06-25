package com.edu.achadosufc.model.user

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository {
    private val api: UserService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(UserService::class.java)
    }


    suspend fun findAll(): List<UserResponse>? {

        try {
            val response = api.getAllUsers()

            if (response.isSuccessful) {
                val responseBody = response.body()

                return responseBody
            } else {
                val errorBody = response.errorBody()?.string()
                return null
            }
        } catch (e: Exception) {

            return null
        }
    }

    suspend fun findById(id: Int): UserResponse? {
        try {
            val response = api.getAllUsers()

            if (response.isSuccessful) {
                val responseBody = response.body()?.find { it.id == id }
                return responseBody
            } else {
                val errorBody = response.errorBody()?.string()
                return null
            }
        } catch (e: Exception) {

            return null
        }
    }

    suspend fun findByUsername(username: String): UserResponse? {
        try {
            val response = api.getUserByUsername(username)

            if (response.isSuccessful) {
                val responseBody = response.body()

                return responseBody
            } else {
                val errorBody = response.errorBody()?.string()
                return null
            }
        } catch (e: Exception) {

            return null
        }
    }

    suspend fun createUser(user: UserRequest): UserResponse? {
        try {
            val response = api.createUser(user)

            if (response.isSuccessful) {
                return response.body()
            } else {
                val errorBody = response.errorBody()?.string()
                return null
            }
        } catch (e: Exception) {
            return null
        }
    }
}