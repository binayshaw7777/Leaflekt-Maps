package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class LeaflektPolygonState(
    points: List<LeaflektLatLng> = emptyList(),
    holes: List<List<LeaflektLatLng>> = emptyList()
) {
    var points: List<LeaflektLatLng> by mutableStateOf(points)
    var holes: List<List<LeaflektLatLng>> by mutableStateOf(holes)
    var isSelected: Boolean by mutableStateOf(false)

    fun select() { isSelected = true }
    fun deselect() { isSelected = false }
    fun toggleSelection() { isSelected = !isSelected }
}

@Composable
fun rememberLeaflektPolygonState(
    points: List<LeaflektLatLng> = emptyList(),
    holes: List<List<LeaflektLatLng>> = emptyList()
): LeaflektPolygonState = remember {
    LeaflektPolygonState(points = points, holes = holes)
}
