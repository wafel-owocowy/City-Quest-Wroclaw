package com.example.city_quest_wroclaw.viewmodel

import android.app.Application
import androidx.preference.PreferenceManager
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.city_quest_wroclaw.data.AppDatabase
import com.example.city_quest_wroclaw.data.Attraction
import com.example.city_quest_wroclaw.location.LocationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class CityQuestViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        // PROMIEŃ ZALICZENIA ATRAKCJI (W METRACH) - ZMIEŃ TĘ WARTOŚĆ DLA TESTÓW
        const val VISIT_RADIUS_METERS = 10000.0
    }

    private val db = AppDatabase.getDatabase(application)
    private val attractionDao = db.attractionDao()
    private val locationService = LocationService(application)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    // Theme State Persistence (StateFlow to reactively update Compose UI)
    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (sharedPreferences.contains("dark_mode")) {
            sharedPreferences.getBoolean("dark_mode", false)
        } else {
            null // system default
        }
    )
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
    }

    val attractions: StateFlow<List<Attraction>> = attractionDao.getAllAttractions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    // Map State Persistence
    var lastMapCenterLat: Double? = null
    var lastMapCenterLon: Double? = null
    var lastMapZoom: Double = 15.0
    var shouldCenterOnUser: Boolean = true
        private set

    fun saveMapState(lat: Double, lon: Double, zoom: Double) {
        lastMapCenterLat = lat
        lastMapCenterLon = lon
        lastMapZoom = zoom
    }

    fun resetMapCentering() {
        shouldCenterOnUser = true
    }

    fun markMapCentered() {
        shouldCenterOnUser = false
    }

    private var locationJob: Job? = null

    fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        locationJob = viewModelScope.launch {
            locationService.getLocationFlow().collect { location ->
                _currentLocation.value = location
                checkDistanceToAttractions(location)
            }
        }
    }

    private fun checkDistanceToAttractions(userLocation: Location) {
        val currentAttractions = attractions.value
        currentAttractions.forEach { attraction ->
            if (!attraction.isVisited) {
                val results = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    attraction.latitude, attraction.longitude,
                    results
                )
                val distanceInMeters = results[0]
                if (distanceInMeters <= VISIT_RADIUS_METERS) {
                    markAsVisited(attraction.id)
                }
            }
        }
    }

    private fun markAsVisited(id: Int) {
        viewModelScope.launch {
            attractionDao.updateVisitedStatus(id, true)
        }
    }
}
