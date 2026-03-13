package com.example.citymoodscanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.citymoodscanner.data.repository.EnvironmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the map screen.
 * Handles map-tap events, triggers data fetching, and exposes [UiState].
 */
class MapViewModel(
    private val repository: EnvironmentRepository,
    private val owmApiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    /**
     * Called when the user taps a location on the map.
     * Fetches environmental data and emits the result via [uiState].
     */
    fun onMapTapped(lat: Double, lng: Double) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val data = repository.getEnvironmentData(lat, lng, owmApiKey)
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    e.localizedMessage ?: "Failed to fetch environmental data"
                )
            }
        }
    }

    /**
     * Retry the last request (re-triggers the same coordinates).
     */
    fun retry(lat: Double, lng: Double) {
        onMapTapped(lat, lng)
    }

    /**
     * Factory for creating [MapViewModel] with constructor dependencies.
     */
    class Factory(
        private val repository: EnvironmentRepository,
        private val owmApiKey: String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                return MapViewModel(repository, owmApiKey) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
