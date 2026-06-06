package com.binayshaw7777.leaflekt.library.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.map.LocalMapController
import kotlin.math.roundToInt

/**
 * A declarative component that allows pinning any standard @Composable content to map coordinates.
 *
 * Unlike [Marker], which uses Leaflet's internal marker system, [MapOverlay]
 * renders content directly in the Compose layer, allowing for full use of Compose animations,
 * interactions, and complex UI components.
 *
 * ### Usage Example:
 * ```kotlin
 * MapOverlay(
 *     position = LatLng(22.5726, 88.3639),
 *     anchorFractionX = 0.5f,
 *     anchorFractionY = 1f
 * ) {
 *     Surface(shape = RoundedCornerShape(20.dp), tonalElevation = 6.dp) {
 *         Text(
 *             text = "Pinned Compose content",
 *             modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
 *         )
 *     }
 * }
  * ```
  *
  * @param position The [com.binayshaw7777.leaflekt.library.camera.LatLng] where this overlay should be pinned.
  * @param anchorFractionX Horizontal anchor fraction. `0f` aligns the content start with the
  * map point, `0.5f` centers it, and `1f` aligns the content end with the map point.
  * Default: `0.5f` (centered horizontally).
  * @param anchorFractionY Vertical anchor fraction. `0f` aligns the content top with the map
  * point and `1f` aligns the content bottom with the map point. Default: `1f` (bottom-aligned,
  * typical for info windows above a marker).
  * @param zIndex The z-index of the overlay. Higher values are drawn on top.
  * @param content The @Composable content to render at this position.
 */
@Composable
fun MapOverlay(
    position: LatLng,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 1f,
    zIndex: Float = 0f,
    content: @Composable () -> Unit
) {
    val controller = LocalMapController.current ?: return
    var contentWidthPx by remember(position) { mutableStateOf(0) }
    var contentHeightPx by remember(position) { mutableStateOf(0) }
    var overlayHostWidthPx by remember { mutableStateOf(0) }
    var overlayHostHeightPx by remember { mutableStateOf(0) }

    DisposableEffect(position, controller) {
        controller.registerOverlayPoint(position)
        onDispose {
            controller.unregisterOverlayPoint(position)
        }
    }

    val overlayProjection = controller.projectionState[position]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                overlayHostWidthPx = size.width
                overlayHostHeightPx = size.height
            }
    ) {
        if (overlayProjection != null && overlayHostWidthPx > 0 && overlayHostHeightPx > 0) {
            val x = overlayHostWidthPx * overlayProjection.xFraction
            val y = overlayHostHeightPx * overlayProjection.yFraction

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (x - contentWidthPx * anchorFractionX).roundToInt(),
                            y = (y - contentHeightPx * anchorFractionY).roundToInt()
                        )
                    }
                    .onSizeChanged { size ->
                        contentWidthPx = size.width
                        contentHeightPx = size.height
                    }
                    .zIndex(zIndex)
            ) {
                content()
            }
        }
    }
}

