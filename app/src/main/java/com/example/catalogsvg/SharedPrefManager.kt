package com.example.catalogsvg

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    fun saveListOfIdDocVsd(list: Map<String, String>) {
        val gson = Gson()
        val json = gson.toJson(list)
        sharedPreferences.edit().putString("listOfIdDocVsd", json).apply()
    }
    fun getListOfIdDocVsd(): Map<String, String> {
        val gson = Gson()
        val json = sharedPreferences.getString("listOfIdDocVsd", null)
        return if (json != null) {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyMap()
        }
    }
}
