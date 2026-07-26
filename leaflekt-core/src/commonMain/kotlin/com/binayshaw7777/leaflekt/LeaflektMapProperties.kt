package com.binayshaw7777.leaflekt

val DefaultLeaflektMapProperties: LeaflektMapProperties = LeaflektMapProperties()

data class LeaflektClusterConfig(
    val groupId: String,
    val maxClusterRadius: Int = 80
)

class LeaflektMapProperties(
    val mapStyle: LeaflektMapStyle = LeaflektMapStyle.OpenStreetMap,
    val geoJsonOverlay: LeaflektGeoJsonOverlay = LeaflektGeoJsonOverlay.India,
    val tileBufferSize: Int = defaultTileBufferSize(),
    val clusterConfig: LeaflektClusterConfig? = null
) {
    override fun toString(): String =
        "LeaflektMapProperties(mapStyle=$mapStyle, geoJsonOverlay=$geoJsonOverlay, tileBufferSize=$tileBufferSize, clusterConfig=$clusterConfig)"

    override fun equals(other: Any?): Boolean =
        other is LeaflektMapProperties &&
            mapStyle == other.mapStyle &&
            geoJsonOverlay == other.geoJsonOverlay &&
            tileBufferSize == other.tileBufferSize &&
            clusterConfig == other.clusterConfig

    override fun hashCode(): Int {
        var result = 31 * (31 * mapStyle.hashCode() + geoJsonOverlay.hashCode()) + tileBufferSize
        result = 31 * result + (clusterConfig?.hashCode() ?: 0)
        return result
    }

    fun copy(
        mapStyle: LeaflektMapStyle = this.mapStyle,
        geoJsonOverlay: LeaflektGeoJsonOverlay = this.geoJsonOverlay,
        tileBufferSize: Int = this.tileBufferSize,
        clusterConfig: LeaflektClusterConfig? = this.clusterConfig
    ): LeaflektMapProperties = LeaflektMapProperties(
        mapStyle = mapStyle,
        geoJsonOverlay = geoJsonOverlay,
        tileBufferSize = tileBufferSize,
        clusterConfig = clusterConfig
    )
}
