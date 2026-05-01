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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.binayshaw7777.leaflekt.library.map.LeaflektMap
import com.binayshaw7777.leaflekt.library.map.LeaflektMapComposable
import com.binayshaw7777.leaflekt.library.map.LocalLeaflektController
import com.binayshaw7777.leaflekt.library.map.LocalLeaflektMarkerClusterId
import com.binayshaw7777.leaflekt.library.marker.LeaflektMarker
import java.util.UUID

/**
 * A declarative marker cluster group that can be placed inside a [LeaflektMap] content block.
 *
 * All [LeaflektMarker]s placed inside the [content] block will be added to this cluster group.
 *
 * ### Usage Example:
 * ```kotlin
 * LeaflektMap {
 *     LeaflektMarkerCluster(
 *         options = MarkerClusterOptions(maxClusterRadius = 100)
 *     ) {
 *         LeaflektMarker(position = LeaflektLatLng(22.5726, 88.3639))
 *         LeaflektMarker(position = LeaflektLatLng(22.5800, 88.3700))
 *     }
 * }
 * ```
 *
 * @param id Unique identifier for the cluster group. If not provided, a random UUID will be generated.
 * @param options Configuration options for the marker cluster group.
 * @param onClusterClick Callback invoked when a cluster is clicked.
 * @param content The markers to be clustered.
 */
@Composable
@LeaflektMapComposable
fun LeaflektMarkerCluster(
    id: String = remember { UUID.randomUUID().toString() },
    options: MarkerClusterOptions = MarkerClusterOptions(),
    onClusterClick: ((lat: Double, lng: Double, count: Int) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val controller = LocalLeaflektController.current ?: return

    DisposableEffect(id, options) {
        controller.createMarkerClusterGroup(id, options)
        onDispose {
            controller.removeMarkerClusterGroup(id)
        }
    }

    DisposableEffect(id, onClusterClick) {
        if (onClusterClick != null) {
            controller.registerClusterClick(id, onClusterClick)
        }
        onDispose {
            controller.unregisterClusterClick(id)
        }
    }

    CompositionLocalProvider(LocalLeaflektMarkerClusterId provides id) {
        content()
    }
}
