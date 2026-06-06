package com.binayshaw7777.leaflekt.compose

val DefaultLeaflektMapProperties: LeaflektMapProperties = LeaflektMapProperties()

class LeaflektMapProperties(
    val mapStyle: LeaflektMapStyle = LeaflektMapStyle.OpenStreetMap,
    val geoJsonOverlay: LeaflektGeoJsonOverlay = LeaflektGeoJsonOverlay.India
) {
    override fun toString(): String =
        "LeaflektMapProperties(mapStyle=$mapStyle, geoJsonOverlay=$geoJsonOverlay)"

    override fun equals(other: Any?): Boolean =
        other is LeaflektMapProperties &&
            mapStyle == other.mapStyle &&
            geoJsonOverlay == other.geoJsonOverlay

    override fun hashCode(): Int = 31 * mapStyle.hashCode() + geoJsonOverlay.hashCode()

    fun copy(
        mapStyle: LeaflektMapStyle = this.mapStyle,
        geoJsonOverlay: LeaflektGeoJsonOverlay = this.geoJsonOverlay
    ): LeaflektMapProperties = LeaflektMapProperties(mapStyle = mapStyle, geoJsonOverlay = geoJsonOverlay)
}
