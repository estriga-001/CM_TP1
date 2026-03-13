package com.example.citymoodscanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.citymoodscanner.data.repository.EnvironmentRepository
import com.example.citymoodscanner.ui.MapViewModel
import com.example.citymoodscanner.ui.UiState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var viewModel: MapViewModel
    private lateinit var googleMap: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    // Bottom sheet views
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var loadingContainer: LinearLayout
    private lateinit var errorContainer: LinearLayout
    private lateinit var successContainer: LinearLayout
    private lateinit var errorMessage: TextView
    private lateinit var retryButton: MaterialButton

    // Data views
    private lateinit var moodEmoji: TextView
    private lateinit var locationName: TextView
    private lateinit var moodLabel: TextView
    private lateinit var temperatureValue: TextView
    private lateinit var weatherDescription: TextView
    private lateinit var aqiValue: TextView
    private lateinit var aqiDetails: TextView
    private lateinit var noiseValue: TextView
    private lateinit var noiseLabel: TextView
    private lateinit var windValue: TextView
    private lateinit var humidityValue: TextView
    private lateinit var pressureValue: TextView
    private lateinit var feelsLikeValue: TextView

    // Track the last tapped location for retry
    private var lastTappedLat: Double = 0.0
    private var lastTappedLng: Double = 0.0

    // Location permission launcher
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            enableMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        val apiKey = BuildConfig.OWM_API_KEY
        val factory = MapViewModel.Factory(EnvironmentRepository(), apiKey)
        viewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        // Bind views
        bindViews()

        // Set up Bottom Sheet
        setupBottomSheet()

        // Set up map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Observe UI state
        observeUiState()

        // Show a hint toast
        Toast.makeText(this, getString(R.string.tap_to_scan), Toast.LENGTH_LONG).show()
    }

    private fun bindViews() {
        bottomSheetContainer = findViewById(R.id.bottomSheetContainer)
        loadingContainer = findViewById(R.id.loadingContainer)
        errorContainer = findViewById(R.id.errorContainer)
        successContainer = findViewById(R.id.successContainer)
        errorMessage = findViewById(R.id.errorMessage)
        retryButton = findViewById(R.id.retryButton)
        moodEmoji = findViewById(R.id.moodEmoji)
        locationName = findViewById(R.id.locationName)
        moodLabel = findViewById(R.id.moodLabel)
        temperatureValue = findViewById(R.id.temperatureValue)
        weatherDescription = findViewById(R.id.weatherDescription)
        aqiValue = findViewById(R.id.aqiValue)
        aqiDetails = findViewById(R.id.aqiDetails)
        noiseValue = findViewById(R.id.noiseValue)
        noiseLabel = findViewById(R.id.noiseLabel)
        windValue = findViewById(R.id.windValue)
        humidityValue = findViewById(R.id.humidityValue)
        pressureValue = findViewById(R.id.pressureValue)
        feelsLikeValue = findViewById(R.id.feelsLikeValue)

        retryButton.setOnClickListener {
            viewModel.retry(lastTappedLat, lastTappedLng)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.skipCollapsed = true
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Default camera: Europe overview
        val defaultLocation = LatLng(40.0, -8.0) // Portugal
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 6f))

        // Map click listener
        googleMap.setOnMapClickListener { latLng ->
            lastTappedLat = latLng.latitude
            lastTappedLng = latLng.longitude

            // Drop a marker
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Scanning…")
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))

            // Trigger data fetch
            viewModel.onMapTapped(latLng.latitude, latLng.longitude)
        }

        // Request location permission
        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                enableMyLocation()
            }
            else -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @Suppress("MissingPermission")
    private fun enableMyLocation() {
        if (::googleMap.isInitialized) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Idle -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        is UiState.Loading -> {
                            showBottomSheetState(loading = true)
                        }
                        is UiState.Success -> {
                            showBottomSheetState(success = true)
                            bindEnvironmentData(state)
                        }
                        is UiState.Error -> {
                            showBottomSheetState(error = true)
                            errorMessage.text = state.message
                        }
                    }
                }
            }
        }
    }

    private fun showBottomSheetState(
        loading: Boolean = false,
        success: Boolean = false,
        error: Boolean = false
    ) {
        loadingContainer.visibility = if (loading) View.VISIBLE else View.GONE
        successContainer.visibility = if (success) View.VISIBLE else View.GONE
        errorContainer.visibility = if (error) View.VISIBLE else View.GONE

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun bindEnvironmentData(state: UiState.Success) {
        val data = state.data

        // Header
        moodEmoji.text = data.moodEmoji
        locationName.text = data.locationName
        moodLabel.text = getString(R.string.mood_format, data.moodLabel)

        // Temperature
        temperatureValue.text = getString(R.string.temp_format, data.temperatureCelsius)
        weatherDescription.text = data.weatherDescription

        // Air Quality
        aqiValue.text = data.aqiLabel
        aqiDetails.text = getString(R.string.pm_details_format, data.pm25, data.pm10)

        // Noise
        noiseValue.text = getString(R.string.noise_format, data.noiseDb)
        noiseLabel.text = getString(R.string.noise_detail_format, data.noiseLabel)

        // General Conditions
        windValue.text = getString(R.string.wind_format, data.windSpeed)
        humidityValue.text = getString(R.string.humidity_format, data.humidity)
        pressureValue.text = getString(R.string.pressure_format, data.pressure)
        feelsLikeValue.text = getString(R.string.temp_format, data.feelsLikeCelsius)

        // Update marker title
        if (::googleMap.isInitialized) {
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions()
                    .position(LatLng(data.latitude, data.longitude))
                    .title("${data.moodEmoji} ${data.locationName}")
                    .snippet("${data.temperatureCelsius.toInt()}°C · AQI: ${data.aqiLabel}")
            )
        }
    }
}