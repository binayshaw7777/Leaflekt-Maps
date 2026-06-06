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

package com.binayshaw7777.leaflekt.library.cluster

import kotlinx.serialization.Serializable

/**
 * Options for configuring a marker cluster group.
 * 
 * @param showCoverageOnHover When you mouse over a cluster it shows the bounds of its markers.
 * @param zoomToBoundsOnClick When you click a cluster we zoom to its bounds.
 * @param spiderfyOnMaxZoom When you click a cluster at the maximum zoom level we spiderfy it so
 * you can see all of its markers.
 * @param disableClusteringAtZoom If set, at this zoom level and below, markers will not be
 * clustered.
 * @param maxClusterRadius The maximum radius that a cluster will cover from the central marker
 * (in pixels). Default 80.
 */
@Serializable
data class MarkerClusterOptions(
    val showCoverageOnHover: Boolean = false,
    val zoomToBoundsOnClick: Boolean = true,
    val spiderfyOnMaxZoom: Boolean = true,
    val disableClusteringAtZoom: Int? = null,
    val maxClusterRadius: Int = 80
)
