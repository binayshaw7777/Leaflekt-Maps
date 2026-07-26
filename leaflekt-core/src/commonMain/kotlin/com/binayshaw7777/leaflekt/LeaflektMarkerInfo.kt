package com.binayshaw7777.leaflekt

data class LeaflektMarkerInfo(
    val id: String? = null,
    val lat: Double,
    val lng: Double,
    val title: String? = null,
    val snippet: String? = null,
    val visible: Boolean = true,
    val alpha: Float = 1.0f,
    val zIndex: Int = 0,
    val rotationDegrees: Float = 0f,
    val icon: LeaflektMarkerIconInfo? = null
) {
    init {
        require(id == null || id.isNotBlank()) { "marker id must not be blank" }
        require(alpha in 0f..1f) { "alpha must be in [0, 1], was $alpha" }
        require(!lat.isNaN() && !lat.isInfinite()) { "lat must be finite" }
        require(!lng.isNaN() && !lng.isInfinite()) { "lng must be finite" }
        require(lat in -90.0..90.0) { "lat must be in [-90, 90], was $lat" }
        require(lng in -180.0..180.0) { "lng must be in [-180, 180], was $lng" }
    }
}

data class LeaflektMarkerIconInfo(
    val dataUrl: String,
    val widthPx: Int,
    val heightPx: Int,
    val anchorFractionX: Float,
    val anchorFractionY: Float
) {
    init {
        require(dataUrl.isNotBlank()) { "dataUrl must not be blank" }
        require(widthPx > 0) { "widthPx must be > 0, was $widthPx" }
        require(heightPx > 0) { "heightPx must be > 0, was $heightPx" }
        require(anchorFractionX in 0f..1f) { "anchorFractionX must be in [0, 1], was $anchorFractionX" }
        require(anchorFractionY in 0f..1f) { "anchorFractionY must be in [0, 1], was $anchorFractionY" }
    }
}
