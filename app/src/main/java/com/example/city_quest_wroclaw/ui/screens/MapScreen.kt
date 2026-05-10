package com.example.city_quest_wroclaw.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    viewModel: CityQuestViewModel,
    onAttractionClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val attractions by viewModel.attractions.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    var isMapCenteredOnUser by remember { mutableStateOf(false) }

    val mapView = remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(51.107885, 17.038538)) // Wroclaw center
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    LaunchedEffect(attractions, currentLocation) {
        mapView.overlays.clear()

        // Draw attractions
        attractions.forEach { attraction ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(attraction.latitude, attraction.longitude)
            marker.title = attraction.name
            marker.setOnMarkerClickListener { _, _ ->
                onAttractionClick(attraction.id)
                true
            }
            mapView.overlays.add(marker)
            marker.showInfoWindow() // Show title persistently
        }

        // Draw user location
        currentLocation?.let { loc ->
            val userMarker = Marker(mapView)
            userMarker.position = GeoPoint(loc.latitude, loc.longitude)
            userMarker.title = "Twoja lokalizacja"
            mapView.overlays.add(userMarker)
            
            if (!isMapCenteredOnUser) {
                mapView.controller.animateTo(userMarker.position)
                isMapCenteredOnUser = true
            }
        }
        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
