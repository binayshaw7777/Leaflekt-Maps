package com.binayshaw7777.leaflekt.cmp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kotlin.math.cos
import kotlin.math.sin

private val Kolkata = LeaflektLatLng(22.5726, 88.3639)

private enum class IosSampleTab(val label: String) {
    Explore("Explore"), Directions("Directions"), Clustering("Clustering")
}

@Composable
actual fun CmpSampleAppScreen(modifier: Modifier) {
    val vm = remember { CmpOlaMapsViewModel() }
    DisposableEffect(vm) {
        onDispose { vm.onCleared() }
    }

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
                vm = vm,
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
            IosSampleTab.Directions -> IosDirectionsScreen(
                modifier = Modifier.padding(innerPadding),
                vm = vm,
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
            IosSampleTab.Clustering -> IosClusteringScreen(
                modifier = Modifier.padding(innerPadding),
                vm = vm,
                selectedStyle = selectedStyle,
                onStyleChange = { selectedStyle = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosExploreScreen(
    modifier: Modifier,
    vm: CmpOlaMapsViewModel,
    selectedStyle: LeaflektMapStyle,
    onStyleChange: (LeaflektMapStyle) -> Unit
) {
    val predictions by vm.explorePredictions.collectAsState()
    val isLoading by vm.isExploreLoading.collectAsState()
    val selectedPlace by vm.selectedExplorePlace.collectAsState()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(target = Kolkata, zoom = 12.0)
    }
    val selectedLoc = selectedPlace?.geometry?.location

    LaunchedEffect(selectedLoc) {
        val loc = selectedLoc ?: return@LaunchedEffect
        cameraState.move(target = loc.latLng, zoom = 15.0)
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
            onReady = { controller = it },
            onMapClick = { controller?.removeMarker("ios-explore-pin") }
        ) {
            if (selectedLoc != null) {
                LeaflektMarker(
                    position = selectedLoc.latLng,
                    title = selectedPlace?.headline(),
                    id = "ios-explore-pin"
                )
            }
        }
        Column(Modifier.fillMaxSize()) {
            IosSearchBar(
                expanded = expanded,
                query = vm.exploreQuery.collectAsState().value,
                placeholder = "Search places…",
                isLoading = isLoading,
                predictions = predictions,
                onQueryChange = vm::onExploreQueryChange,
                onExpandedChange = { expanded = it },
                onClear = vm::clearExplore,
                onSelectPrediction = { pred -> vm.selectExplorePrediction(pred); expanded = false }
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosDirectionsScreen(
    modifier: Modifier,
    vm: CmpOlaMapsViewModel,
    selectedStyle: LeaflektMapStyle,
    onStyleChange: (LeaflektMapStyle) -> Unit
) {
    val predictions by vm.directionsPredictions.collectAsState()
    val isLoading by vm.isDirectionsLoading.collectAsState()
    val originPlace by vm.originPlace.collectAsState()
    val destPlace by vm.destinationPlace.collectAsState()
    val activeRoute by vm.activeRoute.collectAsState()
    val isRouteLoading by vm.isRouteLoading.collectAsState()
    val routeError by vm.routeError.collectAsState()

    var showPicker by rememberSaveable { mutableStateOf(false) }
    var showStyleSheet by rememberSaveable { mutableStateOf(false) }
    var controller by remember { mutableStateOf<LeaflektController?>(null) }

    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(target = Kolkata, zoom = 6.0)
    }

    LaunchedEffect(activeRoute) {
        val route = activeRoute ?: return@LaunchedEffect
        cameraState.move(target = route.cameraTarget(), zoom = route.recommendedZoom())
    }

    if (showPicker) {
        IosPlacePickerSheet(
            title = if (vm.activeEndpoint.collectAsState().value == CmpDirectionsEndpoint.Origin) "From" else "To",
            query = vm.directionsQuery.collectAsState().value,
            onQueryChange = vm::onDirectionsQueryChange,
            predictions = predictions,
            isLoading = isLoading,
            onSelectPrediction = { pred -> vm.selectDirectionsPrediction(pred); showPicker = false },
            onDismiss = { vm.clearDirectionsSearch(); showPicker = false }
        )
    }

    if (showStyleSheet) {
        IosMapStyleSheet(selectedStyle, onStyleChange) { showStyleSheet = false }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraState,
            properties = LeaflektMapProperties(mapStyle = selectedStyle),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
            onReady = { controller = it }
        ) {
            originPlace?.geometry?.location?.let {
                LeaflektMarker(position = it.latLng, title = "Origin", id = "ios-dir-origin")
            }
            destPlace?.geometry?.location?.let {
                LeaflektMarker(position = it.latLng, title = "Destination", id = "ios-dir-dest")
            }
            activeRoute?.let { route ->
                LeaflektPolyline(
                    points = route.points,
                    color = Color(0xFF1565C0),
                    width = 7f,
                    id = "ios-dir-route"
                )
            }
        }

        Column(Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Directions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    IosDirectionsPlaceButton(
                        label = "From",
                        place = originPlace,
                        onTap = { vm.beginDirectionsSearch(CmpDirectionsEndpoint.Origin); showPicker = true },
                        onClear = { vm.clearDirectionsPlace(CmpDirectionsEndpoint.Origin) }
                    )
                    IosDirectionsPlaceButton(
                        label = "To",
                        place = destPlace,
                        onTap = { vm.beginDirectionsSearch(CmpDirectionsEndpoint.Destination); showPicker = true },
                        onClear = { vm.clearDirectionsPlace(CmpDirectionsEndpoint.Destination) }
                    )

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = vm::swapPlaces,
                            modifier = Modifier.weight(1f),
                            enabled = originPlace != null || destPlace != null
                        ) { Text("Swap") }
                        Button(
                            onClick = vm::refreshRoute,
                            modifier = Modifier.weight(1f),
                            enabled = originPlace != null && destPlace != null && !isRouteLoading
                        ) { Text(if (isRouteLoading) "Loading…" else "Get Route") }
                    }

                    activeRoute?.let { route ->
                        val info = listOfNotNull(route.distanceLabel(), route.durationLabel()).joinToString(" | ")
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Column(Modifier.fillMaxWidth().padding(10.dp)) {
                                Text(route.summary ?: "Route ready",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                                if (info.isNotEmpty()) {
                                    Text(info, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }

                    if (routeError != null) {
                        Text(routeError!!, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                SmallFloatingActionButton(
                    onClick = { showStyleSheet = true },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ) { Icon(Icons.Default.Layers, contentDescription = "Map style") }
            }
        }
    }
}

@Composable
private fun IosDirectionsPlaceButton(
    label: String,
    place: CmpPlaceDetails?,
    onTap: () -> Unit,
    onClear: () -> Unit
) {
    Card(
        onClick = onTap,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
                Text(
                    place?.headline() ?: "Search $label",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1, overflow = TextOverflow.Ellipsis
                )
            }
            if (place != null) {
                IconButton(onClick = onClear) { Icon(Icons.Default.Close, contentDescription = "Clear $label") }
            }
        }
    }
}

@Composable
private fun IosClusteringScreen(
    modifier: Modifier,
    vm: CmpOlaMapsViewModel,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosSearchBar(
    expanded: Boolean,
    query: String,
    placeholder: String,
    isLoading: Boolean,
    predictions: List<CmpPrediction>,
    onQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onClear: () -> Unit,
    onSelectPrediction: (CmpPrediction) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp)
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = if (expanded) 0.dp else 16.dp),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = { onExpandedChange(false) },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = { Text(placeholder) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp), strokeWidth = 2.dp)
                        } else if (query.isNotEmpty()) {
                            IconButton(onClick = onClear) { Icon(Icons.Default.Close, contentDescription = "Clear") }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            IosPredictionList(predictions, query, isLoading, onSelectPrediction)
        }
    }
}

@Composable
private fun IosPredictionList(
    predictions: List<CmpPrediction>,
    query: String,
    isLoading: Boolean,
    onSelect: (CmpPrediction) -> Unit
) {
    LazyColumn {
        items(predictions, key = { it.placeId }) { pred ->
            ListItem(
                headlineContent = {
                    Text(pred.structuredFormatting?.mainText ?: pred.description,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                supportingContent = pred.structuredFormatting?.secondaryText?.let { sub ->
                    { Text(sub, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                },
                modifier = Modifier.fillMaxWidth().clickable { onSelect(pred) }
            )
        }
        if (predictions.isEmpty() && query.length > 2 && !isLoading) {
            item {
                Text(
                    "No results",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IosPlacePickerSheet(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    predictions: List<CmpPrediction>,
    isLoading: Boolean,
    onSelectPrediction: (CmpPrediction) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp))
            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = {},
                        expanded = true,
                        onExpandedChange = {},
                        placeholder = { Text("Search places…") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (isLoading) CircularProgressIndicator(Modifier.padding(8.dp), strokeWidth = 2.dp)
                            else if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    )
                },
                expanded = true,
                onExpandedChange = {}
            ) {
                IosPredictionList(predictions, query, isLoading, onSelectPrediction)
            }
            Spacer(Modifier.height(8.dp))
        }
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
