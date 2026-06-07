package com.binayshaw7777.leaflektsampleapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.binayshaw7777.leaflekt.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.LeaflektController
import com.binayshaw7777.leaflekt.LeaflektLatLng
import com.binayshaw7777.leaflekt.LeaflektMapProperties
import com.binayshaw7777.leaflekt.LeaflektMapStyle
import com.binayshaw7777.leaflekt.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.LeaflektMarkerInfo
import com.binayshaw7777.leaflekt.compose.LeaflektMap
import com.binayshaw7777.leaflekt.compose.LeaflektMarker
import com.binayshaw7777.leaflekt.compose.LeaflektMarkerCluster
import com.binayshaw7777.leaflekt.compose.LeaflektPolyline
import com.binayshaw7777.leaflekt.compose.rememberLeaflektCameraPositionState

@Composable
internal fun SampleAppScreen(onBack: () -> Unit = {}) {
    var selectedTab by rememberSaveable { mutableStateOf(SampleTab.Explore) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(Kolkata, 11.0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                SampleTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Text(tab.shortLabel) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LeaflektMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = LeaflektMapProperties(mapStyle = selectedMapStyle),
                uiSettings = LeaflektMapUiSettings(
                    zoomControlsEnabled = false,
                    showCurrentLocation = true,
                ),
                onReady = { controller = it },
            ) {
                SampleMapContent(selectedTab)
            }

            SampleTopBar(
                selectedMapStyle = selectedMapStyle,
                onMapStyleSelected = { selectedMapStyle = it },
                onBack = onBack,
            )

            FloatingActionButton(
                onClick = { controller?.centerOnCurrentLocation() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
            ) {
                Icon(Icons.Default.LocationSearching, contentDescription = "Current location")
            }
        }
    }
}

@Composable
private fun SampleMapContent(selectedTab: SampleTab) {
    when (selectedTab) {
        SampleTab.Explore -> {
            LeaflektMarker(
                position = Kolkata,
                title = "Kolkata",
                snippet = "Tap marker to verify callbacks",
                id = "explore-kolkata",
            )
            LeaflektMarker(
                position = VictoriaMemorial,
                title = "Victoria Memorial",
                id = "explore-victoria",
            )
        }

        SampleTab.Directions -> {
            LeaflektMarker(position = Kolkata, title = "Origin", id = "route-origin")
            LeaflektMarker(position = Howrah, title = "Destination", id = "route-destination")
            LeaflektPolyline(
                points = RoutePoints,
                color = Color(0xFF0A84FF),
                width = 8f,
                id = "sample-route",
            )
        }

        SampleTab.Clustering -> {
            LeaflektMarkerCluster(
                id = "sample-cluster",
                markers = ClusterMarkers,
                maxClusterRadius = 80,
            )
        }
    }
}

@Composable
private fun SampleTopBar(
    selectedMapStyle: LeaflektMapStyle,
    onMapStyleSelected: (LeaflektMapStyle) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconButton(onClick = onBack) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
            Text(
                text = "LeafleKT Compose sample",
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            SampleMapStyles.forEach { mapStyle ->
                FilterChip(
                    selected = mapStyle == selectedMapStyle,
                    onClick = { onMapStyleSelected(mapStyle) },
                    label = { Text(mapStyle.displayLabel()) },
                )
            }
        }
    }
}

private enum class SampleTab(
    val label: String,
    val shortLabel: String,
) {
    Explore("Explore", "E"),
    Directions("Directions", "D"),
    Clustering("Clusters", "C"),
}

private val Kolkata = LeaflektLatLng(22.5726, 88.3639)
private val VictoriaMemorial = LeaflektLatLng(22.5448, 88.3426)
private val Howrah = LeaflektLatLng(22.5958, 88.2636)

private val RoutePoints = listOf(
    Kolkata,
    LeaflektLatLng(22.5840, 88.3300),
    LeaflektLatLng(22.5900, 88.2950),
    Howrah,
)

private val ClusterMarkers = List(100) { index ->
    val row = index / 10
    val column = index % 10
    LeaflektMarkerInfo(
        id = "cluster-$index",
        lat = Kolkata.latitude + (row - 4.5) * 0.008,
        lng = Kolkata.longitude + (column - 4.5) * 0.008,
        title = "Marker #$index",
    )
}

private val SampleMapStyles = listOf(
    LeaflektMapStyle.OpenStreetMap,
    LeaflektMapStyle.CartoLight,
    LeaflektMapStyle.CartoDark,
    LeaflektMapStyle.OpenTopoMap,
    LeaflektMapStyle.EsriWorldImagery,
    LeaflektMapStyle.OpenFreeMapLiberty,
    LeaflektMapStyle.OpenFreeMapFiord,
    LeaflektMapStyle.OpenFreeMapBright,
)
