package com.binayshaw7777.leaflekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationSearching
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.binayshaw7777.leaflekt.library.camera.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng
import com.binayshaw7777.leaflekt.library.camera.rememberLeaflektCameraPositionState
import com.binayshaw7777.leaflekt.library.circle.LeaflektCircle
import com.binayshaw7777.leaflekt.library.controller.LeaflektController
import com.binayshaw7777.leaflekt.library.map.LeaflektMap
import com.binayshaw7777.leaflekt.library.map.LeaflektMapProperties
import com.binayshaw7777.leaflekt.library.map.LeaflektMapStyle
import com.binayshaw7777.leaflekt.library.map.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.library.marker.LeaflektMarker
import com.binayshaw7777.leaflekt.library.polygon.LeaflektPolygon
import com.binayshaw7777.leaflekt.library.polyline.LeaflektPolyline
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern
import com.binayshaw7777.leaflekt.ui.theme.LeafleKTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LeafleKTTheme {
                LeaflektDemoScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun LeaflektDemoScreen(modifier: Modifier = Modifier) {
    var selectedZoom by rememberSaveable { mutableFloatStateOf(12f) }
    var circleRadiusMeters by rememberSaveable { mutableFloatStateOf(1500f) }
    var activeFeatureLat by rememberSaveable { mutableDoubleStateOf(22.5726) }
    var activeFeatureLng by rememberSaveable { mutableDoubleStateOf(88.3639) }
    var selectedMapStyle by rememberSaveable { mutableStateOf(LeaflektMapStyle.OpenStreetMap) }
    var lastTap by rememberSaveable { mutableStateOf("Tap anywhere on map to move the demo set") }
    var lastMarkerId by rememberSaveable { mutableStateOf("No marker clicked yet") }
    var isMarkerVisible by rememberSaveable { mutableStateOf(true) }
    var isPolylineVisible by rememberSaveable { mutableStateOf(true) }
    var isPolygonVisible by rememberSaveable { mutableStateOf(true) }
    var isCircleVisible by rememberSaveable { mutableStateOf(true) }
    var isPolylineSelected by rememberSaveable { mutableStateOf(false) }
    var isPolygonSelected by rememberSaveable { mutableStateOf(false) }
    var isCircleSelected by rememberSaveable { mutableStateOf(false) }
    var cameraMotionLabel by rememberSaveable { mutableStateOf("Camera idle") }
    var lastCameraSnapshot by rememberSaveable {
        mutableStateOf("22.57260, 88.36390 | z 12.0")
    }
    var isCameraMoving by rememberSaveable { mutableStateOf(false) }
    var mapController by remember {
        mutableStateOf<LeaflektController?>(null)
    }

    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }
    val sheetState = rememberBottomSheetScaffoldState()
    val mapProperties = LeaflektMapProperties(mapStyle = selectedMapStyle)
    val mapUiSettings = LeaflektMapUiSettings(
        zoomControlsEnabled = false,
        showCurrentLocation = true
    )
    val activeFeaturePoint = LeaflektLatLng(activeFeatureLat, activeFeatureLng)
    val activeFeatureCount = visibleFeatureCount(
        isMarkerVisible = isMarkerVisible,
        isPolylineVisible = isPolylineVisible,
        isPolygonVisible = isPolygonVisible,
        isCircleVisible = isCircleVisible
    )

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = sheetState,
        sheetPeekHeight = 100.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
        sheetShadowElevation = 10.dp,
        sheetContent = {
            MapControlSheet(
                selectedMapStyle = selectedMapStyle,
                selectedZoom = selectedZoom,
                circleRadiusMeters = circleRadiusMeters,
                isMarkerVisible = isMarkerVisible,
                isPolylineVisible = isPolylineVisible,
                isPolygonVisible = isPolygonVisible,
                isCircleVisible = isCircleVisible,
                onMapStyleSelected = { selectedMapStyle = it },
                onZoomChanged = { selectedZoom = it },
                onZoomChangeFinished = {
                    cameraPositionState.move(
                        target = cameraPositionState.position.target,
                        zoom = selectedZoom.toDouble()
                    )
                },
                onCircleRadiusChanged = { circleRadiusMeters = it },
                onToggleMarker = { isMarkerVisible = !isMarkerVisible },
                onTogglePolyline = { isPolylineVisible = !isPolylineVisible },
                onTogglePolygon = { isPolygonVisible = !isPolygonVisible },
                onToggleCircle = { isCircleVisible = !isCircleVisible },
                onShowAll = {
                    isMarkerVisible = true
                    isPolylineVisible = true
                    isPolygonVisible = true
                    isCircleVisible = true
                },
                onHideAll = {
                    isMarkerVisible = false
                    isPolylineVisible = false
                    isPolygonVisible = false
                    isCircleVisible = false
                },
                onGoToKolkata = {
                    cameraPositionState.move(
                        target = Kolkata,
                        zoom = selectedZoom.toDouble()
                    )
                },
                onGoToBengaluru = {
                    cameraPositionState.move(
                        target = Bengaluru,
                        zoom = selectedZoom.toDouble()
                    )
                },
                onGoToSelectedPoint = {
                    cameraPositionState.move(
                        target = activeFeaturePoint,
                        zoom = selectedZoom.toDouble()
                    )
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LeaflektMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                contentDescription = "LeafleKT demo map app",
                properties = mapProperties,
                uiSettings = mapUiSettings,
                onReady = { controller ->
                    mapController = controller
                },
                onMapClick = { point ->
                    activeFeatureLat = point.latitude
                    activeFeatureLng = point.longitude
                    lastTap = "Pinned at %.5f, %.5f".format(point.latitude, point.longitude)
                },
                onCameraMoveStarted = {
                    cameraMotionLabel = "Camera moving"
                    lastCameraSnapshot = cameraPositionState.position.displayLabel()
                    isCameraMoving = true
                },
                onCameraMove = {
                    lastCameraSnapshot = cameraPositionState.position.displayLabel()
                    isCameraMoving = true
                },
                onCameraIdle = {
                    cameraMotionLabel = "Camera idle"
                    selectedZoom = cameraPositionState.position.zoom.toFloat()
                    lastCameraSnapshot = cameraPositionState.position.displayLabel()
                    isCameraMoving = false
                },
                onMarkerClick = { markerId ->
                    lastMarkerId = markerId
                }
            ) {
                LeaflektMarker(
                    position = activeFeaturePoint,
                    title = "Selected point",
                    id = "demo-marker",
                    visible = isMarkerVisible,
                    onClick = {
                        lastMarkerId = "demo-marker"
                        true
                    }
                )

                LeaflektPolyline(
                    points = activeFeaturePoint.demoPolylinePoints(),
                    color = Color(0xFF1D3557),
                    width = 6f,
                    pattern = listOf(
                        LeaflektStrokePattern.Dash(10f),
                        LeaflektStrokePattern.Gap(8f)
                    ),
                    id = "demo-polyline",
                    visible = isPolylineVisible,
                    selected = isPolylineSelected,
                    onClick = {
                        isPolylineSelected = true
                        isPolygonSelected = false
                        isCircleSelected = false
                        lastTap = "Polyline click: demo-polyline"
                        true
                    }
                )

                LeaflektPolygon(
                    points = activeFeaturePoint.demoPolygonPoints(),
                    fillColor = Color(0xFF2A9D8F),
                    strokeColor = Color(0xFF264653),
                    strokeWidth = 4f,
                    fillOpacity = 0.25f,
                    id = "demo-polygon",
                    visible = isPolygonVisible,
                    selected = isPolygonSelected,
                    onClick = {
                        isPolylineSelected = false
                        isPolygonSelected = true
                        isCircleSelected = false
                        lastTap = "Polygon click: demo-polygon"
                        true
                    }
                )

                LeaflektCircle(
                    center = activeFeaturePoint,
                    radiusMeters = circleRadiusMeters.toDouble(),
                    fillColor = Color(0xFFF4A261),
                    strokeColor = Color(0xFFE76F51),
                    strokeWidth = 4f,
                    fillOpacity = 0.2f,
                    id = "demo-circle",
                    visible = isCircleVisible,
                    selected = isCircleSelected,
                    onClick = {
                        isPolylineSelected = false
                        isPolygonSelected = false
                        isCircleSelected = true
                        lastTap = "Circle click: demo-circle"
                        true
                    }
                )
            }

            AnimatedVisibility(!isCameraMoving, modifier = Modifier.align(Alignment.TopCenter)) {
                MapStatusCard(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    selectedMapStyle = selectedMapStyle,
                    activeFeatureCount = activeFeatureCount,
                    lastTap = lastTap,
                    lastMarkerId = lastMarkerId,
                    cameraMotionLabel = cameraMotionLabel,
                    lastCameraSnapshot = lastCameraSnapshot
                )
            }


            AnimatedVisibility(!isCameraMoving, modifier = Modifier.align(Alignment.BottomEnd)) {
                MapQuickActions(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(end = 16.dp, bottom = 248.dp),
                    onShowAll = {
                        isMarkerVisible = true
                        isPolylineVisible = true
                        isPolygonVisible = true
                        isCircleVisible = true
                    },
                    onHideAll = {
                        isMarkerVisible = false
                        isPolylineVisible = false
                        isPolygonVisible = false
                        isCircleVisible = false
                    },
                    onFocusSelected = {
                        cameraPositionState.move(
                            target = activeFeaturePoint,
                            zoom = selectedZoom.toDouble()
                        )
                    }
                )
            }

            CurrentLocationFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = 16.dp, bottom = 176.dp),
                onClick = {
                    mapController?.centerOnCurrentLocation(
                        zoom = selectedZoom.toDouble().coerceAtLeast(16.0)
                    )
                }
            )
        }
    }
}

@Composable
private fun MapStatusCard(
    modifier: Modifier = Modifier,
    selectedMapStyle: LeaflektMapStyle,
    activeFeatureCount: Int,
    lastTap: String,
    lastMarkerId: String,
    cameraMotionLabel: String,
    lastCameraSnapshot: String
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${selectedMapStyle.displayName()} | $activeFeatureCount layers visible",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$cameraMotionLabel | $lastCameraSnapshot",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = lastTap,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Last marker: $lastMarkerId",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MapQuickActions(
    modifier: Modifier = Modifier,
    onShowAll: () -> Unit,
    onHideAll: () -> Unit,
    onFocusSelected: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            AssistChip(
                onClick = onFocusSelected,
                label = { Text("Focus") }
            )
            AssistChip(
                onClick = onShowAll,
                label = { Text("Show all") }
            )
            AssistChip(
                onClick = onHideAll,
                label = { Text("Hide all") }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun MapControlSheet(
    selectedMapStyle: LeaflektMapStyle,
    selectedZoom: Float,
    circleRadiusMeters: Float,
    isMarkerVisible: Boolean,
    isPolylineVisible: Boolean,
    isPolygonVisible: Boolean,
    isCircleVisible: Boolean,
    onMapStyleSelected: (LeaflektMapStyle) -> Unit,
    onZoomChanged: (Float) -> Unit,
    onZoomChangeFinished: () -> Unit,
    onCircleRadiusChanged: (Float) -> Unit,
    onToggleMarker: () -> Unit,
    onTogglePolyline: () -> Unit,
    onTogglePolygon: () -> Unit,
    onToggleCircle: () -> Unit,
    onShowAll: () -> Unit,
    onHideAll: () -> Unit,
    onGoToKolkata: () -> Unit,
    onGoToBengaluru: () -> Unit,
    onGoToSelectedPoint: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Controls",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Keep the map open and change layers, style, camera, and radius from here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Circle radius ${circleRadiusMeters.toInt()} m",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Slider(
                value = circleRadiusMeters,
                onValueChange = onCircleRadiusChanged,
                valueRange = 300f..4000f
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Layers",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FeatureToggleChip("Marker", isMarkerVisible, onToggleMarker)
                FeatureToggleChip("Polyline", isPolylineVisible, onTogglePolyline)
                FeatureToggleChip("Polygon", isPolygonVisible, onTogglePolygon)
                FeatureToggleChip("Circle", isCircleVisible, onToggleCircle)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = onShowAll
                ) {
                    Text("Show all")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onHideAll
                ) {
                    Text("Hide all")
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Camera",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = onGoToKolkata
                ) {
                    Text("Kolkata")
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = onGoToBengaluru
                ) {
                    Text("Bengaluru")
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onGoToSelectedPoint
            ) {
                Text("Focus selected point")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Map style",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LeaflektMapStyle.entries.forEach { mapStyle ->
                    FilterChip(
                        selected = mapStyle == selectedMapStyle,
                        onClick = { onMapStyleSelected(mapStyle) },
                        label = { Text(mapStyle.displayName()) }
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Zoom ${selectedZoom.toInt()}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Slider(
                value = selectedZoom,
                onValueChange = onZoomChanged,
                valueRange = 3f..18f,
                onValueChangeFinished = onZoomChangeFinished
            )
        }
    }
}

@Composable
private fun FeatureToggleChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) }
    )
}

@Composable
private fun CurrentLocationFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Icon(
            imageVector = Icons.Rounded.LocationSearching,
            contentDescription = "Current location"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaflektDemoPreview() {
    LeafleKTTheme {
        LeaflektDemoScreen()
    }
}

private fun visibleFeatureCount(
    isMarkerVisible: Boolean,
    isPolylineVisible: Boolean,
    isPolygonVisible: Boolean,
    isCircleVisible: Boolean
): Int {
    return listOf(
        isMarkerVisible,
        isPolylineVisible,
        isPolygonVisible,
        isCircleVisible
    ).count { it }
}

private fun LeaflektMapStyle.displayName(): String {
    return when (this) {
        LeaflektMapStyle.OpenStreetMap -> "OpenStreetMap"
        LeaflektMapStyle.CartoLight -> "CARTO Light"
        LeaflektMapStyle.CartoDark -> "CARTO Dark"
        LeaflektMapStyle.OpenTopoMap -> "OpenTopoMap"
        LeaflektMapStyle.EsriWorldImagery -> "Esri World Imagery"
    }
}

private fun LeaflektLatLng.demoPolylinePoints(): List<LeaflektLatLng> {
    return listOf(
        LeaflektLatLng(latitude - 0.03, longitude - 0.03),
        LeaflektLatLng(latitude - 0.01, longitude),
        LeaflektLatLng(latitude + 0.02, longitude + 0.03)
    )
}

private fun LeaflektLatLng.demoPolygonPoints(): List<LeaflektLatLng> {
    return listOf(
        LeaflektLatLng(latitude + 0.01, longitude - 0.03),
        LeaflektLatLng(latitude + 0.04, longitude),
        LeaflektLatLng(latitude + 0.01, longitude + 0.03),
        LeaflektLatLng(latitude - 0.02, longitude)
    )
}

private fun LeaflektCameraPosition.displayLabel(): String {
    return "%.5f, %.5f | z %.1f".format(
        target.latitude,
        target.longitude,
        zoom
    )
}

private val Kolkata = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639)

private val Bengaluru = LeaflektLatLng(latitude = 12.9716, longitude = 77.5946)
