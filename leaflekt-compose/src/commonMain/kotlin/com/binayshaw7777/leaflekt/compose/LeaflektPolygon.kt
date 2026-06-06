package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.math.max

private const val SelectedLeaflektPolygonStrokeWidthBoost = 2f

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
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return
    if (state.points.size < 3) return

    val resolvedStroke = if (state.isSelected) selectedStrokeColor else strokeColor
    val resolvedStrokeW = if (state.isSelected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOp = if (state.isSelected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolygon(LeaflektPolygonInfo(id = id, points = state.points, clickable = clickable, fillColor = fillColor, geodesic = geodesic, holes = state.holes, strokeColor = resolvedStroke, strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
        controller.registerPolygonClick(id, onClick)
        onDispose { controller.unregisterPolygonClick(id); controller.removePolygon(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolygonClick(id, onClick)
        onDispose { controller.unregisterPolygonClick(id) }
    }

    LaunchedEffect(state.points, state.holes, state.isSelected, clickable, fillColor, geodesic, strokeColor, strokePattern, strokeWidth, visible, zIndex, fillOpacity, strokeOpacity, selectedStrokeColor, selectedStrokeWidth, selectedFillOpacity, selectedZIndexBoost) {
        controller.updatePolygon(LeaflektPolygonInfo(id = id, points = state.points, clickable = clickable, fillColor = fillColor, geodesic = geodesic, holes = state.holes, strokeColor = resolvedStroke, strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
    }
}

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
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return
    if (points.size < 3) return

    val resolvedStroke = if (selected) selectedStrokeColor else strokeColor
    val resolvedStrokeW = if (selected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOp = if (selected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (selected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addPolygon(LeaflektPolygonInfo(id = id, points = points, clickable = clickable, fillColor = fillColor, geodesic = geodesic, holes = holes, strokeColor = resolvedStroke, strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
        controller.registerPolygonClick(id, onClick)
        onDispose { controller.unregisterPolygonClick(id); controller.removePolygon(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerPolygonClick(id, onClick)
        onDispose { controller.unregisterPolygonClick(id) }
    }

    LaunchedEffect(points, holes, selected, clickable, fillColor, geodesic, strokeColor, strokePattern, strokeWidth, visible, zIndex, fillOpacity, strokeOpacity, selectedStrokeColor, selectedStrokeWidth, selectedFillOpacity, selectedZIndexBoost) {
        controller.updatePolygon(LeaflektPolygonInfo(id = id, points = points, clickable = clickable, fillColor = fillColor, geodesic = geodesic, holes = holes, strokeColor = resolvedStroke, strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
    }
}
