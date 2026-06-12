package com.binayshaw7777.leaflekt

val DefaultLeaflektMapProperties: LeaflektMapProperties = LeaflektMapProperties()

class LeaflektMapProperties(
    val mapStyle: LeaflektMapStyle = LeaflektMapStyle.OpenStreetMap,
    val geoJsonOverlay: LeaflektGeoJsonOverlay = LeaflektGeoJsonOverlay.India,
    val tileBufferSize: Int = 10
) {
    override fun toString(): String =
        "LeaflektMapProperties(mapStyle=$mapStyle, geoJsonOverlay=$geoJsonOverlay, tileBufferSize=$tileBufferSize)"

    override fun equals(other: Any?): Boolean =
        other is LeaflektMapProperties &&
            mapStyle == other.mapStyle &&
            geoJsonOverlay == other.geoJsonOverlay &&
            tileBufferSize == other.tileBufferSize

    override fun hashCode(): Int = 31 * (31 * mapStyle.hashCode() + geoJsonOverlay.hashCode()) + tileBufferSize

    fun copy(
        mapStyle: LeaflektMapStyle = this.mapStyle,
        geoJsonOverlay: LeaflektGeoJsonOverlay = this.geoJsonOverlay,
        tileBufferSize: Int = this.tileBufferSize
    ): LeaflektMapProperties = LeaflektMapProperties(mapStyle = mapStyle, geoJsonOverlay = geoJsonOverlay, tileBufferSize = tileBufferSize)
}
