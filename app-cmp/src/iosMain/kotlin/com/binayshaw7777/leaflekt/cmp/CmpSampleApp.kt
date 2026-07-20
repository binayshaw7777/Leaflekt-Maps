package com.binayshaw7777.leaflekt.cmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.binayshaw7777.leaflekt.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.LeaflektController
import com.binayshaw7777.leaflekt.LeaflektLatLng
import com.binayshaw7777.leaflekt.compose.LeaflektMap
import com.binayshaw7777.leaflekt.LeaflektMapProperties
import com.binayshaw7777.leaflekt.LeaflektMapStyle
import com.binayshaw7777.leaflekt.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.compose.LeaflektMarker
import com.binayshaw7777.leaflekt.compose.LeaflektMarkerCluster
import com.binayshaw7777.leaflekt.LeaflektMarkerInfo
import com.binayshaw7777.leaflekt.compose.rememberLeaflektCameraPositionState
import kotlin.math.sin
import kotlin.math.cos

private val Kolkata = LeaflektLatLng(22.5726, 88.3639)

private enum class IosSampleTab(val label: String) {
    Explore("Explore"), Directions("Directions"), Clustering("Clustering")
}

@Composable
actual fun CmpSampleAppScreen(modifier: Modifier) {
    // Use ordinal-based savers: enums are not auto-saveable on iOS Compose
    var selectedTab by rememberSaveable(
        stateSaver = Saver(save = { it.ordinal }, restore = { IosSampleTab.entries[it] })
    ) { mutableStateOf(IosSampleTab.Explore) }
    var selectedStyle by rememberSaveable(
        stateSaver = Saver(save = { it.ordinal }, restore = { LeaflektMapStyle.entries[it] })
    ) { mutableStateOf(LeaflektMapStyle.CartoDark) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                IosSampleTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    IosSampleTab.Explore -> Icons.Default.Search
                                    IosSampleTab.Directions -> Icons.Default.Directions
                                    IosSampleTab.Clustering -> Icons.Default.Star
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            IosSampleTab.Explore -> IosExploreScreen(
                modifier = Modifier.padding(innerPadding),
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
            IosSampleTab.Directions -> IosDirectionsScreen(
                modifier = Modifier.padding(innerPadding),
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
            IosSampleTab.Clustering -> IosClusteringScreen(
                modifier = Modifier.padding(innerPadding),
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
        }
    }
}

@Composable
private fun IosExploreScreen(
    modifier: Modifier,
    selectedStyle: LeaflektMapStyle,
    onStyleChange: (LeaflektMapStyle) -> Unit
) {
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(target = Kolkata, zoom = 12.0)
    }

    if (showStyleSheet) {
        IosMapStyleSheet(selectedStyle, onStyleChange) { showStyleSheet = false }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false, showCurrentLocation = true),
            onReady = { controller = it }
        ) {
            LeaflektMarker(position = Kolkata, title = "Kolkata")
        }
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallFloatingActionButton(
                        onClick = { showStyleSheet = true },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) { Icon(Icons.Default.Layers, contentDescription = "Map style") }
                    SmallFloatingActionButton(
                        onClick = { controller?.centerOnCurrentLocation(16.0) },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ) { Icon(Icons.Default.LocationSearching, contentDescription = "My location") }
                }
            }
        }
    }
}

@Composable
private fun IosDirectionsScreen(
    modifier: Modifier,
    selectedStyle: LeaflektMapStyle,
    onStyleChange: (LeaflektMapStyle) -> Unit
) {
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(target = Kolkata, zoom = 8.0)
    }

    if (showStyleSheet) {
        IosMapStyleSheet(selectedStyle, onStyleChange) { showStyleSheet = false }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    "Directions: search not available on iOS CMP yet",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        SmallFloatingActionButton(
            onClick = { showStyleSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) { Icon(Icons.Default.Layers, contentDescription = "Map style") }
    }
}

@Composable
private fun IosClusteringScreen(
    modifier: Modifier,
    selectedStyle: LeaflektMapStyle,
    onStyleChange: (LeaflektMapStyle) -> Unit
) {
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    val clusterMarkers = remember {
        (0 until 100).map { i ->
            val angle = i * 0.628
            val radius = 0.05 + (i % 5) * 0.02
            LeaflektMarkerInfo(
                id = "ios-cluster-$i",
                lat = Kolkata.latitude + radius * sin(angle),
                lng = Kolkata.longitude + radius * cos(angle),
                title = "Marker #$i"
            )
        }
    }
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(target = Kolkata, zoom = 11.0)
    }

    if (showStyleSheet) {
        IosMapStyleSheet(selectedStyle, onStyleChange) { showStyleSheet = false }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false)
        ) {
            LeaflektMarkerCluster(
                id = "ios-cluster-group",
                markers = clusterMarkers
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    "100 markers — tap clusters to expand",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        SmallFloatingActionButton(
            onClick = { showStyleSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) { Icon(Icons.Default.Layers, contentDescription = "Map style") }
    }
}

@Composable
private fun IosMapStyleSheet(
    selectedStyle: LeaflektMapStyle,
    onStyleSelected: (LeaflektMapStyle) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Map Style") },
        text = {
            Column {
                LeaflektMapStyle.entries.forEach { style ->
                    androidx.compose.material3.TextButton(
                        onClick = { onStyleSelected(style); onDismiss() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            style.name,
                            color = if (style == selectedStyle) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
