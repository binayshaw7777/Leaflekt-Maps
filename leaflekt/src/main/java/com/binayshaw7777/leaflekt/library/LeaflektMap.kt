/*
 * Copyright 2026 Binay Shaw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binayshaw7777.leaflekt.library

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

/**
 * CompositionLocal used to provide the [LeaflektController] to map children.
 */
val LocalLeaflektController = compositionLocalOf<LeaflektController?> { null }

/**
 * CompositionLocal used to provide the [LeaflektCameraPositionState] to map children.
 */
internal val LocalLeaflektCameraPositionState = staticCompositionLocalOf { LeaflektCameraPositionState() }

/**
 * Compose API for rendering a Leaflekt map backed by WebView and JavaScript bridge.
 *
 * This is the primary entry point for the LeafleKT SDK. It allows for high-level, declarative
 * map management similar to the Google Maps Compose library.
 *
 * ### Basic Usage:
 * ```kotlin
 * LeaflektMap(
 *     modifier = Modifier.fillMaxSize(),
 *     onMapClick = { latLng -> Log.d("Map", "Clicked at $latLng") }
 * ) {
 *     LeaflektMarker(
 *         position = LeaflektLatLng(22.5726, 88.3639),
 *         title = "Kolkata"
 *     )
 * }
 * ```
 *
 * ### Advanced Usage (Hoisted State):
 * ```kotlin
 * val cameraPositionState = rememberLeaflektCameraPositionState {
 *     position = LeaflektCameraPosition(LeaflektLatLng(12.9716, 77.5946), 10.0)
 * }
 * 
 * LeaflektMap(
 *     cameraPositionState = cameraPositionState,
 *     properties = LeaflektMapProperties(mapStyle = LeaflektMapStyle.CartoDark),
 *     uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = false)
 * )
 * ```
 *
 * ### Marker With Custom Compose Info Window:
 * ```kotlin
 * val markerState = rememberLeaflektMarkerState(
 *     position = LeaflektLatLng(12.9716, 77.5946)
 * )
 *
 * LeaflektMap {
 *     LeaflektMarker(
 *         state = markerState,
 *         rotationDegrees = 18f,
 *         infoWindow = {
 *             Surface {
 *                 Text("Bangalore marker")
 *             }
 *         }
 *     )
 * }
 * ```
 *
 * **Attribution:**
 * This component wraps [Leaflet.js](https://leafletjs.com/) and provides a bridge for 
 * [OpenStreetMap](https://www.openstreetmap.org/) based tiles.
 *
 * @param modifier The modifier to be applied to the map layout.
 * @param cameraPositionState The state object to be used to control or observe the map's camera.
 * @param contentDescription The content description of the map for accessibility.
 * @param properties The properties of the map, such as the active [LeaflektMapStyle].
 * @param uiSettings The UI settings of the map, such as gesture toggles.
 * @param onMapLoaded Callback invoked when the underlying Leaflet engine is fully initialized.
 * @param onReady Callback invoked when the [LeaflektController] is available for imperative calls.
 * @param onMapClick Callback invoked when the user taps on the map surface.
 * @param onCameraMoveStarted Callback invoked when a pan/zoom motion session begins.
 * @param onCameraMove Callback invoked while the map camera is actively changing.
 * @param onCameraIdle Callback invoked after the map camera settles.
 * @param onMarkerClick Callback invoked when a marker layer is clicked.
 * @param content The content to be displayed on top of the map, such as [LeaflektMarker] and
 * [LeaflektOverlay].
 */
@Composable
fun LeaflektMap(
    modifier: Modifier = Modifier,
    cameraPositionState: LeaflektCameraPositionState = rememberLeaflektCameraPositionState(),
    contentDescription: String? = null,
    properties: LeaflektMapProperties = DefaultLeaflektMapProperties,
    uiSettings: LeaflektMapUiSettings = DefaultLeaflektMapUiSettings,
    onMapLoaded: (() -> Unit)? = null,
    onReady: ((LeaflektController) -> Unit)? = null,
    onMapClick: ((LeaflektLatLng) -> Unit)? = null,
    onCameraMoveStarted: (() -> Unit)? = null,
    onCameraMove: (() -> Unit)? = null,
    onCameraIdle: (() -> Unit)? = null,
    onMarkerClick: ((String) -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    val controller = remember { LeaflektController() }
    var hasReportedReady by remember { mutableStateOf(false) }

    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnMapLoaded by rememberUpdatedState(onMapLoaded)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnCameraMoveStarted by rememberUpdatedState(onCameraMoveStarted)
    val currentOnCameraMove by rememberUpdatedState(onCameraMove)
    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentCameraPositionState by rememberUpdatedState(cameraPositionState)

    val jsBridge = remember {
        LeaflektJsBridge(
            onMapReady = {
                controller.notifyMapReady()
                if (!hasReportedReady) {
                    hasReportedReady = true
                    currentOnReady?.invoke(controller)
                    currentOnMapLoaded?.invoke()
                }
            },
            onMapClick = { lat, lng ->
                currentOnMapClick?.invoke(
                    LeaflektLatLng(latitude = lat, longitude = lng)
                )
            },
            onCameraMoveStarted = { lat, lng, zoom, bearing ->
                currentCameraPositionState.onCameraMoveStarted(
                    LeaflektCameraPosition(
                        target = LeaflektLatLng(latitude = lat, longitude = lng),
                        zoom = zoom,
                        bearing = bearing
                    )
                )
                currentOnCameraMoveStarted?.invoke()
            },
            onCameraMove = { lat, lng, zoom, bearing ->
                currentCameraPositionState.onCameraMove(
                    LeaflektCameraPosition(
                        target = LeaflektLatLng(latitude = lat, longitude = lng),
                        zoom = zoom,
                        bearing = bearing
                    )
                )
                currentOnCameraMove?.invoke()
            },
            onCameraIdle = { lat, lng, zoom, bearing ->
                currentCameraPositionState.onCameraIdle(
                    LeaflektCameraPosition(
                        target = LeaflektLatLng(latitude = lat, longitude = lng),
                        zoom = zoom,
                        bearing = bearing
                    )
                )
                currentOnCameraIdle?.invoke()
            },
            onMarkerClick = { markerId ->
                controller.notifyMarkerClick(markerId)
                currentOnMarkerClick?.invoke(markerId)
            },
            onPolylineClick = { polylineId ->
                controller.notifyPolylineClick(polylineId)
            },
            onPolygonClick = { polygonId ->
                controller.notifyPolygonClick(polygonId)
            },
            onCircleClick = { circleId ->
                controller.notifyCircleClick(circleId)
            },
            onProjectionChanged = { projectionsJson ->
                controller.onProjectionChanged(projectionsJson)
            }
        )
    }

    // Bind controller to camera state
    DisposableEffect(controller, cameraPositionState) {
        cameraPositionState.setController(controller)
        onDispose {
            cameraPositionState.setController(null)
        }
    }

    LaunchedEffect(controller) {
        controller.initializeMap(
            initialLat = cameraPositionState.position.target.latitude,
            initialLng = cameraPositionState.position.target.longitude,
            initialZoom = cameraPositionState.position.zoom,
            initialBearing = cameraPositionState.position.bearing,
            isZoomControlEnabled = uiSettings.zoomControlsEnabled,
            initialMapStyle = properties.mapStyle,
            minZoom = properties.minZoom,
            maxZoom = properties.maxZoom
        )
    }

    val isDarkTheme = isSystemInDarkTheme()
    
    LaunchedEffect(properties.mapStyle, properties.automaticThemeSync, isDarkTheme) {
        val style = if (properties.automaticThemeSync) {
            if (isDarkTheme) LeaflektMapStyle.CartoDark else LeaflektMapStyle.OpenStreetMap
        } else {
            properties.mapStyle
        }
        controller.setMapStyle(style)
    }

    // UI Settings wiring
    LaunchedEffect(uiSettings.zoomControlsEnabled) {
        controller.setZoomControlsEnabled(uiSettings.zoomControlsEnabled)
    }

    LaunchedEffect(uiSettings.scrollGesturesEnabled) {
        controller.setScrollGesturesEnabled(uiSettings.scrollGesturesEnabled)
    }

    LaunchedEffect(uiSettings.zoomGesturesEnabled) {
        controller.setZoomGesturesEnabled(uiSettings.zoomGesturesEnabled)
    }

    LaunchedEffect(uiSettings.rotateGesturesEnabled) {
        controller.setRotateGesturesEnabled(uiSettings.rotateGesturesEnabled)
    }

    Box(modifier = modifier) {
        LeaflektWebView(
            modifier = Modifier.fillMaxSize(),
            controller = controller,
            jsBridge = jsBridge,
            contentDescription = contentDescription
        )

        CompositionLocalProvider(
            LocalLeaflektController provides controller,
            LocalLeaflektCameraPositionState provides cameraPositionState
        ) {
            LeaflektCurrentLocationOverlay(uiSettings = uiSettings)
            content()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.setWebView(null)
        }
    }
}
