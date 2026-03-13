package com.example.citymoodscanner.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for OpenWeatherMap /data/2.5/weather response.
 */
data class WeatherResponse(
    @SerializedName("weather") val weather: List<WeatherInfo>,
    @SerializedName("main") val main: MainInfo,
    @SerializedName("wind") val wind: WindInfo,
    @SerializedName("name") val cityName: String
)

data class WeatherInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)

data class MainInfo(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("pressure") val pressure: Int
)

data class WindInfo(
    @SerializedName("speed") val speed: Double
)
