package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color

data class LeaflektPolylineInfo(
    val id: String,
    val points: List<LeaflektLatLng>,
    val clickable: Boolean = false,
    val color: Color = Color.Black,
    val geodesic: Boolean = false,
    val pattern: List<LeaflektStrokePattern>? = null,
    val visible: Boolean = true,
    val width: Float = 10f,
    val zIndex: Float = 0f,
    val alpha: Float = 1f
)
