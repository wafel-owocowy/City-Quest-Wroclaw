package com.example.city_quest_wroclaw.ui.screens

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.city_quest_wroclaw.R
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

    val mapView = remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            
            // Restore saved map state or default to Wroclaw
            val savedLat = viewModel.lastMapCenterLat
            val savedLon = viewModel.lastMapCenterLon
            if (savedLat != null && savedLon != null) {
                controller.setZoom(viewModel.lastMapZoom)
                controller.setCenter(GeoPoint(savedLat, savedLon))
            } else {
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(51.107885, 17.038538)) // Wroclaw center
            }
            
            // Save map state when user drags or zooms
            addMapListener(object : org.osmdroid.events.MapListener {
                override fun onScroll(event: org.osmdroid.events.ScrollEvent?): Boolean {
                    event?.source?.let { map ->
                        val center = map.mapCenter
                        viewModel.saveMapState(center.latitude, center.longitude, map.zoomLevelDouble)
                    }
                    return true
                }

                override fun onZoom(event: org.osmdroid.events.ZoomEvent?): Boolean {
                    event?.source?.let { map ->
                        val center = map.mapCenter
                        viewModel.saveMapState(center.latitude, center.longitude, map.zoomLevelDouble)
                    }
                    return true
                }
            })
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
    val user_marker_title = stringResource(R.string.user_marker)
    LaunchedEffect(attractions, currentLocation) {
        mapView.overlays.clear()

        // Draw attractions
        attractions.forEach { attraction ->
            val marker = Marker(mapView)
            marker.position = GeoPoint(attraction.latitude, attraction.longitude)
            marker.title = attraction.name
            
            val iconRes = if (attraction.isVisited) {
                R.drawable.ic_attraction_visited
            } else {
                R.drawable.ic_attraction_unvisited
            }
            marker.icon = ContextCompat.getDrawable(context, iconRes)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

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
            userMarker.title = user_marker_title
            
            userMarker.icon = ContextCompat.getDrawable(context, R.drawable.ic_user_location)
            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            
            mapView.overlays.add(userMarker)
            
            if (viewModel.shouldCenterOnUser) {
                mapView.controller.animateTo(userMarker.position)
                viewModel.saveMapState(loc.latitude, loc.longitude, mapView.zoomLevelDouble)
                viewModel.markMapCentered()
            }
        }
        mapView.invalidate()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )
        
        // Floating action button to center map on user
        FloatingActionButton(
            onClick = {
                currentLocation?.let { loc ->
                    val userPoint = GeoPoint(loc.latitude, loc.longitude)
                    mapView.controller.animateTo(userPoint)
                    viewModel.saveMapState(userPoint.latitude, userPoint.longitude, mapView.zoomLevelDouble)
                    viewModel.markMapCentered()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = stringResource(R.string.center_on_user)
            )
        }
    }
}
