package com.app.weathernow.data

import android.content.Context
import android.content.SharedPreferences

class CityPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveCity(city: String) {
        prefs.edit().putString(KEY_CITY, city).apply()
    }

    fun getCity(): String {
        return prefs.getString(KEY_CITY, DEFAULT_CITY) ?: DEFAULT_CITY
    }

    companion object {
        private const val PREFS_NAME = "weather_prefs"
        private const val KEY_CITY = "selected_city"
        private const val DEFAULT_CITY = "Chernihiv"
    }
}
