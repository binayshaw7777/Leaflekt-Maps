package com.binayshaw7777.leaflekt

data class LeaflektLatLng(
    val latitude: Double,
    val longitude: Double
) {
    init {
        require(!latitude.isNaN() && !latitude.isInfinite()) { "latitude must be finite" }
        require(!longitude.isNaN() && !longitude.isInfinite()) { "longitude must be finite" }
        require(latitude in -90.0..90.0) { "latitude must be in [-90, 90], was $latitude" }
        require(longitude in -180.0..180.0) { "longitude must be in [-180, 180], was $longitude" }
    }
}
