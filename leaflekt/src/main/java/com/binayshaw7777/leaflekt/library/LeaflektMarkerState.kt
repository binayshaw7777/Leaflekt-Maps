package com.binayshaw7777.leaflekt.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A state object that can be hoisted to control and observe the marker state.
 *
 * This implementation follows the Google Maps Compose pattern to provide a familiar API
 * for Kotlin developers using Leaflekt.
 *
 * ### Usage Example:
 * ```kotlin
 * val markerState = rememberLeaflektMarkerState(
 *     position = LeaflektLatLng(22.5726, 88.3639)
 * )
 *
 * LeaflektMarker(
 *     state = markerState,
 *     title = "Kolkata"
 * )
 *
 * LaunchedEffect(Unit) {
 *     markerState.position = LeaflektLatLng(22.5850, 88.3900)
 *     markerState.showInfoWindow()
 * }
 * ```
 *
 * @param position the initial marker position
 */
class LeaflektMarkerState(position: LeaflektLatLng = LeaflektLatLng(0.0, 0.0)) {
    /**
     * Current position of the marker.
     *
     * This property is backed by Compose state. It can be updated by the API user
     * to move the marker on the map.
     */
    var position: LeaflektLatLng by mutableStateOf(position)

    internal var isInfoWindowShown: Boolean by mutableStateOf(false)
        private set

    private var showInfoWindowAction: (() -> Unit)? = null
    private var hideInfoWindowAction: (() -> Unit)? = null

    /**
     * Shows the info window for the underlying marker.
     *
     * This opens the default Leaflet popup when [LeaflektMarker] uses `title` and `snippet`,
     * or reveals the custom Compose info window when [LeaflektMarker] uses `infoWindow`.
     */
    fun showInfoWindow() {
        isInfoWindowShown = true
        showInfoWindowAction?.invoke()
    }

    /**
     * Hides the info window for the underlying marker.
     *
     * This closes whichever info window implementation is currently attached to the marker.
     */
    fun hideInfoWindow() {
        isInfoWindowShown = false
        hideInfoWindowAction?.invoke()
    }

    internal fun bindInfoWindowActions(
        showInfoWindow: (() -> Unit)?,
        hideInfoWindow: (() -> Unit)?
    ) {
        showInfoWindowAction = showInfoWindow
        hideInfoWindowAction = hideInfoWindow
    }

    companion object {
        /**
         * The default saver implementation for [LeaflektMarkerState].
         */
        val Saver: Saver<LeaflektMarkerState, *> = Saver(
            save = { listOf(it.position.latitude, it.position.longitude) },
            restore = { 
                LeaflektMarkerState(LeaflektLatLng(it[0], it[1]))
            }
        )
    }
}

/**
 * Creates and [rememberSaveable]s a [LeaflektMarkerState].
 *
 * This is the preferred way to create marker state in Compose screens.
 *
 * ### Usage Example:
 * ```kotlin
 * val markerState = rememberLeaflektMarkerState(
 *     position = LeaflektLatLng(12.9716, 77.5946)
 * )
 * ```
 *
 * @param key optional key for the saved state
 * @param position the initial marker position
 */
@Composable
fun rememberLeaflektMarkerState(
    key: String? = null,
    position: LeaflektLatLng = LeaflektLatLng(0.0, 0.0)
): LeaflektMarkerState = rememberSaveable(key = key, saver = LeaflektMarkerState.Saver) {
    LeaflektMarkerState(position)
}
