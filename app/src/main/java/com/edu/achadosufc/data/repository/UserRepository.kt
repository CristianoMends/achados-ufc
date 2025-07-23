package com.edu.achadosufc.data.repository

import android.util.Log
import com.edu.achadosufc.data.dao.UserDao
import com.edu.achadosufc.data.model.UserRequest
import com.edu.achadosufc.data.model.UserResponse
import com.edu.achadosufc.data.model.toUserEntity
import com.edu.achadosufc.data.model.toUserResponse
import com.edu.achadosufc.data.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.net.UnknownHostException

class UserRepository(
    private val apiService: UserService,
    private val userDao: UserDao
) {

    fun getUserByIdLocal(userId: Int): Flow<UserResponse?> {
        return userDao.getUserById(userId).map { it?.toUserResponse() }
    }

    fun getUserByUsernameLocal(username: String): Flow<UserResponse?> {
        return userDao.getUserByUsername(username).map { it?.toUserResponse() }
    }

    suspend fun clearLocalDatabase() {
        try {
            userDao.clearAllUsers()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error clearing local database: ${e.message}")
            throw e
        }
    }

    suspend fun fetchUserByEmailAndSave(email: String) {
        try {
            val response = apiService.getUserByEmail(email)
            if (response.isSuccessful) {
                val userFromApi = response.body()
                userFromApi?.let {
                    userDao.insertUser(it.toUserEntity())
                } ?: run {
                    Log.e("UserRepository", "No user found with email: $email")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                throw HttpException(response)
            }
        } catch (e: UnknownHostException) {
            Log.e("UserRepository", "Network error: ${e.message}")
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by email: ${e.message}")
            throw e
        }
    }

    suspend fun getUserByEmailLocal(email: String): UserResponse? {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            userEntity?.toUserResponse()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user by email locally: ${e.message}")
            null
        }
    }

    suspend fun fetchUserByIdAndSave(userId: Int): UserResponse? {
        try {

            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                val usersFromApi = response.body() ?: emptyList()
                val userToSave = usersFromApi.find { it.id == userId }
                userToSave?.let {
                    userDao.insertUser(it.toUserEntity())

                    return it
                } ?: run {
                    return null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                throw HttpException(response)
            }
        } catch (e: UnknownHostException) {
            return null
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun fetchUserByUsernameAndSave(username: String): UserResponse? {
        try {

            val response = apiService.getUserByEmail(username)
            if (response.isSuccessful) {
                val userFromApi = response.body()
                userFromApi?.let {
                    userDao.insertUser(it.toUserEntity())

                    return it
                } ?: run {
                    return null
                }
            } else {
                val errorBody = response.errorBody()?.string()
                throw HttpException(response)
            }
        } catch (e: UnknownHostException) {
            return null
        } catch (e: Exception) {
            throw e
        }
    }


    suspend fun createUser(userRequest: UserRequest): UserResponse? {

        val response = apiService.createUser(userRequest)
        if (response.isSuccessful) {
            val createdUser = response.body()
            createdUser?.let {
                userDao.insertUser(it.toUserEntity())
            }
            return createdUser
        } else {
            val errorBody = response.errorBody()?.string()
            throw HttpException(response)
        }
    }
}