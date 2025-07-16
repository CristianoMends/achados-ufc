package com.edu.achadosufc.data


import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val AUTH_TOKEN = "auth_token"
        private const val USER_ID = "user_id"
        private const val TOKEN_EXPIRATION_TIME = "token_expiration_time"
    }

    fun saveAuthToken(token: String) {
        prefs.edit() {
            putString(AUTH_TOKEN, token)
        }
    }

    fun saveUserLoggedIn(userId: Int) {
        prefs.edit() {
            putInt("user_id", userId)
        }
    }

    fun saveExpirationTime(expirationTimeInMillis: Long) {
        prefs.edit { putLong(TOKEN_EXPIRATION_TIME, expirationTimeInMillis) }
    }

    fun isTokenExpired(): Boolean {
        return false
    }

    fun fetchUserLoggedIn(): Int {
        return prefs.getInt("user_id", -1)
    }

    fun clearSession() {
        prefs.edit {
            remove(AUTH_TOKEN)
            remove(USER_ID)
            remove(TOKEN_EXPIRATION_TIME)
        }
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }
}