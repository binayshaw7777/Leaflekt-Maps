package com.binayshaw7777.leaflekt.library.circle

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
import com.binayshaw7777.leaflekt.library.shape.SelectedLeaflektMinimumFillOpacity
import com.binayshaw7777.leaflekt.library.shape.SelectedLeaflektZIndexBoost
import kotlin.math.max
import java.util.UUID

/**
 * A declarative circle overlay that can be placed inside a [MapView] content block.
 *
 * This component follows the Jetpack Compose declarative pattern. It adds a circle to the
 * underlying Leaflet.js map when it enters the composition and removes it when it leaves.
 *
 * ### Usage Example:
 * ```kotlin
 * MapView {
 *     Circle(
 *         center = LatLng(22.5726, 88.3639),
 *         radiusMeters = 500.0,
 *         fillColor = Color.Blue.copy(alpha = 0.3f),
 *         strokeColor = Color.Blue,
 *         strokeWidth = 2f
 *     )
 * }
 * ```
 *
 * @param state The [CircleState] to be used to control or observe the circle's properties.
 * @param clickable Whether the circle is interactive and can receive click events.
 * @param fillColor The color used to fill the circle's interior.
 * @param strokeColor The color of the circle's outline.
 * @param strokePattern Optional dash/gap/dot pattern for the outline.
 * @param strokeWidth The width of the circle's outline in pixels.
 * @param visible Whether the circle is currently visible on the map.
 * @param zIndex The drawing order of the circle. Higher values are drawn on top.
 * @param fillOpacity The opacity of the interior fill (0.0 to 1.0).
 * @param strokeOpacity The opacity of the outline (0.0 to 1.0).
 * @param selectedStrokeColor The stroke color to use when the circle's state is selected.
 * @param selectedStrokeWidth The stroke width to use when the circle's state is selected.
 * @param selectedFillOpacity The fill opacity to use when the circle's state is selected.
 * @param selectedZIndexBoost The z-index boost applied when the circle is selected.
 * @param id Unique identifier for the circle. If not provided, a random UUID will be generated.
 * @param onClick A lambda invoked when the circle is clicked. Return `true` to consume the click
 * event and prevent it from propagating to the map below. Return `false` (default) to allow
 * the event to bubble up.
 */
@Composable
@MapComposable
fun Circle(
    state: CircleState = rememberCircleState(),
    clickable: Boolean = false,
    fillColor: Color = Color.Transparent,
    strokeColor: Color = Color.Black,
    strokePattern: List<LeaflektStrokePattern>? = null,
    strokeWidth: Float = 10f,
    visible: Boolean = true,
    zIndex: Float = 0f,
    fillOpacity: Float = 0.2f,
    strokeOpacity: Float = 1f,
    selectedStrokeColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedStrokeWidth: Float = strokeWidth + SelectedCircleStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    val controller = LocalMapController.current ?: return
    val resolvedStrokeColor = if (state.isSelected) selectedStrokeColor else strokeColor
    val resolvedStrokeWidth = if (state.isSelected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOpacity = if (state.isSelected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addCircle(
            CircleInfo(
                id = id,
                center = state.center,
                clickable = clickable,
                fillColor = fillColor,
                radiusMeters = state.radiusMeters,
                strokeColor = resolvedStrokeColor,
                strokePattern = strokePattern,
                strokeWidth = resolvedStrokeWidth,
                visible = visible,
                zIndex = resolvedZIndex,
                fillOpacity = resolvedFillOpacity,
                strokeOpacity = strokeOpacity
            )
        )
        controller.registerCircleClick(id, onClick)

        onDispose {
            controller.unregisterCircleClick(id)
            controller.removeCircle(id)
        }
    }

    DisposableEffect(id, onClick) {
        controller.registerCircleClick(id, onClick)
        onDispose {
            controller.unregisterCircleClick(id)
        }
    }

    LaunchedEffect(
        state.center,
        state.radiusMeters,
        state.isSelected,
        clickable,
        fillColor,
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
        controller.updateCircle(
            CircleInfo(
                id = id,
                center = state.center,
                clickable = clickable,
                fillColor = fillColor,
                radiusMeters = state.radiusMeters,
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
 * A declarative circle overlay that can be placed inside a [MapView] content block.
 *
 * This is a convenience overload that takes a [com.binayshaw7777.leaflekt.library.camera.LatLng] instead of a [CircleState].
 * Use this for static circles that don't need programmatic movement or selection control.
 *
 * @param center The center coordinate of the circle.
 * @param clickable Whether the circle is interactive.
 * @param fillColor The interior fill color.
 * @param radiusMeters The radius of the circle in meters.
 * @param strokeColor The outline color.
 * @param strokePattern Optional dash/gap/dot pattern.
 * @param strokeWidth The outline width in pixels.
 * @param visible Visibility toggle.
 * @param zIndex The drawing order.
 * @param fillOpacity Interior fill opacity (0.0 to 1.0).
 * @param strokeOpacity Outline opacity (0.0 to 1.0).
 * @param selected Whether the circle starts in a visually highlighted "selected" state.
 * @param selectedStrokeColor Highlight stroke color.
 * @param selectedStrokeWidth Highlight stroke width.
 * @param selectedFillOpacity Highlight fill opacity.
 * @param selectedZIndexBoost Highlight z-index boost.
 * @param id Unique identifier.
 * @param onClick Click handler. Return `true` to consume the event.
 */
@Composable
@MapComposable
fun Circle(
    center: LatLng,
    clickable: Boolean = false,
    fillColor: Color = Color.Transparent,
    radiusMeters: Double = 10.0,
    strokeColor: Color = Color.Black,
    strokePattern: List<LeaflektStrokePattern>? = null,
    strokeWidth: Float = 10f,
    visible: Boolean = true,
    zIndex: Float = 0f,
    fillOpacity: Float = 0.2f,
    strokeOpacity: Float = 1f,
    selected: Boolean = false,
    selectedStrokeColor: Color = DefaultLeaflektSelectedStrokeColor,
    selectedStrokeWidth: Float = strokeWidth + SelectedCircleStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { UUID.randomUUID().toString() },
    onClick: () -> Boolean = { false }
) {
    Circle(
        state = rememberCircleState(center = center, radiusMeters = radiusMeters).apply { isSelected = selected },
        clickable = clickable,
        fillColor = fillColor,
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

private const val SelectedCircleStrokeWidthBoost = 2f

