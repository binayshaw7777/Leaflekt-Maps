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
import com.binayshaw7777.leaflekt.library.controller.MapController

/**
 * A state object that can be hoisted to control and observe the map's camera state.
 * 
 * This class follows the Google Maps Compose pattern for familiarity and consistency.
 * 
 * ### Usage Example:
 * ```kotlin
 * val cameraState = rememberCameraPositionState()
 * 
 * Button(onClick = { 
 *     cameraState.move(LatLng(22.5726, 88.3639), zoom = 14.0, bearing = 45.0) 
 * }) {
 *     Text("Fly to Kolkata (Rotated)")
 * }
 * 
 * MapView(cameraPositionState = cameraState)
 * ```
 */
class CameraPositionState(
    initialPosition: CameraPosition = CameraPosition(
        target = LatLng(22.5726, 88.3639),
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
    var position: CameraPosition
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

    private var boundController: MapController? = null

    /**
     * Binds this state object to a specific map controller.
     */
    internal fun setController(controller: MapController?) {
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
        target: LatLng, 
        zoom: Double = position.zoom,
        bearing: Double = position.bearing
    ) {
        this.position = CameraPosition(target, zoom, bearing)
    }

    internal fun onCameraMoveStarted(position: CameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraMove(position: CameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraIdle(position: CameraPosition) {
        rawPosition = position
        isMoving = false
    }

    companion object {
        /**
         * The default saver implementation for [CameraPositionState].
         */
        val Saver: Saver<CameraPositionState, *> = listSaver(
            save = {
                listOf(
                    it.position.target.latitude,
                    it.position.target.longitude,
                    it.position.zoom,
                    it.position.bearing
                )
            },
            restore = {
                CameraPositionState(
                    initialPosition = CameraPosition(
                        target = LatLng(it[0], it[1]),
                        zoom = it[2],
                        bearing = it[3]
                    )
                )
            }
        )
    }
}

/**
 * Creates and remembers a [CameraPositionState] using [rememberSaveable].
 * 
 * @param init A lambda to configure the initial state (e.g. set starting position/zoom).
 */
@Composable
fun rememberCameraPositionState(
    init: CameraPositionState.() -> Unit = {}
): CameraPositionState = rememberSaveable(saver = CameraPositionState.Saver) {
    CameraPositionState().apply(init)
}

