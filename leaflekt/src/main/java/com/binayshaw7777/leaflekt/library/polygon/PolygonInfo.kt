package com.binayshaw7777.leaflekt.library.polygon

import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern

/**
 * Polygon configuration sent to the Leaflet runtime.
 *
 * `geodesic` is retained for source compatibility, but Leaflet renders the polygon edges as
 * normal projected segments.
 *
 * `zIndex` is applied as a best-effort draw order among vector layers.
 */
data class PolygonInfo(
    val id: String,
    val points: List<LatLng>,
    val clickable: Boolean = false,
    val fillColor: Color = Color.Transparent,
    val geodesic: Boolean = false,
    val holes: List<List<LatLng>> = emptyList(),
    val strokeColor: Color = Color.Black,
    val strokePattern: List<LeaflektStrokePattern>? = null,
    val strokeWidth: Float = 10f,
    val visible: Boolean = true,
    val zIndex: Float = 0f,
    val fillOpacity: Float = 0.2f,
    val strokeOpacity: Float = 1f
)

