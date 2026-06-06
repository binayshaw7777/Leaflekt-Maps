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

package com.binayshaw7777.leaflekt.library.marker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.map.MapView
import com.binayshaw7777.leaflekt.library.map.MapComposable
import com.binayshaw7777.leaflekt.library.map.LocalMapController
import com.binayshaw7777.leaflekt.library.map.LocalMarkerClusterId
import com.binayshaw7777.leaflekt.library.overlay.MapOverlay
import java.util.UUID

/**
 * A declarative marker that can be placed inside a [MapView] content block.
 *
 * This component follows the Jetpack Compose declarative pattern. It adds a marker to the
 * underlying Leaflet.js map when it enters the composition and removes it when it leaves.
 *
 * ### Usage Example:
 * ```kotlin
 * MapView {
 *     Marker(
 *         position = LatLng(22.5726, 88.3639),
 *         title = "Victoria Memorial",
 *         snippet = "Built between 1906 and 1921",
 *         alpha = 0.8f
 *     )
 * }
 * ```
 *
 * ### Custom Icon, Rotation, and Compose Info Window:
 * ```kotlin
 * val markerState = rememberMarkerState(
 *     position = LatLng(12.9716, 77.5946)
 * )
 *
 * Marker(
 *     state = markerState,
 *     icon = MarkerIcon(bitmap = scooterBitmap, widthPx = 64, heightPx = 64),
 *     rotationDegrees = 32f,
 *     infoWindow = {
 *         Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 4.dp) {
 *             Text(
 *                 text = "Scooter heading to pickup",
 *                 modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
 *             )
 *         }
 *     }
 * )
 * ```
 *
 * **Attribution:**
 * - **Leaflet.js:** Marker rendering is powered by [L.marker](https://leafletjs.com/reference.html#marker).
 * - **OpenStreetMap:** Default marker visuals are compatible with OSM-based tile layers.
 *
 * @param state The [MarkerState] to be used to control or observe the marker's position.
 * @param title Optional title shown in the default Leaflet popup. Ignored when [infoWindow] is
 * provided.
 * @param snippet Optional secondary text shown in the default Leaflet popup. Ignored when
 * [infoWindow] is provided.
 * @param icon Optional custom marker bitmap and anchor configuration.
 * @param rotationDegrees Optional clockwise marker rotation in degrees.
 * @param visible Whether the marker is currently visible on the map.
 * @param alpha The opacity of the marker (0.0 to 1.0). Default is 1.0.
 * @param id Unique identifier for the marker. If not provided, a random UUID will be generated.
 * @param infoWindow Optional Compose content anchored above the marker. When supplied, the
 * default Leaflet popup is suppressed.
 * @param isInfoWindowVisible Initial visibility state for the info window.
 * @param onClick A lambda invoked when the marker is clicked. Return `true` to consume the click
 * and keep Leaflekt from opening the marker info window automatically.
 */
@Composable
@MapComposable
fun Marker(
    state: MarkerState = rememberMarkerState(),
    title: String? = null,
    snippet: String? = null,
    icon: MarkerIcon? = null,
    rotationDegrees: Float = 0f,
    visible: Boolean = true,
    alpha: Float = 1.0f,
    zIndex: Float = 0f,
    id: String = remember { UUID.randomUUID().toString() },
    infoWindow: (@Composable () -> Unit)? = null,
    isInfoWindowVisible: Boolean = false,
    onClick: () -> Boolean = { false }
) {
    val controller = LocalMapController.current ?: return
    val clusterId = LocalMarkerClusterId.current
    val markerIconInfo = icon?.toMarkerIconInfo()
    val popupTitle = if (infoWindow == null) title else null
    val popupSnippet = if (infoWindow == null) snippet else null
    val markerClickHandler = remember(state, onClick) {
        {
            val clickConsumed = onClick()
            if (!clickConsumed) {
                state.showInfoWindow()
            }
            clickConsumed
        }
    }

    DisposableEffect(id) {
        val info = MarkerInfo(
            id = id,
            lat = state.position.latitude,
            lng = state.position.longitude,
            title = popupTitle,
            snippet = popupSnippet,
            visible = visible,
            alpha = alpha,
            zIndex = zIndex,
            icon = markerIconInfo,
            rotationDegrees = rotationDegrees
        )

        controller.addMarker(info, clusterId)
        controller.registerMarkerClick(id, markerClickHandler)

        if (isInfoWindowVisible) {
            state.showInfoWindow()
        }

        onDispose {
            controller.unregisterMarkerClick(id)
            controller.removeMarker(id)
        }
    }

    DisposableEffect(id, markerClickHandler) {
        controller.registerMarkerClick(id, markerClickHandler)
        onDispose {
            controller.unregisterMarkerClick(id)
        }
    }

    DisposableEffect(controller, id) {
        state.bindInfoWindowActions(
            showInfoWindow = { controller.showMarkerInfoWindow(id) },
            hideInfoWindow = { controller.hideMarkerInfoWindow(id) }
        )
        onDispose {
            state.bindInfoWindowActions(showInfoWindow = null, hideInfoWindow = null)
        }
    }

    LaunchedEffect(state.position, popupTitle, popupSnippet, visible, alpha, zIndex, markerIconInfo, rotationDegrees) {
        val info = MarkerInfo(
            id = id,
            lat = state.position.latitude,
            lng = state.position.longitude,
            title = popupTitle,
            snippet = popupSnippet,
            visible = visible,
            alpha = alpha,
            zIndex = zIndex,
            icon = markerIconInfo,
            rotationDegrees = rotationDegrees
        )
        controller.updateMarker(info)
    }

    if (infoWindow != null && state.isInfoWindowShown) {
        MapOverlay(
            position = state.position,
            anchorFractionX = 0.5f,
            anchorFractionY = 1f
        ) {
            infoWindow()
        }
    }
}

/**
 * A declarative marker that can be placed inside a [MapView] content block.
 *
 * This is a convenience overload that takes a [com.binayshaw7777.leaflekt.library.camera.LatLng] instead of a [MarkerState].
 * Use this when you don't need to programmatically move the marker after it's been created.
 *
 * @param position The position of the marker.
 * @param title Optional title shown in the default Leaflet popup.
 * @param snippet Optional secondary text shown in the default Leaflet popup.
 * @param icon Optional custom marker bitmap and anchor configuration.
 * @param rotationDegrees Optional clockwise marker rotation in degrees.
 * @param visible Whether the marker is currently visible on the map.
 * @param alpha The opacity of the marker (0.0 to 1.0). Default is 1.0.
 * @param zIndex The z-index of the marker. Default is 0.
 * @param id Unique identifier for the marker.
 * @param infoWindow Optional Compose content anchored above the marker.
 * @param isInfoWindowVisible Initial visibility state for the info window.
 * @param onClick A lambda invoked when the marker is clicked.
 */
@Composable
@MapComposable
fun Marker(
    position: LatLng,
    title: String? = null,
    snippet: String? = null,
    icon: MarkerIcon? = null,
    rotationDegrees: Float = 0f,
    visible: Boolean = true,
    alpha: Float = 1.0f,
    zIndex: Float = 0f,
    id: String = remember { UUID.randomUUID().toString() },
    infoWindow: (@Composable () -> Unit)? = null,
    isInfoWindowVisible: Boolean = false,
    onClick: () -> Boolean = { false }
) {
    Marker(
        state = rememberMarkerState(position = position),
        title = title,
        snippet = snippet,
        icon = icon,
        rotationDegrees = rotationDegrees,
        visible = visible,
        alpha = alpha,
        zIndex = zIndex,
        id = id,
        infoWindow = infoWindow,
        isInfoWindowVisible = isInfoWindowVisible,
        onClick = onClick
    )
}

/**
 * A declarative marker with a custom @Composable icon.
 *
 * This overload uses [MapOverlay] internally to render any standard Compose UI at a map
 * coordinate.
 *
 * @param state The [MarkerState] to be used to control or observe the marker's position.
 * @param iconContent The Compose content to render as the marker icon.
 * @param iconAnchorX Horizontal anchor for the custom icon. Default is 0.5f (centered).
 * @param iconAnchorY Vertical anchor for the custom icon. Default is 0.5f (centered).
 * @param zIndex The z-index of the marker. Default is 0.
 * @param id Unique identifier for the marker.
 * @param infoWindow Optional Compose content anchored above the marker.
 * @param isInfoWindowVisible Initial visibility state for the info window.
 * @param onClick A lambda invoked when the custom icon is clicked. Return `true` to consume the
 * click and keep Leaflekt from opening the marker info window automatically.
 */
@Composable
@MapComposable
fun Marker(
    state: MarkerState,
    iconContent: @Composable () -> Unit,
    iconAnchorX: Float = 0.5f,
    iconAnchorY: Float = 0.5f,
    zIndex: Float = 0f,
    id: String = remember { UUID.randomUUID().toString() },
    infoWindow: (@Composable () -> Unit)? = null,
    isInfoWindowVisible: Boolean = false,
    onClick: () -> Boolean = { false }
) {
    LaunchedEffect(isInfoWindowVisible) {
        if (isInfoWindowVisible) {
            state.showInfoWindow()
        } else {
            state.hideInfoWindow()
        }
    }

    MapOverlay(
        position = state.position,
        anchorFractionX = iconAnchorX,
        anchorFractionY = iconAnchorY,
        zIndex = zIndex
    ) {
        Box(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    if (!onClick()) {
                        state.showInfoWindow()
                    }
                }
            )
        ) {
            iconContent()
        }
    }

    if (infoWindow != null && state.isInfoWindowShown) {
        MapOverlay(
            position = state.position,
            anchorFractionX = 0.5f,
            anchorFractionY = 1f,
            zIndex = zIndex + 1f // Info window always above marker
        ) {
            infoWindow()
        }
    }
}

/**
 * A declarative marker with a custom @Composable icon.
 *
 * This is a convenience overload that takes a [LatLng] instead of a [MarkerState].
 *
 * @param position The position of the marker.
 * @param iconContent The Compose content to render as the marker icon.
 * @param iconAnchorX Horizontal anchor for the custom icon. Default is 0.5f (centered).
 * @param iconAnchorY Vertical anchor for the custom icon. Default is 0.5f (centered).
 * @param zIndex The z-index of the marker. Default is 0.
 * @param id Unique identifier for the marker.
 * @param infoWindow Optional Compose content anchored above the marker.
 * @param isInfoWindowVisible Initial visibility state for the info window.
 * @param onClick A lambda invoked when the custom icon is clicked.
 */
@Composable
@MapComposable
fun Marker(
    position: LatLng,
    iconContent: @Composable () -> Unit,
    iconAnchorX: Float = 0.5f,
    iconAnchorY: Float = 0.5f,
    zIndex: Float = 0f,
    id: String = remember { UUID.randomUUID().toString() },
    infoWindow: (@Composable () -> Unit)? = null,
    isInfoWindowVisible: Boolean = false,
    onClick: () -> Boolean = { false }
) {
    Marker(
        state = rememberMarkerState(position = position),
        iconContent = iconContent,
        iconAnchorX = iconAnchorX,
        iconAnchorY = iconAnchorY,
        zIndex = zIndex,
        id = id,
        infoWindow = infoWindow,
        isInfoWindowVisible = isInfoWindowVisible,
        onClick = onClick
    )
}

