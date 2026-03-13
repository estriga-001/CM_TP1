package com.example.citymoodscanner.data.remote

import com.example.citymoodscanner.data.model.AirQualityResponse
import com.example.citymoodscanner.data.model.GeocodingResponse
import com.example.citymoodscanner.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service for the OpenWeatherMap current-weather endpoint.
 */
interface WeatherApiService {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

/**
 * Retrofit service for the OpenWeatherMap air-pollution endpoint.
 */
interface AirQualityApiService {

    @GET("data/2.5/air_pollution")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): AirQualityResponse
}

/**
 * Retrofit service for OpenWeatherMap reverse-geocoding.
 */
interface GeocodingApiService {

    @GET("geo/1.0/reverse")
    suspend fun reverseGeocode(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): List<GeocodingResponse>
}
