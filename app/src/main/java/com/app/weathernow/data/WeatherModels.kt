package com.app.weathernow.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val cityName: String?,
    @SerializedName("weather") val weather: List<WeatherDescription>?,
    @SerializedName("main") val main: MainInfo?,
    @SerializedName("wind") val wind: WindInfo?
)

data class WeatherDescription(
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)

data class MainInfo(
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("humidity") val humidity: Int?
)

data class WindInfo(
    @SerializedName("speed") val speed: Double?
)

