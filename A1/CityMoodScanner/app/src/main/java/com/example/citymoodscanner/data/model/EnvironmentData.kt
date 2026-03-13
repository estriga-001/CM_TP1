package com.example.citymoodscanner.data.model

/**
 * Unified domain model that aggregates all environmental data for a tapped location.
 */
data class EnvironmentData(
    // Location
    val locationName: String,
    val latitude: Double,
    val longitude: Double,

    // Weather
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val humidity: Int,           // percentage
    val windSpeed: Double,      // m/s
    val pressure: Int,          // hPa

    // Air Quality
    val aqiLevel: Int,          // 1-5
    val aqiLabel: String,       // "Good", "Fair", "Moderate", "Poor", "Very Poor"
    val pm25: Double,
    val pm10: Double,

    // Noise (simulated)
    val noiseDb: Int,
    val noiseLabel: String,     // "Quiet", "Moderate", "Loud", "Very Loud"

    // Overall mood
    val moodEmoji: String,
    val moodLabel: String
)
