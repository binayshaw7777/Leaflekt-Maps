package com.binayshaw7777.leaflekt.library.polyline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng

class LeaflektPolylineState(points: List<LeaflektLatLng> = emptyList()) {
    var points: List<LeaflektLatLng> by mutableStateOf(points)
    var isSelected: Boolean by mutableStateOf(false)

    fun select() {
        isSelected = true
    }

    fun deselect() {
        isSelected = false
    }

    fun toggleSelection() {
        isSelected = !isSelected
    }

    companion object {
        val Saver: Saver<LeaflektPolylineState, *> = Saver(
            save = { state ->
                state.points.flatMap { listOf(it.latitude, it.longitude) } + listOf(
                    if (state.isSelected) 1.0 else 0.0
                )
            },
            restore = { values ->
                val savedValues = values.toList()
                val isSelected = savedValues.lastOrNull() == 1.0
                val points = savedValues.dropLast(1).chunked(2).map {
                    LeaflektLatLng(it[0], it[1])
                }
                LeaflektPolylineState(points).apply {
                    this.isSelected = isSelected
                }
            }
        )
    }
}

@Composable
fun rememberLeaflektPolylineState(
    points: List<LeaflektLatLng> = emptyList()
): LeaflektPolylineState = rememberSaveable(saver = LeaflektPolylineState.Saver) {
    LeaflektPolylineState(points)
}
