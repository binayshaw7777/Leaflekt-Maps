package com.binayshaw7777.leaflektsampleapp

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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val vm: OlaMapsViewModel = viewModel()
    var selectedTab by rememberSaveable { mutableStateOf(SampleTab.Explore) }

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
        when (selectedTab) {
            SampleTab.Explore -> ExploreContent(
                modifier = Modifier.padding(innerPadding),
                vm = vm,
            )
            SampleTab.Directions -> DirectionsContent(
                modifier = Modifier.padding(innerPadding),
                vm = vm,
            )
            SampleTab.Clustering -> ClusteringContent(
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreContent(
    modifier: Modifier,
    vm: OlaMapsViewModel,
) {
    val predictions by vm.explorePredictions.collectAsState()
    val isLoading by vm.isExploreSearchLoading.collectAsState()
    val selectedPlace by vm.selectedExplorePlace.collectAsState()
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }

    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 11.0)
    }

    val selectedLoc = selectedPlace?.geometry?.location

    LaunchedEffect(selectedLoc) {
        val loc = selectedLoc ?: return@LaunchedEffect
        cameraState.move(target = LeaflektLatLng(loc.lat, loc.lng), zoom = 15.0)
    }

    if (showStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = { selectedMapStyle = it; showStyleSheet = false },
            onDismissRequest = { showStyleSheet = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedMapStyle),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                showCurrentLocation = true,
            ),
            onReady = { controller = it },
        ) {
            if (selectedLoc != null) {
                LeaflektMarker(
                    position = LeaflektLatLng(selectedLoc.lat, selectedLoc.lng),
                    title = selectedPlace?.headline(),
                    id = "explore-pin",
                )
            }
        }

        ExploreSearchBar(
            expanded = searchExpanded,
            searchQuery = vm.exploreSearchQuery.value,
            onSearchQueryChange = vm::onExploreSearchQueryChange,
            onExpandedChange = { searchExpanded = it },
            isLoading = isLoading,
            onClear = vm::clearExploreSearch,
            predictions = predictions,
            onSearchPrediction = { pred ->
                vm.selectExplorePrediction(pred)
                searchExpanded = false
            },
        )

        if (!searchExpanded) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FloatingActionButton(
                    onClick = { showStyleSheet = true },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Icon(Icons.Default.Layers, contentDescription = "Map style")
                }
                FloatingActionButton(
                    onClick = { controller?.centerOnCurrentLocation() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Icon(Icons.Default.LocationSearching, contentDescription = "My location")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DirectionsContent(
    modifier: Modifier,
    vm: OlaMapsViewModel,
) {
    val predictions by vm.directionsPredictions.collectAsState()
    val isLoading by vm.isDirectionsSearchLoading.collectAsState()
    val originPlace by vm.selectedOriginPlace.collectAsState()
    val destPlace by vm.selectedDestinationPlace.collectAsState()
    val activeRoute by vm.activeRoute.collectAsState()
    val isRouteLoading by vm.isRouteLoading.collectAsState()
    val routeError by vm.routeErrorMessage.collectAsState()

    var showPicker by rememberSaveable { mutableStateOf(false) }
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var showDirectionsCard by rememberSaveable { mutableStateOf(true) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }

    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 6.0)
    }

    LaunchedEffect(activeRoute) {
        val route = activeRoute ?: return@LaunchedEffect
        cameraState.move(target = route.cameraTarget(), zoom = route.recommendedZoom())
    }

    if (showPicker) {
        PlacePickerSheet(
            title = if (vm.activeDirectionsEndpoint.value == DirectionsEndpoint.Origin) "From" else "To",
            query = vm.directionsSearchQuery.value,
            onQueryChange = vm::onDirectionsSearchQueryChange,
            predictions = predictions,
            isLoading = isLoading,
            onPredictionSelected = { pred ->
                vm.selectDirectionsPrediction(pred)
                showPicker = false
            },
            onDismissRequest = {
                vm.clearDirectionsSearch()
                showPicker = false
            },
            onClear = { vm.directionsSearchQuery.value = "" },
        )
    }

    if (showStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = { selectedMapStyle = it; showStyleSheet = false },
            onDismissRequest = { showStyleSheet = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedMapStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
            onReady = { controller = it },
        ) {
            originPlace?.geometry?.location?.let { loc ->
                LeaflektMarker(
                    position = LeaflektLatLng(loc.lat, loc.lng),
                    title = "Origin",
                    id = "dir-origin",
                )
            }
            destPlace?.geometry?.location?.let { loc ->
                LeaflektMarker(
                    position = LeaflektLatLng(loc.lat, loc.lng),
                    title = "Destination",
                    id = "dir-dest",
                )
            }
            activeRoute?.let { route ->
                LeaflektPolyline(
                    points = route.points,
                    color = Color(0xFF1565C0),
                    width = 7f,
                    id = "dir-route",
                )
            }
        }

        Column(Modifier.fillMaxSize()) {
            if (showDirectionsCard) Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        "Directions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )

                    DirectionsPlaceButton(
                        label = "From",
                        place = originPlace,
                        onTap = {
                            vm.beginDirectionsSearch(DirectionsEndpoint.Origin)
                            showPicker = true
                        },
                        onClear = { vm.clearDirectionsPlace(DirectionsEndpoint.Origin) },
                    )
                    DirectionsPlaceButton(
                        label = "To",
                        place = destPlace,
                        onTap = {
                            vm.beginDirectionsSearch(DirectionsEndpoint.Destination)
                            showPicker = true
                        },
                        onClear = { vm.clearDirectionsPlace(DirectionsEndpoint.Destination) },
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = vm::swapDirectionsPlaces,
                            modifier = Modifier.weight(1f),
                            enabled = originPlace != null || destPlace != null,
                        ) {
                            Icon(Icons.Default.SwapVert, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Swap")
                        }
                        Button(
                            onClick = vm::refreshRouteIfPossible,
                            modifier = Modifier.weight(1f),
                            enabled = originPlace != null && destPlace != null && !isRouteLoading,
                        ) {
                            Text(if (isRouteLoading) "Loading\u2026" else "Get Route")
                        }
                    }

                    activeRoute?.let { route ->
                        val info = listOfNotNull(route.distanceLabel(), route.durationLabel())
                            .joinToString(" | ")
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Column(Modifier.fillMaxWidth().padding(10.dp)) {
                                Text(
                                    route.summary ?: "Route ready",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                if (info.isNotEmpty()) {
                                    Text(
                                        info,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                        }
                    }

                    routeError?.let { error ->
                        Text(
                            error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FloatingActionButton(
                        onClick = { showDirectionsCard = !showDirectionsCard },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {
                        Icon(
                            imageVector = if (showDirectionsCard) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showDirectionsCard) "Hide directions" else "Show directions",
                        )
                    }
                    FloatingActionButton(
                        onClick = { showStyleSheet = true },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {
                        Icon(Icons.Default.Layers, contentDescription = "Map style")
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectionsPlaceButton(
    label: String,
    place: PlaceDetails?,
    onTap: () -> Unit,
    onClear: () -> Unit,
) {
    Card(
        onClick = onTap,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    place?.headline() ?: "Search $label",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (place != null) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear $label")
                }
            }
        }
    }
}

@Composable
private fun ClusteringContent(modifier: Modifier) {
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }

    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 11.0)
    }

    if (showStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = { selectedMapStyle = it; showStyleSheet = false },
            onDismissRequest = { showStyleSheet = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedMapStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
            onReady = { controller = it },
        ) {
            LeaflektMarkerCluster(
                id = "sample-cluster",
                markers = ClusterMarkers,
                maxClusterRadius = 80,
            )
        }

        Column(Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp)) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    "100 markers — tap clusters to expand",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }

        FloatingActionButton(
            onClick = { showStyleSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Icon(Icons.Default.Layers, contentDescription = "Map style")
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

private val ClusterMarkers = List(100) { index ->
    val row = index / 10
    val column = index % 10
    LeaflektMarkerInfo(
        id = "cluster-$index",
        lat = 22.5726 + (row - 4.5) * 0.008,
        lng = 88.3639 + (column - 4.5) * 0.008,
        title = "Marker #$index",
    )
}
