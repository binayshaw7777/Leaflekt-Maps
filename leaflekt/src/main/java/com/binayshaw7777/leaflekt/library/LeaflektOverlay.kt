package com.binayshaw7777.leaflekt.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * A declarative component that allows pinning any standard @Composable content to map coordinates.
 *
 * Unlike [LeaflektMarker], which uses Leaflet's internal marker system, [LeaflektOverlay]
 * renders content directly in the Compose layer, allowing for full use of Compose animations,
 * interactions, and complex UI components.
 *
 * @param position The [LeaflektLatLng] where this overlay should be pinned.
 * @param content The @Composable content to render at this position.
 */
@Composable
fun LeaflektOverlay(
    position: LeaflektLatLng,
    content: @Composable () -> Unit
) {
    val controller = LocalLeaflektController.current ?: return

    // Register this point with the JS bridge to get projection updates
    DisposableEffect(position, controller) {
        controller.registerOverlayPoint(position)
        onDispose {
            controller.unregisterOverlayPoint(position)
        }
    }

    val screenPoint = controller.projectionState[position]

    if (screenPoint != null) {
        val x = screenPoint.first
        val y = screenPoint.second
        
        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = x.roundToInt(),
                    y = y.roundToInt()
                )
            }
        ) {
            content()
        }
    }
}
