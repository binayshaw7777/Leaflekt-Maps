package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.math.max

private const val SelectedLeaflektStrokeWidthBoost = 4f

@Composable
@LeaflektMapComposable
fun LeaflektPolyline(
    state: LeaflektPolylineState = rememberLeaflektPolylineState(),
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
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return
    if (state.points.isEmpty()) return

    val resolvedColor = if (state.isSelected) selectedColor else color
    val resolvedWidth = if (state.isSelected) max(selectedWidth, width) else width
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolyline(LeaflektPolylineInfo(id = id, points = state.points, clickable = clickable, color = resolvedColor.toLeaflektColor(), geodesic = geodesic, pattern = pattern, visible = visible, width = resolvedWidth, zIndex = resolvedZIndex, alpha = alpha))
        controller.registerPolylineClick(id, onClick)
        onDispose { controller.unregisterPolylineClick(id); controller.removePolyline(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolylineClick(id, onClick)
        onDispose { controller.unregisterPolylineClick(id) }
    }

    LaunchedEffect(state.points, state.isSelected, clickable, color, geodesic, pattern, visible, width, zIndex, alpha, selectedColor, selectedWidth, selectedZIndexBoost) {
        controller.updatePolyline(LeaflektPolylineInfo(id = id, points = state.points, clickable = clickable, color = resolvedColor.toLeaflektColor(), geodesic = geodesic, pattern = pattern, visible = visible, width = resolvedWidth, zIndex = resolvedZIndex, alpha = alpha))
    }
}

@Composable
@LeaflektMapComposable
fun LeaflektPolyline(
    points: List<LeaflektLatLng>,
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
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return
    if (points.isEmpty()) return

    val resolvedColor = if (selected) selectedColor else color
    val resolvedWidth = if (selected) max(selectedWidth, width) else width
    val resolvedZIndex = if (selected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolyline(LeaflektPolylineInfo(id = id, points = points, clickable = clickable, color = resolvedColor.toLeaflektColor(), geodesic = geodesic, pattern = pattern, visible = visible, width = resolvedWidth, zIndex = resolvedZIndex, alpha = alpha))
        controller.registerPolylineClick(id, onClick)
        onDispose { controller.unregisterPolylineClick(id); controller.removePolyline(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolylineClick(id, onClick)
        onDispose { controller.unregisterPolylineClick(id) }
    }

    LaunchedEffect(points, selected, clickable, color, geodesic, pattern, visible, width, zIndex, alpha, selectedColor, selectedWidth, selectedZIndexBoost) {
        controller.updatePolyline(LeaflektPolylineInfo(id = id, points = points, clickable = clickable, color = resolvedColor.toLeaflektColor(), geodesic = geodesic, pattern = pattern, visible = visible, width = resolvedWidth, zIndex = resolvedZIndex, alpha = alpha))
    }
}
