package com.app.weathernow.data

import com.app.weathernow.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface WeatherResult {
    data class Success(val data: WeatherResponse) : WeatherResult
    data class Error(val message: String) : WeatherResult
    data object Loading : WeatherResult
}

class WeatherRepository(
    private val api: WeatherApi
) {
    suspend fun getCurrentWeather(city: String): WeatherResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getCurrentWeatherByCity(
                city = city,
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            )
            WeatherResult.Success(response)
        } catch (e: Exception) {
            WeatherResult.Error(e.message ?: "Unknown error")
        }
    }
}

