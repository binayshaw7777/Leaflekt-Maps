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

import java.util.Objects

/**
 * Default properties for the map. Uses OpenStreetMap as the base style.
 */
val DefaultLeaflektMapProperties: LeaflektMapProperties = LeaflektMapProperties()

/**
 * Data-like class for map-level properties that can be set on a [LeaflektMap].
 *
 * @param mapStyle The [LeaflektMapStyle] (tile provider) to use for the map. 
 *                 Ignored if [automaticThemeSync] is true.
 * @param automaticThemeSync Whether the map should automatically switch between light and dark 
 *                           tile providers based on the system theme.
 */
class LeaflektMapProperties(
    val mapStyle: LeaflektMapStyle = LeaflektMapStyle.OpenStreetMap,
    val automaticThemeSync: Boolean = false
) {
    override fun toString(): String = "LeaflektMapProperties(" +
            "mapStyle=$mapStyle, " +
            "automaticThemeSync=$automaticThemeSync)"

    override fun equals(other: Any?): Boolean =
        other is LeaflektMapProperties && 
                mapStyle == other.mapStyle &&
                automaticThemeSync == other.automaticThemeSync

    override fun hashCode(): Int = Objects.hash(mapStyle, automaticThemeSync)

    /**
     * Returns a copy of this [LeaflektMapProperties] with the specified properties updated.
     */
    fun copy(
        mapStyle: LeaflektMapStyle = this.mapStyle,
        automaticThemeSync: Boolean = this.automaticThemeSync
    ): LeaflektMapProperties = LeaflektMapProperties(
        mapStyle = mapStyle,
        automaticThemeSync = automaticThemeSync
    )
}
