package com.binayshaw7777.leaflekt.library.circle

import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern

/**
 * Circle configuration sent to the Leaflet runtime.
 *
 * `zIndex` is applied as a best-effort draw order among vector layers.
 */
internal data class CircleInfo(
    val id: String,
    val center: LatLng,
    val clickable: Boolean = false,
    val fillColor: Color = Color.Transparent,
    val radiusMeters: Double = 10.0,
    val strokeColor: Color = Color.Black,
    val strokePattern: List<LeaflektStrokePattern>? = null,
    val strokeWidth: Float = 10f,
    val visible: Boolean = true,
    val zIndex: Float = 0f,
    val fillOpacity: Float = 0.2f,
    val strokeOpacity: Float = 1f
)

