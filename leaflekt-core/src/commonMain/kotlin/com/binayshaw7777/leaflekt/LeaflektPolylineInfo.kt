package com.binayshaw7777.leaflekt

data class LeaflektPolylineInfo(
    val id: String,
    val points: List<LeaflektLatLng>,
    val clickable: Boolean = false,
    val color: LeaflektColor = LeaflektColor.Black,
    val geodesic: Boolean = false,
    val pattern: List<LeaflektStrokePattern>? = null,
    val visible: Boolean = true,
    val width: Float = 10f,
    val zIndex: Float = 0f,
    val alpha: Float = 1f
) {
    init {
        require(points.isNotEmpty()) { "polyline must have at least one point" }
        require(alpha in 0f..1f) { "alpha must be in [0, 1], was $alpha" }
    }
}
