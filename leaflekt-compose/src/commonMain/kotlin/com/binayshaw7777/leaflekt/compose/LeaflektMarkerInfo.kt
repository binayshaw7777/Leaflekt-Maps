package com.binayshaw7777.leaflekt.compose

data class LeaflektMarkerInfo(
    val id: String? = null,
    val lat: Double,
    val lng: Double,
    val title: String? = null,
    val snippet: String? = null,
    val visible: Boolean = true,
    val alpha: Float = 1.0f,
    val icon: LeaflektMarkerIconInfo? = null
)

data class LeaflektMarkerIconInfo(
    val dataUrl: String,
    val widthPx: Int,
    val heightPx: Int,
    val anchorFractionX: Float,
    val anchorFractionY: Float
)
