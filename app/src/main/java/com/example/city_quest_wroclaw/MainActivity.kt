package com.example.city_quest_wroclaw

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceManager
import com.example.city_quest_wroclaw.ui.Navigation
import com.example.city_quest_wroclaw.ui.theme.CityQuestWroclawTheme
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel

class MainActivity : AppCompatActivity() {
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val viewModel: CityQuestViewModel by viewModels()

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                viewModel.startLocationUpdates()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewModel.startLocationUpdates()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        setContent {
            val isDarkModeState by viewModel.isDarkMode.collectAsState()
            val darkTheme = isDarkModeState ?: isSystemInDarkTheme()

            CityQuestWroclawTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Navigation(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}