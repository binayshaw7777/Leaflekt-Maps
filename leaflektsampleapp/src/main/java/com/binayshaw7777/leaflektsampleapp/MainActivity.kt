package com.binayshaw7777.leaflektsampleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
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
import com.binayshaw7777.leaflekt.library.rememberLeaflektCameraPositionState
import com.binayshaw7777.leaflekt.library.rememberLeaflektMarkerState
import com.binayshaw7777.leaflektsampleapp.ui.theme.LeafleKTTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafleKTTheme {
                MapView()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapView(viewModel: OlaMapsViewModel = viewModel()) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val predictions by viewModel.predictions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showMapStyleSheet by rememberSaveable { mutableStateOf(false) }

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }

    var selectedZoom by rememberSaveable { mutableFloatStateOf(12f) }

    var mapController by remember {
        mutableStateOf<LeaflektController?>(null)
    }
    val markerState = rememberLeaflektMarkerState()
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.CartoDark) }

    // Sync map camera with selected place from ViewModel
    LaunchedEffect(selectedPlace) {
        selectedPlace?.geometry?.location?.let { loc ->
            cameraPositionState.position = LeaflektCameraPosition(
                target = LeaflektLatLng(loc.lat, loc.lng),
                zoom = 15.0
            )
        }
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (showMapStyleSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    showMapStyleSheet = false
                }
            },
            sheetState = sheetState
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .navigationBarsPadding()
            ) {
                FlowRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LeaflektMapStyle.entries.forEach {
                        InputChip(
                            shape = RoundedCornerShape(20.dp),
                            selected = it == selectedMapStyle,
                            onClick = {
                                selectedMapStyle = it
                                showMapStyleSheet = false
                            },
                            label = { Text(it.name) }
                        )
                    }
                }
            }
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            properties = LeaflektMapProperties(mapStyle = selectedMapStyle),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = false,
                rotateGesturesEnabled = true
            ),
            cameraPositionState = cameraPositionState
        ) {
            selectedPlace?.geometry?.location?.let { location ->
                LeaflektMarker(
                    state = markerState.apply {
                        position = LeaflektLatLng(location.lat, location.lng)
                    },
                    title = selectedPlace?.name ?: "Selected Location",
                    snippet = selectedPlace?.formattedAddress
                )
            }
        }

        Column(Modifier.fillMaxSize()) {

            MySearchBar(
                expanded = expanded,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onExpandedChange = { expanded = it },
                onClear = { viewModel.clearSearch() },
                isLoading = isLoading,
                predictions = predictions,
                onSearchPrediction = { prediction ->
                    viewModel.selectPrediction(prediction)
                    expanded = false
                }
            )

            Spacer(Modifier.weight(1f))

            SmallFloatingActionButton(
                onClick = {
                    showMapStyleSheet = true
                },
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
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MySearchBar(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    isLoading: Boolean,
    onClear: () -> Unit,
    predictions: List<Prediction>,
    onSearchPrediction: (Prediction) -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp) // Adjust for status bar
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = if (expanded) 0.dp else 16.dp)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = {
                        onExpandedChange(false)
                    },
                    expanded = expanded,
                    onExpandedChange = { onExpandedChange(it) },
                    placeholder = { RotatingSearchPlaceholder() },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.padding(4.dp))
                            }
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = onClear) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = { onExpandedChange(it) },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(predictions.size) { index ->
                    val prediction = predictions[index]
                    ListItem(
                        headlineContent = {
                            Text(
                                prediction.structuredFormatting?.mainText
                                    ?: prediction.description,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        supportingContent = {
                            Text(
                                prediction.structuredFormatting?.secondaryText ?: "",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                onSearchPrediction(prediction)
                            }
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                    )
                    if (index < predictions.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 56.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }


                if (predictions.isEmpty() && searchQuery.length > 2 && !isLoading) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No results found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RotatingSearchPlaceholder(
    cityPrompts: List<String> = listOf(
        "Kolkata",
        "Bangalore",
        "Mumbai",
        "Delhi",
        "Hyderabad",
        "Chennai",
        "Pune"
    ),
    promptHoldDelayMillis: Long = 2400L
) {
    var activeCityIndex by remember { mutableStateOf(0) }
    var visibleCharacterCount by remember { mutableStateOf(0) }
    val activePrompt = cityPrompts[activeCityIndex]

    LaunchedEffect(cityPrompts) {
        while (true) {
            val currentPrompt = cityPrompts[activeCityIndex]
            visibleCharacterCount = 0
            while (visibleCharacterCount < currentPrompt.length) {
                delay(55)
                visibleCharacterCount++
            }
            delay(promptHoldDelayMillis)
            activeCityIndex = (activeCityIndex + 1) % cityPrompts.size
        }
    }

    AnimatedContent(
        targetState = activePrompt,
        transitionSpec = {
            fadeIn(animationSpec = tween(durationMillis = 250)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 180))
        },
        label = "search-placeholder"
    ) { prompt ->
        Text(
            text = "Try searching ${prompt.take(visibleCharacterCount)}",
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
    }
}
