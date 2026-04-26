package com.binayshaw7777.leaflektsampleapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.binayshaw7777.leaflekt.library.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.library.LeaflektController
import com.binayshaw7777.leaflekt.library.LeaflektLatLng
import com.binayshaw7777.leaflekt.library.LeaflektMap
import com.binayshaw7777.leaflekt.library.LeaflektMapProperties
import com.binayshaw7777.leaflekt.library.LeaflektMapStyle
import com.binayshaw7777.leaflekt.library.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.library.LeaflektMarker
import com.binayshaw7777.leaflekt.library.LeaflektPolyline
import com.binayshaw7777.leaflekt.library.rememberLeaflektCameraPositionState
import com.binayshaw7777.leaflekt.library.rememberLeaflektMarkerState

@Composable
internal fun SampleAppScreen(viewModel: OlaMapsViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableStateOf(SampleTab.Explore) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                SampleTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
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
            SampleTab.Explore -> ExploreMapScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                selectedMapStyle = selectedMapStyle,
                onMapStyleSelected = { selectedMapStyle = it }
            )

            SampleTab.Directions -> DirectionsMapScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                selectedMapStyle = selectedMapStyle
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExploreMapScreen(
    modifier: Modifier = Modifier,
    viewModel: OlaMapsViewModel,
    selectedMapStyle: LeaflektMapStyle,
    onMapStyleSelected: (LeaflektMapStyle) -> Unit
) {
    val searchQuery by viewModel.exploreSearchQuery.collectAsState()
    val predictions by viewModel.explorePredictions.collectAsState()
    val isLoading by viewModel.isExploreSearchLoading.collectAsState()
    val selectedPlace by viewModel.selectedExplorePlace.collectAsState()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showMapStyleSheet by rememberSaveable { mutableStateOf(false) }
    var selectedZoom by rememberSaveable { mutableFloatStateOf(12f) }
    var mapController by remember { mutableStateOf<LeaflektController?>(null) }
    val explorePlace = selectedPlace

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }
    val markerState = rememberLeaflektMarkerState()

    LaunchedEffect(selectedPlace) {
        val location = selectedPlace?.geometry?.location
        if (location == null) {
            markerState.hideInfoWindow()
            return@LaunchedEffect
        }
        cameraPositionState.position = LeaflektCameraPosition(
            target = LeaflektLatLng(location.lat, location.lng),
            zoom = 15.0
        )
        markerState.showInfoWindow()
    }

    if (showMapStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = {
                onMapStyleSelected(it)
                showMapStyleSheet = false
            },
            onDismissRequest = {
                showMapStyleSheet = false
            }
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            properties = LeaflektMapProperties(
                mapStyle = selectedMapStyle,
                automaticThemeSync = true
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true,
                showCurrentLocation = true
            ),
            cameraPositionState = cameraPositionState,
            onReady = { controller ->
                mapController = controller
            }
        ) {
            explorePlace?.geometry?.location?.let { location ->
                LeaflektMarker(
                    state = markerState.apply {
                        position = LeaflektLatLng(location.lat, location.lng)
                    },
                    title = explorePlace.headline(),
                    snippet = explorePlace.supportingLine(),
                    infoWindow = {
                        MarkerInfoWindowCard(
                            label = "Selected place",
                            headline = explorePlace.headline(),
                            supportingLine = explorePlace.supportingLine()
                        )
                    },
                    id = "explore-selected-place"
                )
            }
        }

        Column(Modifier.fillMaxSize()) {
            ExploreSearchBar(
                expanded = expanded,
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onExploreSearchQueryChange,
                onExpandedChange = { expanded = it },
                isLoading = isLoading,
                onClear = viewModel::clearExploreSearch,
                predictions = predictions,
                onSearchPrediction = { prediction ->
                    viewModel.selectExplorePrediction(prediction)
                    expanded = false
                }
            )

            Spacer(Modifier.weight(1f))

            SmallFloatingActionButton(
                onClick = { showMapStyleSheet = true },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Map layers"
                )
            }

            FloatingActionButton(
                onClick = {
                    mapController?.centerOnCurrentLocation(
                        zoom = selectedZoom.toDouble().coerceAtLeast(16.0)
                    )
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationSearching,
                    contentDescription = "Current location"
                )
            }
        }
    }
}

@Composable
internal fun DirectionsMapScreen(
    modifier: Modifier = Modifier,
    viewModel: OlaMapsViewModel,
    selectedMapStyle: LeaflektMapStyle
) {
    val originPlace by viewModel.selectedOriginPlace.collectAsState()
    val destinationPlace by viewModel.selectedDestinationPlace.collectAsState()
    val activeRoute by viewModel.activeRoute.collectAsState()
    val routeErrorMessage by viewModel.routeErrorMessage.collectAsState()
    val isRouteLoading by viewModel.isRouteLoading.collectAsState()
    val directionsSearchQuery by viewModel.directionsSearchQuery.collectAsState()
    val directionsPredictions by viewModel.directionsPredictions.collectAsState()
    val isDirectionsSearchLoading by viewModel.isDirectionsSearchLoading.collectAsState()
    val activeDirectionsEndpoint by viewModel.activeDirectionsEndpoint.collectAsState()

    var showDirectionsSearchSheet by rememberSaveable { mutableStateOf(false) }
    var selectedZoom by rememberSaveable { mutableFloatStateOf(12f) }
    var mapController by remember { mutableStateOf<LeaflektController?>(null) }
    val originMarkerState = rememberLeaflektMarkerState()
    val destinationMarkerState = rememberLeaflektMarkerState()

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }
    val originRotation = activeRoute?.points?.routeStartHeadingDegrees() ?: 0f
    val destinationRotation = activeRoute?.points?.routeEndHeadingDegrees() ?: 0f

    LaunchedEffect(originPlace) {
        val location = originPlace?.geometry?.location ?: return@LaunchedEffect
        originMarkerState.position = LeaflektLatLng(location.lat, location.lng)
        originMarkerState.showInfoWindow()
    }

    LaunchedEffect(destinationPlace) {
        val location = destinationPlace?.geometry?.location ?: return@LaunchedEffect
        destinationMarkerState.position = LeaflektLatLng(location.lat, location.lng)
        destinationMarkerState.showInfoWindow()
    }

    LaunchedEffect(originPlace, destinationPlace, activeRoute) {
        val route = activeRoute
        if (route != null) {
            cameraPositionState.position = LeaflektCameraPosition(
                target = route.cameraTarget(),
                zoom = route.recommendedZoom()
            )
            return@LaunchedEffect
        }

        val originLocation = originPlace?.geometry?.location
        val destinationLocation = destinationPlace?.geometry?.location
        val fallbackTarget = when {
            originLocation != null && destinationLocation != null -> LeaflektLatLng(
                latitude = (originLocation.lat + destinationLocation.lat) / 2,
                longitude = (originLocation.lng + destinationLocation.lng) / 2
            )

            originLocation != null -> LeaflektLatLng(originLocation.lat, originLocation.lng)
            destinationLocation != null -> LeaflektLatLng(
                destinationLocation.lat,
                destinationLocation.lng
            )

            else -> null
        } ?: return@LaunchedEffect

        cameraPositionState.position = LeaflektCameraPosition(
            target = fallbackTarget,
            zoom = 12.5
        )
    }

    if (showDirectionsSearchSheet) {
        PlacePickerSheet(
            title = when (activeDirectionsEndpoint) {
                DirectionsEndpoint.Origin -> "Choose origin"
                DirectionsEndpoint.Destination -> "Choose destination"
            },
            query = directionsSearchQuery,
            onQueryChange = viewModel::onDirectionsSearchQueryChange,
            predictions = directionsPredictions,
            isLoading = isDirectionsSearchLoading,
            onPredictionSelected = { prediction ->
                viewModel.selectDirectionsPrediction(prediction)
                showDirectionsSearchSheet = false
            },
            onDismissRequest = {
                showDirectionsSearchSheet = false
                viewModel.clearDirectionsSearch()
            },
            onClear = viewModel::clearDirectionsSearch
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            properties = LeaflektMapProperties(
                mapStyle = selectedMapStyle,
                automaticThemeSync = true
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true,
                showCurrentLocation = true
            ),
            cameraPositionState = cameraPositionState,
            onReady = { controller ->
                mapController = controller
            }
        ) {
            activeRoute?.let { route ->
                LeaflektPolyline(
                    points = route.points,
                    color = Color(0xFF0A84FF),
                    width = 8f,
                    id = "directions-route"
                )
            }

            originPlace?.geometry?.location?.let { location ->
                LeaflektMarker(
                    state = originMarkerState.apply {
                        position = LeaflektLatLng(location.lat, location.lng)
                    },
                    title = "Origin",
                    snippet = originPlace?.headline(),
                    rotationDegrees = originRotation,
                    infoWindow = {
                        MarkerInfoWindowCard(
                            label = "Origin",
                            headline = originPlace?.headline() ?: "Origin",
                            supportingLine = originPlace?.supportingLine()
                        )
                    },
                    id = "directions-origin"
                )
            }

            destinationPlace?.geometry?.location?.let { location ->
                LeaflektMarker(
                    state = destinationMarkerState.apply {
                        position = LeaflektLatLng(location.lat, location.lng)
                    },
                    title = "Destination",
                    snippet = destinationPlace?.headline(),
                    rotationDegrees = destinationRotation,
                    infoWindow = {
                        MarkerInfoWindowCard(
                            label = "Destination",
                            headline = destinationPlace?.headline() ?: "Destination",
                            supportingLine = destinationPlace?.supportingLine()
                        )
                    },
                    id = "directions-destination"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            DirectionsPlacesCard(
                originPlace = originPlace,
                destinationPlace = destinationPlace,
                isRouteLoading = isRouteLoading,
                route = activeRoute,
                routeErrorMessage = routeErrorMessage,
                onPickOrigin = {
                    viewModel.beginDirectionsSearch(DirectionsEndpoint.Origin)
                    showDirectionsSearchSheet = true
                },
                onPickDestination = {
                    viewModel.beginDirectionsSearch(DirectionsEndpoint.Destination)
                    showDirectionsSearchSheet = true
                },
                onSwapPlaces = viewModel::swapDirectionsPlaces,
                onClearOrigin = { viewModel.clearDirectionsPlace(DirectionsEndpoint.Origin) },
                onClearDestination = { viewModel.clearDirectionsPlace(DirectionsEndpoint.Destination) },
                onRefreshRoute = viewModel::refreshRouteIfPossible
            )

            Spacer(Modifier.weight(1f))

            FloatingActionButton(
                onClick = {
                    mapController?.centerOnCurrentLocation(
                        zoom = selectedZoom.toDouble().coerceAtLeast(16.0)
                    )
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .navigationBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationSearching,
                    contentDescription = "Current location"
                )
            }
        }
    }
}

@Composable
private fun MarkerInfoWindowCard(
    label: String,
    headline: String,
    supportingLine: String?
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        modifier = Modifier
            .padding(bottom = 14.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            supportingLine?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DirectionsPlacesCard(
    originPlace: PlaceDetails?,
    destinationPlace: PlaceDetails?,
    isRouteLoading: Boolean,
    route: DirectionsRoute?,
    routeErrorMessage: String?,
    onPickOrigin: () -> Unit,
    onPickDestination: () -> Unit,
    onSwapPlaces: () -> Unit,
    onClearOrigin: () -> Unit,
    onClearDestination: () -> Unit,
    onRefreshRoute: () -> Unit
) {

    var expandCard by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Directions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = {
                        expandCard = !expandCard
                    }
                ) {
                    Icon(
                        imageVector = if (expandCard) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (expandCard) "Hide directions" else "Show directions"
                    )
                }
            }

            AnimatedVisibility(visible = expandCard) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DirectionsPlaceButton(
                        label = "From",
                        place = originPlace,
                        onClick = onPickOrigin,
                        onClear = onClearOrigin
                    )

                    DirectionsPlaceButton(
                        label = "To",
                        place = destinationPlace,
                        onClick = onPickDestination,
                        onClear = onClearDestination
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onSwapPlaces,
                            enabled = originPlace != null || destinationPlace != null
                        ) {
                            Text("Swap")
                        }

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onRefreshRoute,
                            enabled = originPlace != null && destinationPlace != null && !isRouteLoading
                        ) {
                            Text(if (isRouteLoading) "Loading..." else "Refresh route")
                        }
                    }

                    when {
                        route != null -> {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = route.summary ?: "Route ready",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = listOfNotNull(
                                            route.distanceLabel(),
                                            route.durationLabel()
                                        ).joinToString(" | "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        routeErrorMessage != null -> {
                            Text(
                                text = routeErrorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }    }
}

@Composable
private fun DirectionsPlaceButton(
    label: String,
    place: PlaceDetails?,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = place?.headline() ?: "Search $label",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            if (place != null) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear $label"
                    )
                }
            }
        }
    }
}

private enum class SampleTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Explore(label = "Explore", icon = Icons.Default.Search),
    Directions(label = "Directions", icon = Icons.Default.LocationSearching)
}

private fun List<LeaflektLatLng>.routeStartHeadingDegrees(): Float? {
    if (size < 2) {
        return null
    }

    return first().headingTo(this[1])
}

private fun List<LeaflektLatLng>.routeEndHeadingDegrees(): Float? {
    if (size < 2) {
        return null
    }

    return this[size - 2].headingTo(last())
}

private fun LeaflektLatLng.headingTo(other: LeaflektLatLng): Float {
    val startLatitude = Math.toRadians(latitude)
    val endLatitude = Math.toRadians(other.latitude)
    val longitudeDelta = Math.toRadians(other.longitude - longitude)
    val y = kotlin.math.sin(longitudeDelta) * kotlin.math.cos(endLatitude)
    val x = kotlin.math.cos(startLatitude) * kotlin.math.sin(endLatitude) -
        kotlin.math.sin(startLatitude) * kotlin.math.cos(endLatitude) * kotlin.math.cos(longitudeDelta)
    val heading = Math.toDegrees(kotlin.math.atan2(y, x))
    return ((heading + 360.0) % 360.0).toFloat()
}
