package com.binayshaw7777.leaflekt.library.polyline

import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern

/**
 * Polyline configuration sent to the Leaflet runtime.
 *
 * `geodesic` is retained for source compatibility, but Leaflet renders the path as a normal
 * projected polyline.
 *
 * `zIndex` is applied as a best-effort draw order among vector layers.
 */
internal data class PolylineInfo(
    val id: String,
    val points: List<LatLng>,
    val clickable: Boolean = false,
    val color: Color = Color.Black,
    val geodesic: Boolean = false,
    val pattern: List<LeaflektStrokePattern>? = null,
    val visible: Boolean = true,
    val width: Float = 10f,
    val zIndex: Float = 0f,
    val alpha: Float = 1f
)

