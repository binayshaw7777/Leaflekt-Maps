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

package com.binayshaw7777.leaflekt.library.map

import com.binayshaw7777.leaflekt.library.location.LeaflektCurrentLocationIcon

import java.util.Objects

/**
 * Default UI settings for the map. All gestures and standard controls are enabled by default.
 */
val DefaultLeaflektMapUiSettings: LeaflektMapUiSettings = LeaflektMapUiSettings()

/**
 * Data-like class for UI-related settings on the Leaflekt map.
 *
 * This class allows developers to toggle various interaction gestures and UI elements.
 * 
 * ### Usage Example:
 * ```kotlin
 * LeaflektMap(
 *     uiSettings = LeaflektMapUiSettings(
 *         zoomControlsEnabled = false,
 *         scrollGesturesEnabled = true,
 *         zoomGesturesEnabled = false
 *     )
 * )
 * ```
 *
 * @param zoomControlsEnabled Whether the zoom +/- buttons are visible.
 * @param scrollGesturesEnabled Whether the user can pan the map by dragging.
 * @param zoomGesturesEnabled Whether the user can zoom via pinch or double-tap.
 * @param rotateGesturesEnabled Whether the user can rotate the map via pinch gestures.
 * @param myLocationButtonEnabled Reserved for future use: visibility of the GPS button.
 */
class LeaflektMapUiSettings(
    val zoomControlsEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val rotateGesturesEnabled: Boolean = true,
    val showCurrentLocation: Boolean = false,
    val currentLocationIcon: LeaflektCurrentLocationIcon? = null,
    val myLocationButtonEnabled: Boolean = false,
) {
    override fun toString(): String = "LeaflektMapUiSettings(" +
            "zoomControlsEnabled=$zoomControlsEnabled, " +
            "scrollGesturesEnabled=$scrollGesturesEnabled, " +
            "zoomGesturesEnabled=$zoomGesturesEnabled, " +
            "rotateGesturesEnabled=$rotateGesturesEnabled, " +
            "showCurrentLocation=$showCurrentLocation, " +
            "hasCurrentLocationIcon=${currentLocationIcon != null}, " +
            "myLocationButtonEnabled=$myLocationButtonEnabled)"

    override fun equals(other: Any?): Boolean = other is LeaflektMapUiSettings &&
            zoomControlsEnabled == other.zoomControlsEnabled &&
            scrollGesturesEnabled == other.scrollGesturesEnabled &&
            zoomGesturesEnabled == other.zoomGesturesEnabled &&
            rotateGesturesEnabled == other.rotateGesturesEnabled &&
            showCurrentLocation == other.showCurrentLocation &&
            currentLocationIcon == other.currentLocationIcon &&
            myLocationButtonEnabled == other.myLocationButtonEnabled

    override fun hashCode(): Int = Objects.hash(
        zoomControlsEnabled,
        scrollGesturesEnabled,
        zoomGesturesEnabled,
        rotateGesturesEnabled,
        showCurrentLocation,
        currentLocationIcon,
        myLocationButtonEnabled
    )

    /**
     * Returns a copy of this [LeaflektMapUiSettings] with the specified properties updated.
     */
    fun copy(
        zoomControlsEnabled: Boolean = this.zoomControlsEnabled,
        scrollGesturesEnabled: Boolean = this.scrollGesturesEnabled,
        zoomGesturesEnabled: Boolean = this.zoomGesturesEnabled,
        rotateGesturesEnabled: Boolean = this.rotateGesturesEnabled,
        showCurrentLocation: Boolean = this.showCurrentLocation,
        currentLocationIcon: LeaflektCurrentLocationIcon? = this.currentLocationIcon,
        myLocationButtonEnabled: Boolean = this.myLocationButtonEnabled,
    ): LeaflektMapUiSettings = LeaflektMapUiSettings(
        zoomControlsEnabled = zoomControlsEnabled,
        scrollGesturesEnabled = scrollGesturesEnabled,
        zoomGesturesEnabled = zoomGesturesEnabled,
        rotateGesturesEnabled = rotateGesturesEnabled,
        showCurrentLocation = showCurrentLocation,
        currentLocationIcon = currentLocationIcon,
        myLocationButtonEnabled = myLocationButtonEnabled
    )
}
