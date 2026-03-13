package com.example.citymoodscanner.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit client for OpenWeatherMap APIs.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.openweathermap.org/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApi: WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    val airQualityApi: AirQualityApiService =
        retrofit.create(AirQualityApiService::class.java)

    val geocodingApi: GeocodingApiService =
        retrofit.create(GeocodingApiService::class.java)
}
