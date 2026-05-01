package com.binayshaw7777.leaflekt.library.circle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng

/**
 * A state object that can be hoisted to control and observe a circle's state.
 *
 * @param center the initial center coordinate of the circle
 * @param radiusMeters the initial radius of the circle in meters
 */
class LeaflektCircleState(
    center: LeaflektLatLng = LeaflektLatLng(0.0, 0.0),
    radiusMeters: Double = 10.0
) {
    /**
     * The current center coordinate of the circle.
     */
    var center: LeaflektLatLng by mutableStateOf(center)

    /**
     * The current radius of the circle in meters.
     */
    var radiusMeters: Double by mutableDoubleStateOf(radiusMeters)

    /**
     * Whether the circle is currently in a "selected" state, which typically triggers
     * a visual highlight.
     */
    var isSelected: Boolean by mutableStateOf(false)

    /**
     * Sets [isSelected] to true.
     */
    fun select() {
        isSelected = true
    }

    /**
     * Sets [isSelected] to false.
     */
    fun deselect() {
        isSelected = false
    }

    /**
     * Toggles the [isSelected] state.
     */
    fun toggleSelection() {
        isSelected = !isSelected
    }

    companion object {
        /**
         * The default saver implementation for [LeaflektCircleState].
         */
        val Saver: Saver<LeaflektCircleState, *> = Saver(
            save = { state ->
                listOf(
                    state.center.latitude,
                    state.center.longitude,
                    state.radiusMeters,
                    if (state.isSelected) 1.0 else 0.0
                )
            },
            restore = { values ->
                val list = values.toList()
                LeaflektCircleState(
                    center = LeaflektLatLng(list[0], list[1]),
                    radiusMeters = list[2]
                ).apply {
                    this.isSelected = list[3] == 1.0
                }
            }
        )
    }
}

/**
 * Creates and [rememberSaveable]s a [LeaflektCircleState].
 *
 * @param center the initial center coordinate of the circle
 * @param radiusMeters the initial radius of the circle in meters
 */
@Composable
fun rememberLeaflektCircleState(
    center: LeaflektLatLng = LeaflektLatLng(0.0, 0.0),
    radiusMeters: Double = 10.0
): LeaflektCircleState = rememberSaveable(saver = LeaflektCircleState.Saver) {
    LeaflektCircleState(center = center, radiusMeters = radiusMeters)
}
