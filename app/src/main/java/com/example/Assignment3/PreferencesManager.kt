package com.example.Assignment3

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun saveToken(token: String,id: String) {
        sharedPreferences.edit().putString("USER_TOKEN", token).apply()
        sharedPreferences.edit().putString("USER_ID", id).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("USER_TOKEN", null)
    }

    fun getId(): String? {
        return sharedPreferences.getString("USER_ID", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("USER_TOKEN").apply()
        sharedPreferences.edit().remove("USER_ID").apply()
    }
}
