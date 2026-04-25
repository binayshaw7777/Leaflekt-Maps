package com.binayshaw7777.leaflekt.library

import kotlinx.serialization.Serializable

@Serializable
internal data class MapProjection(
    val lat: Double,
    val lng: Double,
    val x: Double,
    val y: Double
)
