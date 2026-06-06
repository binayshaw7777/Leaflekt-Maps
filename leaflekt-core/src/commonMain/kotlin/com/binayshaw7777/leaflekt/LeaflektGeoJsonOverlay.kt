package com.binayshaw7777.leaflekt

sealed class LeaflektGeoJsonOverlay {
    data object India : LeaflektGeoJsonOverlay()
    data class Custom(val geojson: String) : LeaflektGeoJsonOverlay()
    data object None : LeaflektGeoJsonOverlay()
}
