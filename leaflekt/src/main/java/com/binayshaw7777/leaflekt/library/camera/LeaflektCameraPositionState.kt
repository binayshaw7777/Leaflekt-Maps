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

package com.binayshaw7777.leaflekt.library.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.binayshaw7777.leaflekt.library.controller.LeaflektController

/**
 * Data class representing the camera position of the Leaflekt map.
 * 
 * @property target The center coordinate of the current view.
 * @property zoom The current zoom level (typically 1.0 to 19.0).
 * @property bearing The current rotation angle in degrees (0 to 360).
 */
data class LeaflektCameraPosition(
    val target: LeaflektLatLng,
    val zoom: Double,
    val bearing: Double = 0.0
)

/**
 * Data class representing a latitude and longitude coordinate.
 */
data class LeaflektLatLng(
    val latitude: Double,
    val longitude: Double
)

/**
 * A state object that can be hoisted to control and observe the map's camera state.
 * 
 * This class follows the Google Maps Compose pattern for familiarity and consistency.
 * 
 * ### Usage Example:
 * ```kotlin
 * val cameraState = rememberLeaflektCameraPositionState()
 * 
 * Button(onClick = { 
 *     cameraState.move(LeaflektLatLng(22.5726, 88.3639), zoom = 14.0, bearing = 45.0) 
 * }) {
 *     Text("Fly to Kolkata (Rotated)")
 * }
 * 
 * LeaflektMap(cameraPositionState = cameraState)
 * ```
 */
class LeaflektCameraPositionState(
    initialPosition: LeaflektCameraPosition = LeaflektCameraPosition(
        target = LeaflektLatLng(22.5726, 88.3639),
        zoom = 12.0,
        bearing = 0.0
    )
) {
    /**
     * Whether the camera is currently moving (including panning, zooming, or rotating).
     */
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    /**
     * The internal source of truth for the camera position.
     */
    internal var rawPosition by mutableStateOf(initialPosition)

    /**
     * Current position of the camera on the map. 
     * Setting this value will immediately move the map camera.
     */
    var position: LeaflektCameraPosition
        get() = rawPosition
        set(value) {
            val controller = boundController
            if (controller == null) {
                rawPosition = value
            } else {
                controller.moveCamera(
                    lat = value.target.latitude,
                    lng = value.target.longitude,
                    zoom = value.zoom,
                    bearing = value.bearing
                )
            }
        }

    private var boundController: LeaflektController? = null

    /**
     * Binds this state object to a specific map controller.
     */
    internal fun setController(controller: LeaflektController?) {
        if (this.boundController == null && controller == null) return
        this.boundController = controller
        if (controller != null) {
            controller.moveCamera(
                lat = position.target.latitude,
                lng = position.target.longitude,
                zoom = position.zoom,
                bearing = position.bearing
            )
        } else {
            isMoving = false
        }
    }

    /**
     * Instantly moves the camera to the specified [target], [zoom], and [bearing].
     * 
     * @param target The destination coordinates.
     * @param zoom The destination zoom level. Defaults to current zoom.
     * @param bearing The destination bearing (rotation). Defaults to current bearing.
     */
    fun move(
        target: LeaflektLatLng, 
        zoom: Double = position.zoom,
        bearing: Double = position.bearing
    ) {
        this.position = LeaflektCameraPosition(target, zoom, bearing)
    }

    internal fun onCameraMoveStarted(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraMove(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraIdle(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = false
    }

    companion object {
        /**
         * The default saver implementation for [LeaflektCameraPositionState].
         */
        val Saver: Saver<LeaflektCameraPositionState, *> = listSaver(
            save = {
                listOf(
                    it.position.target.latitude,
                    it.position.target.longitude,
                    it.position.zoom,
                    it.position.bearing
                )
            },
            restore = {
                LeaflektCameraPositionState(
                    initialPosition = LeaflektCameraPosition(
                        target = LeaflektLatLng(it[0], it[1]),
                        zoom = it[2],
                        bearing = it[3]
                    )
                )
            }
        )
    }
}

/**
 * Creates and remembers a [LeaflektCameraPositionState] using [rememberSaveable].
 * 
 * @param init A lambda to configure the initial state (e.g. set starting position/zoom).
 */
@Composable
fun rememberLeaflektCameraPositionState(
    init: LeaflektCameraPositionState.() -> Unit = {}
): LeaflektCameraPositionState = rememberSaveable(saver = LeaflektCameraPositionState.Saver) {
    LeaflektCameraPositionState().apply(init)
}
