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

/**
 * Data class representing the camera position of the map.
 * 
 * @property target The center coordinate of the current view.
 * @property zoom The current zoom level (typically 1.0 to 19.0).
 * @property bearing The current rotation angle in degrees (0 to 360).
 */
data class CameraPosition(
    val target: LatLng,
    val zoom: Double,
    val bearing: Double = 0.0
)
