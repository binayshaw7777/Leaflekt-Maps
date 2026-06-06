package com.binayshaw7777.leaflekt.library.polyline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.map.MapView
import com.binayshaw7777.leaflekt.library.map.MapComposable
import com.binayshaw7777.leaflekt.library.map.LocalMapController
import com.binayshaw7777.leaflekt.library.shape.DefaultLeaflektSelectedStrokeColor
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern
import com.binayshaw7777.leaflekt.library.shape.SelectedLeaflektZIndexBoost
import kotlin.math.max
import java.util.UUID

/**
 * A declarative polyline overlay that can be placed inside a [MapView] content block.
 *
 * This component follows the Jetpack Compose declarative pattern. It adds a polyline to the
 * underlying Leaflet.js map when it enters the composition and removes it when it leaves.
 *
 * ### Usage Example:
 * ```kotlin
 * MapView {
 *     Polyline(
 *         points = listOf(
 *             LatLng(22.57, 88.36),
 *             LatLng(22.58, 88.37)
 *         ),
 *         color = Color.Blue,
 *         width = 4f
 *     )
 * }
 * ```
 *
 * @param state The [PolylineState] to be used to control or observe the polyline's properties.
 * @param clickable Whether the polyline is interactive and can receive click events.
 * @param color The color of the polyline.
 * @param geodesic Whether the polyline edges follow a geodesic path. Note: Leaflet currently
 * renders these as projected segments.
 * @param pattern Optional dash/gap/dot pattern for the line.
 * @param visible Whether the polyline is currently visible on the map.
 * @param width The width of the polyline in pixels.
 * @param zIndex The drawing order of the polyline. Higher values are drawn on top.
 * @param alpha The opacity of the polyline (0.0 to 1.0).
 * @param selectedColor The color to use when the polyline's state is selected.
 * @param selectedWidth The width to use when the polyline's state is selected.
 * @param selectedZIndexBoost The z-index boost applied when the polyline is selected.
 * @param id Unique identifier for the polyline. If not provided, a random UUID will be generated.
 * @param onClick A lambda invoked when the polyline is clicked. Return `true` to consume the click
 * event and prevent it from propagating to the map below. Return `false` (default) to allow
 * the event to bubble up.
 */
@Composable
@MapComposable
fun Polyline(
    state: PolylineState = rememberPolylineState(),
    clickable: Boolean = false,
    color: Color = Color.Black,
    geodesic: Boolean = false,
    pattern: List<LeaflektStrokePattern>? = null,
    visible: Boolean = true,
    width: Float = 10f,
    zIndex: Float = 0f,
    alpha: Float = 1f,
    selectedColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedWidth: Float = width + SelectedLeaflektStrokeWidthBoost,
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    val controller = LocalMapController.current ?: return
    if (state.points.isEmpty()) {
        return
    }
    val resolvedColor = if (state.isSelected) selectedColor else color
    val resolvedWidth = if (state.isSelected) max(selectedWidth, width) else width
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolyline(
            PolylineInfo(
                id = id,
                points = state.points,
                clickable = clickable,
                color = resolvedColor,
                geodesic = geodesic,
                pattern = pattern,
                visible = visible,
                width = resolvedWidth,
                zIndex = resolvedZIndex,
                alpha = alpha
            )
        )
        controller.registerPolylineClick(id, onClick)

        onDispose {
            controller.unregisterPolylineClick(id)
            controller.removePolyline(id)
        }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolylineClick(id, onClick)
        onDispose {
            controller.unregisterPolylineClick(id)
        }
    }

    LaunchedEffect(
        state.points,
        state.isSelected,
        clickable,
        color,
        geodesic,
        pattern,
        visible,
        width,
        zIndex,
        alpha,
        selectedColor,
        selectedWidth,
        selectedZIndexBoost
    ) {
        controller.updatePolyline(
            PolylineInfo(
                id = id,
                points = state.points,
                clickable = clickable,
                color = resolvedColor,
                geodesic = geodesic,
                pattern = pattern,
                visible = visible,
                width = resolvedWidth,
                zIndex = resolvedZIndex,
                alpha = alpha
            )
        )
    }
}

/**
 * A declarative polyline overlay that can be placed inside a [MapView] content block.
 *
 * This is a convenience overload that takes a list of points instead of a [PolylineState].
 * Use this for static polylines that don't need programmatic movement or selection control.
 *
 * @param points The coordinates defining the polyline's path.
 * @param clickable Whether the polyline is interactive.
 * @param color The line color.
 * @param geodesic Whether edges follow a geodesic path.
 * @param pattern Optional dash/gap/dot pattern.
 * @param visible Visibility toggle.
 * @param width The line width in pixels.
 * @param zIndex The drawing order.
 * @param alpha Opacity (0.0 to 1.0).
 * @param selected Whether the polyline starts in a highlighted "selected" state.
 * @param selectedColor Highlight color.
 * @param selectedWidth Highlight width.
 * @param selectedZIndexBoost Highlight z-index boost.
 * @param id Unique identifier.
 * @param onClick Click handler. Return `true` to consume the event.
 */
@Composable
@MapComposable
fun Polyline(
    points: List<LatLng>,
    clickable: Boolean = false,
    color: Color = Color.Black,
    geodesic: Boolean = false,
    pattern: List<LeaflektStrokePattern>? = null,
    visible: Boolean = true,
    width: Float = 10f,
    zIndex: Float = 0f,
    alpha: Float = 1f,
    selected: Boolean = false,
    selectedColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedWidth: Float = width + SelectedLeaflektStrokeWidthBoost,
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    Polyline(
        state = rememberPolylineState(points = points).apply { isSelected = selected },
        clickable = clickable,
        color = color,
        geodesic = geodesic,
        pattern = pattern,
        visible = visible,
        width = width,
        zIndex = zIndex,
        alpha = alpha,
        selectedColor = selectedColor,
        selectedWidth = selectedWidth,
        selectedZIndexBoost = selectedZIndexBoost,
        id = id,
        onClick = onClick
    )
}

private const val SelectedLeaflektStrokeWidthBoost = 4f

