package com.binayshaw7777.leaflekt

data class LeaflektCircleInfo(
    val id: String,
    val center: LeaflektLatLng,
    val clickable: Boolean = false,
    val fillColor: LeaflektColor = LeaflektColor.Transparent,
    val radiusMeters: Double = 10.0,
    val strokeColor: LeaflektColor = LeaflektColor.Black,
    val strokePattern: List<LeaflektStrokePattern>? = null,
    val strokeWidth: Float = 10f,
    val visible: Boolean = true,
    val zIndex: Float = 0f,
    val fillOpacity: Float = 0.2f,
    val strokeOpacity: Float = 1f
)
