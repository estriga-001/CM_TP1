package com.example.citymoodscanner.ui

import com.example.citymoodscanner.data.model.EnvironmentData

/**
 * Sealed interface representing the UI state of the map screen.
 */
sealed interface UiState {

    /** Initial state — no location tapped yet. */
    data object Idle : UiState

    /** A location was tapped and data is being fetched. */
    data object Loading : UiState

    /** Data fetched successfully. */
    data class Success(val data: EnvironmentData) : UiState

    /** An error occurred while fetching data. */
    data class Error(val message: String) : UiState
}
