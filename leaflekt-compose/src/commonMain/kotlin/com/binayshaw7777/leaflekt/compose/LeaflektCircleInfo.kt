package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color

data class LeaflektCircleInfo(
    val id: String,
    val center: LeaflektLatLng,
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
