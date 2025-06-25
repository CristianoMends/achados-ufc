package com.edu.achadosufc.model.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private object PreferencesKeys {
        val KEEP_LOGGED_IN = booleanPreferencesKey("keep_logged_in")
        val USER_ID = intPreferencesKey("user_id")
    }

    val keepLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.KEEP_LOGGED_IN] ?: false
        }

    val userId: Flow<Int?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    suspend fun updateKeepLoggedIn(value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEEP_LOGGED_IN] = value
        }
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_ID] = userId
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.USER_ID)
        }
    }
}