package com.binayshaw7777.leaflekt.library.polygon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng
import com.binayshaw7777.leaflekt.library.map.LeaflektMap
import com.binayshaw7777.leaflekt.library.map.LeaflektMapComposable
import com.binayshaw7777.leaflekt.library.map.LocalLeaflektController
import com.binayshaw7777.leaflekt.library.shape.DefaultLeaflektSelectedStrokeColor
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern
import com.binayshaw7777.leaflekt.library.shape.SelectedLeaflektMinimumFillOpacity
import com.binayshaw7777.leaflekt.library.shape.SelectedLeaflektZIndexBoost
import kotlin.math.max
import java.util.UUID

/**
 * A declarative polygon overlay that can be placed inside a [LeaflektMap] content block.
 *
 * This component follows the Jetpack Compose declarative pattern. It adds a polygon to the
 * underlying Leaflet.js map when it enters the composition and removes it when it leaves.
 *
 * ### Usage Example:
 * ```kotlin
 * LeaflektMap {
 *     LeaflektPolygon(
 *         points = listOf(
 *             LeaflektLatLng(22.57, 88.36),
 *             LeaflektLatLng(22.58, 88.37),
 *             LeaflektLatLng(22.56, 88.38)
 *         ),
 *         fillColor = Color.Green.copy(alpha = 0.2f),
 *         strokeColor = Color.DarkGray
 *     )
 * }
 * ```
 *
 * @param state The [LeaflektPolygonState] to be used to control or observe the polygon's properties.
 * @param clickable Whether the polygon is interactive and can receive click events.
 * @param fillColor The color used to fill the polygon's interior.
 * @param geodesic Whether the polygon edges follow a geodesic path. Note: Leaflet currently
 * renders these as projected segments.
 * @param strokeColor The color of the polygon's outline.
 * @param strokePattern Optional dash/gap/dot pattern for the outline.
 * @param strokeWidth The width of the polygon's outline in pixels.
 * @param visible Whether the polygon is currently visible on the map.
 * @param zIndex The drawing order of the polygon. Higher values are drawn on top.
 * @param fillOpacity The opacity of the interior fill (0.0 to 1.0).
 * @param strokeOpacity The opacity of the outline (0.0 to 1.0).
 * @param selectedStrokeColor The stroke color to use when the polygon's state is selected.
 * @param selectedStrokeWidth The stroke width to use when the polygon's state is selected.
 * @param selectedFillOpacity The fill opacity to use when the polygon's state is selected.
 * @param selectedZIndexBoost The z-index boost applied when the polygon is selected.
 * @param id Unique identifier for the polygon. If not provided, a random UUID will be generated.
 * @param onClick A lambda invoked when the polygon is clicked. Return `true` to consume the click
 * event and prevent it from propagating to the map below. Return `false` (default) to allow
 * the event to bubble up.
 */
@Composable
@LeaflektMapComposable
fun LeaflektPolygon(
    state: LeaflektPolygonState = rememberLeaflektPolygonState(),
    clickable: Boolean = false,
    fillColor: Color = Color.Transparent,
    geodesic: Boolean = false,
    strokeColor: Color = Color.Black,
    strokePattern: List<LeaflektStrokePattern>? = null,
    strokeWidth: Float = 10f,
    visible: Boolean = true,
    zIndex: Float = 0f,
    fillOpacity: Float = 0.2f,
    strokeOpacity: Float = 1f,
    selectedStrokeColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedStrokeWidth: Float = strokeWidth + SelectedLeaflektPolygonStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    val controller = LocalLeaflektController.current ?: return
    if (state.points.size < 3) {
        return
    }
    val resolvedStrokeColor = if (state.isSelected) selectedStrokeColor else strokeColor
    val resolvedStrokeWidth = if (state.isSelected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOpacity = if (state.isSelected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolygon(
            LeaflektPolygonInfo(
                id = id,
                points = state.points,
                clickable = clickable,
                fillColor = fillColor,
                geodesic = geodesic,
                holes = state.holes,
                strokeColor = resolvedStrokeColor,
                strokePattern = strokePattern,
                strokeWidth = resolvedStrokeWidth,
                visible = visible,
                zIndex = resolvedZIndex,
                fillOpacity = resolvedFillOpacity,
                strokeOpacity = strokeOpacity
            )
        )
        controller.registerPolygonClick(id, onClick)

        onDispose {
            controller.unregisterPolygonClick(id)
            controller.removePolygon(id)
        }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolygonClick(id, onClick)
        onDispose {
            controller.unregisterPolygonClick(id)
        }
    }

    LaunchedEffect(
        state.points,
        state.holes,
        state.isSelected,
        clickable,
        fillColor,
        geodesic,
        strokeColor,
        strokePattern,
        strokeWidth,
        visible,
        zIndex,
        fillOpacity,
        strokeOpacity,
        selectedStrokeColor,
        selectedStrokeWidth,
        selectedFillOpacity,
        selectedZIndexBoost
    ) {
        controller.updatePolygon(
            LeaflektPolygonInfo(
                id = id,
                points = state.points,
                clickable = clickable,
                fillColor = fillColor,
                geodesic = geodesic,
                holes = state.holes,
                strokeColor = resolvedStrokeColor,
                strokePattern = strokePattern,
                strokeWidth = resolvedStrokeWidth,
                visible = visible,
                zIndex = resolvedZIndex,
                fillOpacity = resolvedFillOpacity,
                strokeOpacity = strokeOpacity
            )
        )
    }
}

/**
 * A declarative polygon overlay that can be placed inside a [LeaflektMap] content block.
 *
 * This is a convenience overload that takes a list of points instead of a [LeaflektPolygonState].
 * Use this for static polygons that don't need programmatic movement or selection control.
 *
 * @param points The coordinates defining the polygon's outer boundary.
 * @param clickable Whether the polygon is interactive.
 * @param fillColor The interior fill color.
 * @param geodesic Whether edges follow a geodesic path.
 * @param holes Optional lists of coordinates defining holes within the polygon.
 * @param strokeColor The outline color.
 * @param strokePattern Optional dash/gap/dot pattern.
 * @param strokeWidth The outline width in pixels.
 * @param visible Visibility toggle.
 * @param zIndex The drawing order.
 * @param fillOpacity Interior fill opacity (0.0 to 1.0).
 * @param strokeOpacity Outline opacity (0.0 to 1.0).
 * @param selected Whether the polygon starts in a highlighted "selected" state.
 * @param selectedStrokeColor Highlight stroke color.
 * @param selectedStrokeWidth Highlight stroke width.
 * @param selectedFillOpacity Highlight fill opacity.
 * @param selectedZIndexBoost Highlight z-index boost.
 * @param id Unique identifier.
 * @param onClick Click handler. Return `true` to consume the event.
 */
@Composable
@LeaflektMapComposable
fun LeaflektPolygon(
    points: List<LeaflektLatLng>,
    clickable: Boolean = false,
    fillColor: Color = Color.Transparent,
    geodesic: Boolean = false,
    holes: List<List<LeaflektLatLng>> = emptyList(),
    strokeColor: Color = Color.Black,
    strokePattern: List<LeaflektStrokePattern>? = null,
    strokeWidth: Float = 10f,
    visible: Boolean = true,
    zIndex: Float = 0f,
    fillOpacity: Float = 0.2f,
    strokeOpacity: Float = 1f,
    selected: Boolean = false,
    selectedStrokeColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedStrokeWidth: Float = strokeWidth + SelectedLeaflektPolygonStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    LeaflektPolygon(
        state = rememberLeaflektPolygonState(points = points, holes = holes).apply { isSelected = selected },
        clickable = clickable,
        fillColor = fillColor,
        geodesic = geodesic,
        strokeColor = strokeColor,
        strokePattern = strokePattern,
        strokeWidth = strokeWidth,
        visible = visible,
        zIndex = zIndex,
        fillOpacity = fillOpacity,
        strokeOpacity = strokeOpacity,
        selectedStrokeColor = selectedStrokeColor,
        selectedStrokeWidth = selectedStrokeWidth,
        selectedFillOpacity = selectedFillOpacity,
        selectedZIndexBoost = selectedZIndexBoost,
        id = id,
        onClick = onClick
    )
}

private const val SelectedLeaflektPolygonStrokeWidthBoost = 2f
