package com.binayshaw7777.leaflektsampleapp

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.binayshaw7777.leaflekt.library.map.LeaflektMapStyle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ExploreSearchBar(
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
            .padding(top = 12.dp)
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
                    onSearch = { onExpandedChange(false) },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = { RotatingSearchPlaceholder() },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        SearchBarTrailingContent(
                            isLoading = isLoading,
                            searchQuery = searchQuery,
                            onClear = onClear
                        )
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            PlacesPredictionList(
                predictions = predictions,
                query = searchQuery,
                isLoading = isLoading,
                onPredictionSelected = onSearchPrediction
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlacePickerSheet(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    predictions: List<Prediction>,
    isLoading: Boolean,
    onPredictionSelected: (Prediction) -> Unit,
    onDismissRequest: () -> Unit,
    onClear: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SearchBar(
                modifier = Modifier.fillMaxWidth(),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = query,
                        onQueryChange = onQueryChange,
                        onSearch = {},
                        expanded = true,
                        onExpandedChange = {},
                        placeholder = { RotatingSearchPlaceholder() },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            SearchBarTrailingContent(
                                isLoading = isLoading,
                                searchQuery = query,
                                onClear = onClear
                            )
                        }
                    )
                },
                expanded = true,
                onExpandedChange = {}
            ) {
                PlacesPredictionList(
                    predictions = predictions,
                    query = query,
                    isLoading = isLoading,
                    onPredictionSelected = onPredictionSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MapStyleSheet(
    selectedMapStyle: LeaflektMapStyle,
    onMapStyleSelected: (LeaflektMapStyle) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .navigationBarsPadding()
        ) {
             @Suppress("ExperimentalMaterial3Api")
             FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LeaflektMapStyle.entries.forEach { mapStyle ->
                    InputChip(
                        shape = RoundedCornerShape(20.dp),
                        selected = mapStyle == selectedMapStyle,
                        onClick = { onMapStyleSelected(mapStyle) },
                        label = { Text(mapStyle.displayLabel()) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun RotatingSearchPlaceholder(
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

    LaunchedEffect(cityPrompts, promptHoldDelayMillis) {
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

@Composable
private fun SearchBarTrailingContent(
    isLoading: Boolean,
    searchQuery: String,
    onClear: () -> Unit
) {
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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear"
                )
            }
        }
    }
}

@Composable
private fun PlacesPredictionList(
    predictions: List<Prediction>,
    query: String,
    isLoading: Boolean,
    onPredictionSelected: (Prediction) -> Unit
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
                        text = prediction.structuredFormatting?.mainText ?: prediction.description,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    Text(
                        text = prediction.structuredFormatting?.secondaryText.orEmpty(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .clickable { onPredictionSelected(prediction) }
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

        if (predictions.isEmpty() && query.length > 2 && !isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No results found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

internal fun LeaflektMapStyle.displayLabel(): String {
    return when (this) {
        LeaflektMapStyle.OpenStreetMap -> "OpenStreetMap"
        LeaflektMapStyle.CartoLight -> "CARTO Light"
        LeaflektMapStyle.CartoDark -> "CARTO Dark"
        LeaflektMapStyle.OpenTopoMap -> "OpenTopoMap"
        LeaflektMapStyle.EsriWorldImagery -> "Esri World Imagery"
    }
}
