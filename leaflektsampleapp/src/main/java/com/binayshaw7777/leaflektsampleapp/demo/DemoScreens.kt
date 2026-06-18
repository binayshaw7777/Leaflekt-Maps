package com.binayshaw7777.leaflektsampleapp.demo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.binayshaw7777.leaflekt.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.LeaflektGeoJsonOverlay
import com.binayshaw7777.leaflekt.LeaflektLatLng
import com.binayshaw7777.leaflekt.LeaflektMapProperties
import com.binayshaw7777.leaflekt.LeaflektMapStyle
import com.binayshaw7777.leaflekt.LeaflektMapUiSettings
import com.binayshaw7777.leaflekt.LeaflektMarkerInfo
import com.binayshaw7777.leaflekt.compose.LeaflektCircle
import com.binayshaw7777.leaflekt.compose.LeaflektMap
import com.binayshaw7777.leaflekt.compose.LeaflektMarker
import com.binayshaw7777.leaflekt.compose.LeaflektMarkerCluster
import com.binayshaw7777.leaflekt.compose.LeaflektPolygon
import com.binayshaw7777.leaflekt.compose.LeaflektPolyline
import com.binayshaw7777.leaflekt.compose.rememberLeaflektCameraPositionState
import com.binayshaw7777.leaflekt.compose.rememberLeaflektPolylineState
import kotlinx.coroutines.delay

private val KOLKATA = LeaflektLatLng(22.5726, 88.3639)

private val ALL_STYLES = listOf(
    LeaflektMapStyle.OpenStreetMap,
    LeaflektMapStyle.CartoLight,
    LeaflektMapStyle.CartoDark,
    LeaflektMapStyle.OpenTopoMap,
    LeaflektMapStyle.EsriWorldImagery,
    LeaflektMapStyle.OpenFreeMapLiberty,
    LeaflektMapStyle.OpenFreeMapFiord,
    LeaflektMapStyle.OpenFreeMapBright,
)

private val STYLE_LABELS = listOf(
    "OpenStreetMap",
    "Carto Light",
    "Carto Dark",
    "OpenTopoMap",
    "Esri World Imagery",
    "OpenFreeMap Liberty",
    "OpenFreeMap Fiord",
    "OpenFreeMap Bright",
)

@Composable
internal fun MapStylesDemoScreen(styleIndex: Int) {
    val safeIndex = styleIndex.coerceIn(0, ALL_STYLES.lastIndex)
    val style = ALL_STYLES[safeIndex]
    val label = STYLE_LABELS[safeIndex]
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = style,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        )
        DemoLabel(label)
    }
}

@Composable
internal fun MarkersDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 13.0)
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektMarker(
                position = KOLKATA,
                title = "Kolkata",
                snippet = "City of Joy",
                id = "demo-basic",
            )
            LeaflektMarker(
                position = LeaflektLatLng(22.5926, 88.3639),
                title = "Titled Marker",
                id = "demo-titled",
            )
            LeaflektMarker(
                position = LeaflektLatLng(22.5526, 88.3839),
                title = "Rotated 45°",
                rotationDegrees = 45f,
                id = "demo-rotated",
            )
        }
        DemoLabel("Markers — basic · titled · rotated")
    }
}

@Composable
internal fun PolylinesDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    val solidPoints = listOf(
        LeaflektLatLng(22.52, 88.30),
        LeaflektLatLng(22.55, 88.36),
        LeaflektLatLng(22.58, 88.42),
    )
    val dashedPoints = listOf(
        LeaflektLatLng(22.56, 88.30),
        LeaflektLatLng(22.59, 88.36),
        LeaflektLatLng(22.62, 88.42),
    )
    val dottedPoints = listOf(
        LeaflektLatLng(22.48, 88.30),
        LeaflektLatLng(22.51, 88.36),
        LeaflektLatLng(22.54, 88.42),
    )
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektPolyline(
                points = solidPoints,
                color = Color(0xFF1565C0),
                width = 8f,
                id = "solid",
            )
            LeaflektPolyline(
                points = dashedPoints,
                color = Color(0xFFE53935),
                width = 8f,
                pattern = listOf(
                    com.binayshaw7777.leaflekt.LeaflektStrokePattern.Dash(length = 15f),
                    com.binayshaw7777.leaflekt.LeaflektStrokePattern.Gap(length = 10f),
                ),
                id = "dashed",
            )
            LeaflektPolyline(
                points = dottedPoints,
                color = Color(0xFF2E7D32),
                width = 6f,
                pattern = listOf(
                    com.binayshaw7777.leaflekt.LeaflektStrokePattern.Dot(radius = 4f),
                    com.binayshaw7777.leaflekt.LeaflektStrokePattern.Gap(length = 8f),
                ),
                id = "dotted",
            )
        }
        DemoLabel("Polylines — solid · dashed · dotted")
    }
}

@Composable
internal fun PolygonsDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 11.0)
    }
    val outerBoundary = listOf(
        LeaflektLatLng(22.42, 88.22),
        LeaflektLatLng(22.42, 88.52),
        LeaflektLatLng(22.68, 88.52),
        LeaflektLatLng(22.68, 88.22),
    )
    val innerHole = listOf(
        LeaflektLatLng(22.50, 88.30),
        LeaflektLatLng(22.50, 88.44),
        LeaflektLatLng(22.60, 88.44),
        LeaflektLatLng(22.60, 88.30),
    )
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektPolygon(
                points = outerBoundary,
                holes = listOf(innerHole),
                fillColor = Color(0x550B6E4F),
                strokeColor = Color(0xFF0B6E4F),
                strokeWidth = 4f,
                fillOpacity = 0.35f,
                id = "donut",
            )
        }
        DemoLabel("Polygon with hole (donut)")
    }
}

@Composable
internal fun CirclesDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektCircle(
                center = KOLKATA,
                radiusMeters = 2000.0,
                fillColor = Color(0x332196F3),
                strokeColor = Color(0xFF2196F3),
                strokeWidth = 3f,
                fillOpacity = 0.25f,
                id = "circle-lg",
            )
            LeaflektCircle(
                center = KOLKATA,
                radiusMeters = 1000.0,
                fillColor = Color(0x33E53935),
                strokeColor = Color(0xFFE53935),
                strokeWidth = 3f,
                fillOpacity = 0.3f,
                id = "circle-md",
            )
            LeaflektCircle(
                center = KOLKATA,
                radiusMeters = 400.0,
                fillColor = Color(0x332E7D32),
                strokeColor = Color(0xFF2E7D32),
                strokeWidth = 3f,
                fillOpacity = 0.4f,
                id = "circle-sm",
            )
        }
        DemoLabel("Circles — 400m · 1km · 2km radius")
    }
}

@Composable
internal fun SelectionDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    val routePoints = listOf(
        LeaflektLatLng(22.52, 88.28),
        LeaflektLatLng(22.55, 88.36),
        LeaflektLatLng(22.58, 88.44),
        LeaflektLatLng(22.62, 88.50),
    )
    val polylineState = rememberLeaflektPolylineState(points = routePoints)
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektPolyline(
                state = polylineState,
                color = Color(0xFF1565C0),
                width = 8f,
                selectedColor = Color(0xFFFF5722),
                selectedWidth = 14f,
                clickable = true,
                onClick = { polylineState.toggleSelection() },
                id = "route",
            )
        }
        DemoLabel("Selection — tap route to highlight/clear")
    }
}

@Composable
internal fun GeoJsonDemoScreen() {
    val india = LeaflektLatLng(22.0, 79.0)
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(india, 4.5)
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.India,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        )
        DemoLabel("GeoJSON — India boundary overlay")
    }
}

@Composable
internal fun ClusteringDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 11.0)
    }
    val markers = List(100) { i ->
        val row = i / 10
        val col = i % 10
        LeaflektMarkerInfo(
            id = "c-$i",
            lat = 22.5726 + (row - 4.5) * 0.010,
            lng = 88.3639 + (col - 4.5) * 0.010,
            title = "Point $i",
        )
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoDark,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektMarkerCluster(
                id = "demo-cluster",
                markers = markers,
                maxClusterRadius = 80,
            )
        }
        DemoLabel("Clustering — 100 markers · zoom to expand")
    }
}

@Composable
internal fun UiSettingsDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.OpenStreetMap,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true,
                showCurrentLocation = true,
            ),
        )
        DemoLabel("UI Settings — Zoom controls · Location button · Blue dot")
    }
}

@Composable
internal fun CustomIconsDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 13.0)
    }
    val pinUrl = "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png"
    val blueUrl = "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png"
    val goldUrl = "https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-gold.png"

    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        ) {
            LeaflektMarker(
                position = LeaflektLatLng(22.5726, 88.3639),
                title = "Red Pin",
                icon = com.binayshaw7777.leaflekt.LeaflektMarkerIconInfo(
                    dataUrl = pinUrl,
                    widthPx = 25,
                    heightPx = 41,
                    anchorFractionX = 0.5f,
                    anchorFractionY = 1.0f,
                ),
                id = "red-pin",
            )
            LeaflektMarker(
                position = LeaflektLatLng(22.5826, 88.3839),
                title = "Blue Pin",
                icon = com.binayshaw7777.leaflekt.LeaflektMarkerIconInfo(
                    dataUrl = blueUrl,
                    widthPx = 25,
                    heightPx = 41,
                    anchorFractionX = 0.5f,
                    anchorFractionY = 1.0f,
                ),
                id = "blue-pin",
            )
            LeaflektMarker(
                position = LeaflektLatLng(22.5626, 88.3439),
                title = "Gold Pin",
                icon = com.binayshaw7777.leaflekt.LeaflektMarkerIconInfo(
                    dataUrl = goldUrl,
                    widthPx = 25,
                    heightPx = 41,
                    anchorFractionX = 0.5f,
                    anchorFractionY = 1.0f,
                ),
                id = "gold-pin",
            )
        }
        DemoLabel("Custom Icons — External URL PNGs")
    }
}

@Composable
internal fun CameraDemoScreen() {
    val camera = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(KOLKATA, 12.0)
    }
    val delhi = LeaflektLatLng(28.6139, 77.2090)

    LaunchedEffect(Unit) {
        delay(3000) // Wait for tiles
        camera.animate(delhi, 14.0, 2000)
    }

    Box(Modifier.fillMaxSize()) {
        LeaflektMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = camera,
            properties = LeaflektMapProperties(
                mapStyle = LeaflektMapStyle.CartoLight,
                geoJsonOverlay = LeaflektGeoJsonOverlay.None,
            ),
            uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false),
        )
        DemoLabel("Camera — Kolkata → New Delhi (animated)")
    }
}

@Composable
private fun DemoLabel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(12.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.92f),
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}
