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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import com.binayshaw7777.leaflekt.library.camera.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng
import com.binayshaw7777.leaflekt.library.camera.rememberLeaflektCameraPositionState
import com.binayshaw7777.leaflekt.library.cluster.LeaflektMarkerCluster
import com.binayshaw7777.leaflekt.library.cluster.MarkerClusterOptions
import com.binayshaw7777.leaflekt.library.controller.LeaflektController
import com.binayshaw7777.leaflekt.library.map.LeaflektMap
import com.binayshaw7777.leaflekt.library.map.LeaflektMapProperties
import com.binayshaw7777.leaflekt.library.map.LeaflektMapStyle
import com.binayshaw7777.leaflekt.library.map.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.library.marker.LeaflektMarker
import com.binayshaw7777.leaflekt.library.marker.rememberLeaflektMarkerState
import com.binayshaw7777.leaflekt.library.polyline.LeaflektPolyline

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
                onMapStyleChange = { selectedMapStyle = it }
            )

            SampleTab.Directions -> DirectionsMapScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                selectedMapStyle = selectedMapStyle,
                onMapStyleChange = { selectedMapStyle = it }
            )

            SampleTab.Clustering -> ClusteringMapScreen(
                modifier = Modifier.padding(innerPadding),
                viewModel = viewModel,
                selectedMapStyle = selectedMapStyle,
                onMapStyleChange = { selectedMapStyle = it }
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
    onMapStyleChange: (LeaflektMapStyle) -> Unit
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
    val selectedPlaceLocation = explorePlace?.geometry?.location

     val cameraPositionState = rememberLeaflektCameraPositionState {
         position = LeaflektCameraPosition(
             target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
             zoom = 12.0
         )
     }
     val markerState = rememberLeaflektMarkerState()
     val featuredMarkerState = rememberLeaflektMarkerState(
         position = LeaflektLatLng(22.5726 + 0.01, 88.3639 + 0.01)
     )
     val historicalSiteMarkerState = rememberLeaflektMarkerState(
         position = LeaflektLatLng(22.5448, 88.3426)
     )

     // Demo: Async marker icon loaded from a remote URL
     val demoAsyncIcon = rememberSampleRemoteMarkerIcon(
         model = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Bicycle_icon.svg/64px-Bicycle_icon.svg.png",
         widthPx = 48,
         heightPx = 48,
         anchorFractionX = 0.5f,
         anchorFractionY = 0.5f
      )

      LaunchedEffect(selectedPlaceLocation) {
         val location = selectedPlaceLocation
         if (location == null) {
             markerState.hideInfoWindow()
             return@LaunchedEffect
         }
         markerState.position = LeaflektLatLng(location.lat, location.lng)
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
                onMapStyleChange(it)
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
                automaticThemeSync = false
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true,
                showCurrentLocation = true
            ),
            cameraPositionState = cameraPositionState,
            onReady = { controller ->
                mapController = controller
            },
            onMapClick = {
                markerState.hideInfoWindow()
                featuredMarkerState.hideInfoWindow()
                historicalSiteMarkerState.hideInfoWindow()
            }
             ) {
                  LeaflektMarker(
                      state = markerState,
                      title = explorePlace?.headline(),
                      snippet = explorePlace?.supportingLine(),
                      visible = selectedPlaceLocation != null,
                      infoWindow = {
                          MarkerInfoWindowCard(
                              label = "Selected place",
                              headline = explorePlace?.headline().orEmpty(),
                              supportingLine = explorePlace?.supportingLine(),
                              onDismiss = markerState::hideInfoWindow
                          )
                      },
                      id = "explore-selected-place"
                  )

                  // Demo: Composable marker icon (a star) at a fixed offset
                  LeaflektMarker(
                      state = featuredMarkerState,
                      iconContent = {
                          Surface(
                              shape = RoundedCornerShape(12.dp),
                              color = MaterialTheme.colorScheme.secondaryContainer,
                              modifier = Modifier
                                  .padding(4.dp)
                                  .size(36.dp)
                           ) {
                               Icon(
                                   imageVector = Icons.Filled.Star,
                                   contentDescription = "Featured",
                                   tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                   modifier = Modifier
                                       .size(20.dp)
                               )
                           }
                      },
                      infoWindow = {
                          MarkerInfoWindowCard(
                              label = "Featured",
                              headline = "Composable Icon",
                              supportingLine = "Pinned near Kolkata",
                              onDismiss = featuredMarkerState::hideInfoWindow
                          )
                      },
                      iconAnchorX = 0.5f,
                      iconAnchorY = 0.5f

                  )

             // Demo: Async icon marker (Victoria Memorial)
             LeaflektMarker(
                 state = historicalSiteMarkerState,
                 icon = demoAsyncIcon.value,
                 infoWindow = {
                     MarkerInfoWindowCard(
                         label = "Historical Site",
                         headline = "Victoria Memorial",
                         supportingLine = "Sample app icon loaded via Coil",
                         onDismiss = historicalSiteMarkerState::hideInfoWindow
                     )
                 },
                 alpha = 0.9f
             )
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
    selectedMapStyle: LeaflektMapStyle,
    onMapStyleChange: (LeaflektMapStyle) -> Unit
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
    var showMapStyleSheet by rememberSaveable { mutableStateOf(false) }
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
    val originRotation =  0f
    val destinationRotation =  0f

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

    if (showMapStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = {
                onMapStyleChange(it)
                showMapStyleSheet = false
            },
            onDismissRequest = {
                showMapStyleSheet = false
            }
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
                automaticThemeSync = false
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true,
                showCurrentLocation = true
            ),
            cameraPositionState = cameraPositionState,
            onReady = { controller ->
                mapController = controller
            },
            onMapClick = {
                originMarkerState.hideInfoWindow()
                destinationMarkerState.hideInfoWindow()
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
                             supportingLine = null,
                             onDismiss = originMarkerState::hideInfoWindow
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
                             supportingLine = null,
                             onDismiss = destinationMarkerState::hideInfoWindow
                         )
                     },
                     id = "directions-destination"
                 )
             }

            // Async icon demo: bike marker at route midpoint
            activeRoute?.let { route ->
                if (route.points.size > 2) {
                    val midIndex = route.points.size / 2
                    val midPoint = route.points[midIndex]
                    val demoBikeIcon = rememberSampleRemoteMarkerIcon(
                        model = "https://static.vecteezy.com/system/resources/thumbnails/051/959/452/small/top-view-of-a-classic-black-motorcycle-showcasing-its-sleek-design-and-leather-seat-perfect-for-bike-enthusiasts-and-design-projects-png.png",
                        widthPx = 48,
                        heightPx = 48,
                        anchorFractionX = 0.5f,
                        anchorFractionY = 0.5f
                    )
                    LeaflektMarker(
                        position = midPoint,
                        icon = demoBikeIcon.value,
                        alpha = 0.9f
                    )
                }
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
internal fun ClusteringMapScreen(
    modifier: Modifier = Modifier,
    viewModel: OlaMapsViewModel,
    selectedMapStyle: LeaflektMapStyle,
    onMapStyleChange: (LeaflektMapStyle) -> Unit
) {
    val searchQuery by viewModel.exploreSearchQuery.collectAsState()
    val predictions by viewModel.explorePredictions.collectAsState()
    val isLoading by viewModel.isExploreSearchLoading.collectAsState()
    val selectedPlace by viewModel.selectedExplorePlace.collectAsState()

    var showMapStyleSheet by rememberSaveable { mutableStateOf(false) }
    var mapController by remember { mutableStateOf<LeaflektController?>(null) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(22.5726, 88.3639),
            zoom = 11.0
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

    // Generate some random points around Kolkata for clustering
    val clusterPoints = remember {
        List(100) {
            LeaflektLatLng(
                latitude = 22.5726 + (Math.random() - 0.5) * 0.2,
                longitude = 88.3639 + (Math.random() - 0.5) * 0.2
            )
        }
    }

    if (showMapStyleSheet) {
        MapStyleSheet(
            selectedMapStyle = selectedMapStyle,
            onMapStyleSelected = {
                onMapStyleChange(it)
                showMapStyleSheet = false
            },
            onDismissRequest = {
                showMapStyleSheet = false
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            properties = LeaflektMapProperties(
                mapStyle = selectedMapStyle,
                automaticThemeSync = false
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true,
                showCurrentLocation = true
            ),
            cameraPositionState = cameraPositionState,
            onReady = { controller ->
                mapController = controller
            },
            onMapClick = {
                markerState.hideInfoWindow()
            }
        ) {
            if (selectedPlace != null) {
                val location = selectedPlace?.geometry?.location
                if (location != null) {
                    LeaflektMarker(
                        state = markerState.apply {
                            position = LeaflektLatLng(location.lat, location.lng)
                        },
                        title = selectedPlace?.headline(),
                        snippet = selectedPlace?.supportingLine(),
                        infoWindow = {
                            MarkerInfoWindowCard(
                                label = "Search result",
                                headline = selectedPlace?.headline() ?: "",
                                supportingLine = selectedPlace?.supportingLine(),
                                onDismiss = markerState::hideInfoWindow
                            )
                        },
                        id = "clustering-selected-place"
                    )
                }
            }

            LeaflektMarkerCluster(
                options = MarkerClusterOptions(
                    maxClusterRadius = 80,
                    showCoverageOnHover = false
                ),
                onClusterClick = { lat, lng, count ->
                    // Optional: handle cluster click
                }
            ) {
                clusterPoints.forEachIndexed { index, point ->
                    LeaflektMarker(
                        position = point,
                        title = "Marker #$index",
                        snippet = "Clustered point"
                    )
                }
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

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = "Marker Clustering: 100 points",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

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
                        zoom = 15.0
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
private fun MarkerInfoWindowCard(
    label: String,
    headline: String,
    supportingLine: String? = null,
    onDismiss: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close info window",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
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
    Directions(label = "Directions", icon = Icons.Default.LocationSearching),
    Clustering(label = "Clustering", icon = Icons.Default.Layers)
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
