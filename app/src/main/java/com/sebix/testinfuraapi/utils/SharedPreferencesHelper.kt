package com.sebix.testinfuraapi.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(private val context: Context) {
    fun saveString(key: String, value: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("wallet", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("wallet", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}