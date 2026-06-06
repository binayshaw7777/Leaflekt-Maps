package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class LeaflektCircleState(
    center: LeaflektLatLng = LeaflektLatLng(0.0, 0.0),
    radiusMeters: Double = 10.0
) {
    var center: LeaflektLatLng by mutableStateOf(center)
    var radiusMeters: Double by mutableStateOf(radiusMeters)
    var isSelected: Boolean by mutableStateOf(false)

    fun select() { isSelected = true }
    fun deselect() { isSelected = false }
    fun toggleSelection() { isSelected = !isSelected }
}

@Composable
fun rememberLeaflektCircleState(
    center: LeaflektLatLng = LeaflektLatLng(0.0, 0.0),
    radiusMeters: Double = 10.0
): LeaflektCircleState = remember {
    LeaflektCircleState(center = center, radiusMeters = radiusMeters)
}
