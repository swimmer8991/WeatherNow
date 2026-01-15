package com.app.weathernow.data

import com.app.weathernow.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface WeatherResult<out T> {
    data class Success<T>(val data: T) : WeatherResult<T>
    data class Error(val message: String) : WeatherResult<Nothing>
    data object Loading : WeatherResult<Nothing>
}

class WeatherRepository(
    private val api: WeatherApi
) {
    suspend fun getCurrentWeather(city: String): WeatherResult<WeatherResponse> = withContext(Dispatchers.IO) {
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

    suspend fun getForecast(city: String): WeatherResult<ForecastResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getForecastByCity(
                city = city,
                apiKey = BuildConfig.OPEN_WEATHER_API_KEY
            )
            WeatherResult.Success(response)
        } catch (e: Exception) {
            WeatherResult.Error(e.message ?: "Unknown error")
        }
    }
}

