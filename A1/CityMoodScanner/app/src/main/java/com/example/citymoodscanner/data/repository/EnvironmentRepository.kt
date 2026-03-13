package com.example.citymoodscanner.data.repository

import com.example.citymoodscanner.data.model.EnvironmentData
import com.example.citymoodscanner.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Repository that fetches weather, air quality and geocoding data in parallel
 * and combines them into a single [EnvironmentData] domain model.
 */
class EnvironmentRepository {

    private val weatherApi = RetrofitClient.weatherApi
    private val airQualityApi = RetrofitClient.airQualityApi
    private val geocodingApi = RetrofitClient.geocodingApi

    /**
     * Fetch environmental data for the given coordinates.
     * All three API calls run concurrently.
     */
    suspend fun getEnvironmentData(
        lat: Double,
        lng: Double,
        apiKey: String
    ): EnvironmentData = coroutineScope {

        val weatherDeferred = async { weatherApi.getCurrentWeather(lat, lng, apiKey) }
        val aqiDeferred = async { airQualityApi.getAirQuality(lat, lng, apiKey) }
        val geoDeferred = async { geocodingApi.reverseGeocode(lat, lng, apiKey = apiKey) }

        val weather = weatherDeferred.await()
        val aqi = aqiDeferred.await()
        val geo = geoDeferred.await()

        val locationName = if (geo.isNotEmpty()) {
            buildString {
                append(geo[0].name)
                geo[0].state?.let { append(", $it") }
                append(", ${geo[0].country}")
            }
        } else {
            "Unknown location"
        }

        val aqiEntry = aqi.list.firstOrNull()
        val aqiLevel = aqiEntry?.main?.aqi ?: 0
        val aqiLabel = mapAqiLabel(aqiLevel)

        val noiseDb = estimateNoise(weather.main.pop(), aqiLevel)
        val noiseLabel = mapNoiseLabel(noiseDb)

        val moodEmoji = computeMoodEmoji(weather.main.temp, aqiLevel, noiseDb)
        val moodLabel = computeMoodLabel(weather.main.temp, aqiLevel, noiseDb)

        EnvironmentData(
            locationName = locationName,
            latitude = lat,
            longitude = lng,
            temperatureCelsius = weather.main.temp,
            feelsLikeCelsius = weather.main.feelsLike,
            weatherDescription = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                ?: "N/A",
            weatherIcon = weather.weather.firstOrNull()?.icon ?: "01d",
            humidity = weather.main.humidity,
            windSpeed = weather.wind.speed,
            pressure = weather.main.pressure,
            aqiLevel = aqiLevel,
            aqiLabel = aqiLabel,
            pm25 = aqiEntry?.components?.pm25 ?: 0.0,
            pm10 = aqiEntry?.components?.pm10 ?: 0.0,
            noiseDb = noiseDb,
            noiseLabel = noiseLabel,
            moodEmoji = moodEmoji,
            moodLabel = moodLabel
        )
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun mapAqiLabel(aqi: Int): String = when (aqi) {
        1 -> "Good"
        2 -> "Fair"
        3 -> "Moderate"
        4 -> "Poor"
        5 -> "Very Poor"
        else -> "Unknown"
    }

    /**
     * Simulate a noise level based on population-density heuristics.
     * Uses humidity as a rough proxy for urban density + AQI as a pollution/activity proxy.
     */
    private fun estimateNoise(humidity: Int, aqiLevel: Int): Int {
        val base = 30 // rural baseline dB
        val humidityFactor = (humidity / 100.0 * 20).toInt()   // 0-20 dB contribution
        val aqiFactor = (aqiLevel - 1) * 8                      // 0-32 dB contribution
        val noise = base + humidityFactor + aqiFactor
        return noise.coerceIn(25, 90)
    }

    /**
     * Extension to use humidity as a population proxy. The OWM weather response
     * doesn't have population, so we re-use humidity creatively.
     */
    @Suppress("unused")
    private fun com.example.citymoodscanner.data.model.MainInfo.pop(): Int = this.humidity

    private fun mapNoiseLabel(db: Int): String = when {
        db < 40 -> "Quiet"
        db < 55 -> "Moderate"
        db < 70 -> "Loud"
        else -> "Very Loud"
    }

    private fun computeMoodEmoji(temp: Double, aqi: Int, noise: Int): String {
        val score = computeComfortScore(temp, aqi, noise)
        return when {
            score >= 80 -> "😊"
            score >= 60 -> "🙂"
            score >= 40 -> "😐"
            score >= 20 -> "😟"
            else -> "😫"
        }
    }

    private fun computeMoodLabel(temp: Double, aqi: Int, noise: Int): String {
        val score = computeComfortScore(temp, aqi, noise)
        return when {
            score >= 80 -> "Excellent"
            score >= 60 -> "Good"
            score >= 40 -> "Fair"
            score >= 20 -> "Poor"
            else -> "Very Poor"
        }
    }

    /**
     * Computes a 0-100 comfort score from temperature, AQI, and noise.
     * 100 = perfect comfort, 0 = extreme discomfort.
     */
    private fun computeComfortScore(temp: Double, aqi: Int, noise: Int): Int {
        // Temperature: ideal range 18-25 °C → 100, deviations penalized
        val tempScore = (100 - (kotlin.math.abs(temp - 21.5) * 5).toInt()).coerceIn(0, 100)

        // AQI: 1→100, 5→0
        val aqiScore = ((5 - aqi) * 25).coerceIn(0, 100)

        // Noise: <30→100, >80→0
        val noiseScore = ((90 - noise) * 2).coerceIn(0, 100)

        return (tempScore * 0.35 + aqiScore * 0.35 + noiseScore * 0.30).toInt()
    }
}
