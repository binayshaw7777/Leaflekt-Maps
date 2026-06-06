package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class LeaflektPolylineState(points: List<LeaflektLatLng> = emptyList()) {
    var points: List<LeaflektLatLng> by mutableStateOf(points)
    var isSelected: Boolean by mutableStateOf(false)

    fun select() { isSelected = true }
    fun deselect() { isSelected = false }
    fun toggleSelection() { isSelected = !isSelected }

    companion object {
        val Saver: Saver<LeaflektPolylineState, *> = Saver(
            save = { state ->
                state.points.flatMap { listOf(it.latitude, it.longitude) } +
                    listOf(if (state.isSelected) 1.0 else 0.0)
            },
            restore = { values ->
                val isSelected = values.lastOrNull() == 1.0
                val points = values.dropLast(1).chunked(2).map { LeaflektLatLng(it[0], it[1]) }
                LeaflektPolylineState(points).apply { this.isSelected = isSelected }
            }
        )
    }
}

@Composable
fun rememberLeaflektPolylineState(
    key: String? = null,
    points: List<LeaflektLatLng> = emptyList()
): LeaflektPolylineState = rememberSaveable(key = key, saver = LeaflektPolylineState.Saver) {
    LeaflektPolylineState(points)
}
