package com.binayshaw7777.leaflekt.internal.projection

import kotlinx.serialization.Serializable

@Serializable
internal data class MapProjection(
    val lat: Double,
    val lng: Double,
    val xFraction: Double,
    val yFraction: Double
)
