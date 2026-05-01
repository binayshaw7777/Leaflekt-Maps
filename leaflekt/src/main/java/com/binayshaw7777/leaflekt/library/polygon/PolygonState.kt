package com.binayshaw7777.leaflekt.library.polygon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.binayshaw7777.leaflekt.library.camera.LatLng

/**
 * A state object that can be hoisted to control and observe a polygon's state.
 *
 * @param points the initial list of points defining the polygon's outer boundary
 * @param holes the initial list of holes within the polygon
 */
class PolygonState(
    points: List<LatLng> = emptyList(),
    holes: List<List<LatLng>> = emptyList()
) {
    /**
     * The list of coordinates defining the outer boundary of the polygon.
     */
    var points: List<LatLng> by mutableStateOf(points)

    /**
     * The list of coordinate lists, each defining a hole within the polygon.
     */
    var holes: List<List<LatLng>> by mutableStateOf(holes)

    /**
     * Whether the polygon is currently in a "selected" state, which typically triggers
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
         * The default saver implementation for [PolygonState].
         */
        val Saver: Saver<PolygonState, *> = Saver(
            save = { state ->
                val pointsData = state.points.flatMap { listOf(it.latitude, it.longitude) }
                val holesData = state.holes.flatMap { hole ->
                    listOf(hole.size.toDouble()) + hole.flatMap { listOf(it.latitude, it.longitude) }
                }
                listOf(pointsData.size.toDouble()) + pointsData +
                        listOf(state.holes.size.toDouble()) + holesData +
                        listOf(if (state.isSelected) 1.0 else 0.0)
            },
            restore = { values ->
                val list = values.toList()
                var index = 0

                val pointsSize = list[index++].toInt()
                val points = (0 until pointsSize step 2).map {
                    LatLng(list[index++], list[index++])
                }

                val holesCount = list[index++].toInt()
                val holes = (0 until holesCount).map {
                    val holeSize = list[index++].toInt()
                    (0 until holeSize step 2).map {
                        LatLng(list[index++], list[index++])
                    }
                }

                val isSelected = list[index] == 1.0
                PolygonState(points, holes).apply {
                    this.isSelected = isSelected
                }
            }
        )
    }
}

/**
 * Creates and [rememberSaveable]s a [PolygonState].
 *
 * @param points the initial list of points defining the polygon's outer boundary
 * @param holes the initial list of holes within the polygon
 */
@Composable
fun rememberPolygonState(
    points: List<LatLng> = emptyList(),
    holes: List<List<LatLng>> = emptyList()
): PolygonState = rememberSaveable(saver = PolygonState.Saver) {
    PolygonState(points = points, holes = holes)
}

