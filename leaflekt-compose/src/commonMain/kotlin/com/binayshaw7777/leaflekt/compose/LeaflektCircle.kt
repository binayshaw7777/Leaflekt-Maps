package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import kotlin.math.max

private const val SelectedLeaflektCircleStrokeWidthBoost = 2f

@Composable
@LeaflektMapComposable
fun LeaflektCircle(
    state: LeaflektCircleState = rememberLeaflektCircleState(),
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
    selectedStrokeWidth: Float = strokeWidth + SelectedLeaflektCircleStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return

    val resolvedStroke = if (state.isSelected) selectedStrokeColor else strokeColor
    val resolvedStrokeW = if (state.isSelected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOp = if (state.isSelected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (state.isSelected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addCircle(LeaflektCircleInfo(id = id, center = state.center, clickable = clickable, fillColor = fillColor.toLeaflektColor(), radiusMeters = state.radiusMeters, strokeColor = resolvedStroke.toLeaflektColor(), strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
        controller.registerCircleClick(id, onClick)
        onDispose { controller.unregisterCircleClick(id); controller.removeCircle(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerCircleClick(id, onClick)
        onDispose { controller.unregisterCircleClick(id) }
    }

    LaunchedEffect(state.center, state.radiusMeters, state.isSelected, clickable, fillColor, strokeColor, strokePattern, strokeWidth, visible, zIndex, fillOpacity, strokeOpacity, selectedStrokeColor, selectedStrokeWidth, selectedFillOpacity, selectedZIndexBoost) {
        controller.updateCircle(LeaflektCircleInfo(id = id, center = state.center, clickable = clickable, fillColor = fillColor.toLeaflektColor(), radiusMeters = state.radiusMeters, strokeColor = resolvedStroke.toLeaflektColor(), strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
    }
}

@Composable
@LeaflektMapComposable
fun LeaflektCircle(
    center: LeaflektLatLng,
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
    selectedStrokeWidth: Float = strokeWidth + SelectedLeaflektCircleStrokeWidthBoost,
    selectedFillOpacity: Float = max(fillOpacity, SelectedLeaflektMinimumFillOpacity),
    selectedZIndexBoost: Float = SelectedLeaflektZIndexBoost,
    id: String = remember { generateId() },
    onClick: () -> Unit = {}
) {
    val controller = LocalLeaflektController.current ?: return

    val resolvedStroke = if (selected) selectedStrokeColor else strokeColor
    val resolvedStrokeW = if (selected) max(selectedStrokeWidth, strokeWidth) else strokeWidth
    val resolvedFillOp = if (selected) max(selectedFillOpacity, fillOpacity) else fillOpacity
    val resolvedZIndex = if (selected) zIndex + selectedZIndexBoost else zIndex

    DisposableEffect(id) {
        controller.addCircle(LeaflektCircleInfo(id = id, center = center, clickable = clickable, fillColor = fillColor.toLeaflektColor(), radiusMeters = radiusMeters, strokeColor = resolvedStroke.toLeaflektColor(), strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
        controller.registerCircleClick(id, onClick)
        onDispose { controller.unregisterCircleClick(id); controller.removeCircle(id) }
    }

    DisposableEffect(id, onClick) {
        controller.registerCircleClick(id, onClick)
        onDispose { controller.unregisterCircleClick(id) }
    }

    LaunchedEffect(center, radiusMeters, selected, clickable, fillColor, strokeColor, strokePattern, strokeWidth, visible, zIndex, fillOpacity, strokeOpacity, selectedStrokeColor, selectedStrokeWidth, selectedFillOpacity, selectedZIndexBoost) {
        controller.updateCircle(LeaflektCircleInfo(id = id, center = center, clickable = clickable, fillColor = fillColor.toLeaflektColor(), radiusMeters = radiusMeters, strokeColor = resolvedStroke.toLeaflektColor(), strokePattern = strokePattern, strokeWidth = resolvedStrokeW, visible = visible, zIndex = resolvedZIndex, fillOpacity = resolvedFillOp, strokeOpacity = strokeOpacity))
    }
}
