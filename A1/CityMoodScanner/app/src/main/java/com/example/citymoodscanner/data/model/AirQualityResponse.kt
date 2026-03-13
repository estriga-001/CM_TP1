package com.example.citymoodscanner.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for OpenWeatherMap /data/2.5/air_pollution response.
 */
data class AirQualityResponse(
    @SerializedName("list") val list: List<AirQualityEntry>
)

data class AirQualityEntry(
    @SerializedName("main") val main: AqiMain,
    @SerializedName("components") val components: AqiComponents
)

data class AqiMain(
    @SerializedName("aqi") val aqi: Int  // 1=Good, 2=Fair, 3=Moderate, 4=Poor, 5=Very Poor
)

data class AqiComponents(
    @SerializedName("pm2_5") val pm25: Double,
    @SerializedName("pm10") val pm10: Double,
    @SerializedName("co") val co: Double,
    @SerializedName("no2") val no2: Double,
    @SerializedName("o3") val o3: Double,
    @SerializedName("so2") val so2: Double
)
