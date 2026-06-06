package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

@Composable
@LeaflektMapComposable
fun LeaflektMarkerCluster(
    id: String = remember { generateId() },
    markers: List<LeaflektMarkerInfo>,
    maxClusterRadius: Int = 80
) {
    val controller = LocalLeaflektController.current ?: return

    DisposableEffect(id, maxClusterRadius) {
        controller.createClusterGroup(id, maxClusterRadius)
        onDispose { controller.removeClusterGroup(id) }
    }

    DisposableEffect(id, markers) {
        if (markers.isNotEmpty()) controller.addMarkersToCluster(id, markers)
        onDispose {}
    }
}
