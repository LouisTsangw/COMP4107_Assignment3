package com.example.infoday

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreInstance {

    // The DataStore instance, initialized with the "settings" name.
    private val Context.dataStore: DataStore<Preferences>
            by preferencesDataStore(name = "settings")

    // A unique key for boolean settings.
    val DARK_MODE = booleanPreferencesKey("dark_mode")

    suspend fun saveBooleanPreferences(
        context: Context,
        key: Preferences.Key<Boolean>,
        value: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun getBooleanPreferences(context: Context, key: Preferences.Key<Boolean>): Flow<Boolean?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }
}