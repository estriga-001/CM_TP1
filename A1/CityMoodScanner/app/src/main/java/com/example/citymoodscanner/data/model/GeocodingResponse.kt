package com.example.citymoodscanner.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for OpenWeatherMap /geo/1.0/reverse response.
 * The API returns a JSON array of location objects.
 */
data class GeocodingResponse(
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? = null
)
