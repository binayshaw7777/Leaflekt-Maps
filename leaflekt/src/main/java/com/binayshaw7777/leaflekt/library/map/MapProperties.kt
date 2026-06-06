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

import java.util.Objects

/**
 * Configuration properties for the Leaflekt map.
 *
 * @property mapStyle The visual tile style to display.
 * @param automaticThemeSync Whether the map should automatically switch between light and dark
 * tile providers based on the system theme.
 * @property minZoom Minimum allowed zoom level. Prevents zooming out to blank space.
 * Defaults to 2.0 (shows most of the world, but prevents extreme zoom-out).
 * @property maxZoom Maximum allowed zoom level. Prevents over-zooming beyond tile detail.
 * Defaults to 19.0 (typical max for most tile providers).
 */
class MapProperties(
    val mapStyle: MapStyle = MapStyle.OpenStreetMap,
    val automaticThemeSync: Boolean = false,
    val minZoom: Double = 2.0,
    val maxZoom: Double = 19.0,
    val restrictToWorldBounds: Boolean = true
) {
    override fun toString(): String = "MapProperties(" +
            "mapStyle=$mapStyle, " +
            "automaticThemeSync=$automaticThemeSync, " +
            "minZoom=$minZoom, " +
            "maxZoom=$maxZoom, " +
            "restrictToWorldBounds=$restrictToWorldBounds)"

    override fun equals(other: Any?): Boolean =
        other is MapProperties &&
                mapStyle == other.mapStyle &&
                automaticThemeSync == other.automaticThemeSync &&
                minZoom == other.minZoom &&
                maxZoom == other.maxZoom &&
                restrictToWorldBounds == other.restrictToWorldBounds

    override fun hashCode(): Int = Objects.hash(mapStyle, automaticThemeSync, minZoom, maxZoom, restrictToWorldBounds)

    /**
     * Returns a copy of this [MapProperties] with the specified properties updated.
     */
    fun copy(
        mapStyle: MapStyle = this.mapStyle,
        automaticThemeSync: Boolean = this.automaticThemeSync,
        minZoom: Double = this.minZoom,
        maxZoom: Double = this.maxZoom,
        restrictToWorldBounds: Boolean = this.restrictToWorldBounds
    ): MapProperties = MapProperties(
        mapStyle = mapStyle,
        automaticThemeSync = automaticThemeSync,
        minZoom = minZoom,
        maxZoom = maxZoom,
        restrictToWorldBounds = restrictToWorldBounds
    )
}

val DefaultMapProperties: MapProperties = MapProperties()

