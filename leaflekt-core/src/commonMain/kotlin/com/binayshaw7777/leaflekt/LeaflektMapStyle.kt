package com.binayshaw7777.leaflekt

enum class LeaflektMapStyle(
    val id: String,
    val url: String,
    val attribution: String,
    val maxZoom: Int = 19,
    val subdomains: String? = null,
    val isVectorStyle: Boolean = false
) {
    OpenStreetMap(
        id = "open_street_map",
        url = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"
    ),
    CartoLight(
        id = "carto_light",
        url = "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png",
        attribution = "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors &copy; <a href='https://carto.com/attributions'>CARTO</a>"
    ),
    CartoDark(
        id = "carto_dark",
        url = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png",
        attribution = "&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors &copy; <a href='https://carto.com/attributions'>CARTO</a>"
    ),
    OpenTopoMap(
        id = "open_topo_map",
        url = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        attribution = "Map data: &copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors, <a href='http://viewfinderpanoramas.org'>SRTM</a> | Map style: &copy; <a href='https://opentopomap.org'>OpenTopoMap</a> (<a href='https://creativecommons.org/licenses/by-sa/3.0/'>CC-BY-SA</a>)",
        maxZoom = 17
    ),
    EsriWorldImagery(
        id = "esri_world_imagery",
        url = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        attribution = "Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community"
    ),
    OpenFreeMapLiberty(
        id = "openfreemap_liberty",
        url = "https://tiles.openfreemap.org/styles/liberty",
        attribution = "&copy; <a href='https://openfreemap.org'>OpenFreeMap</a> &copy; <a href='https://www.openmaptiles.org/'>OpenMapTiles</a> Data from <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a>",
        isVectorStyle = true
    ),
    OpenFreeMapFiord(
        id = "openfreemap_fiord",
        url = "https://tiles.openfreemap.org/styles/fiord",
        attribution = "&copy; <a href='https://openfreemap.org'>OpenFreeMap</a> &copy; <a href='https://www.openmaptiles.org/'>OpenMapTiles</a> Data from <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a>",
        isVectorStyle = true
    ),
    OpenFreeMap3D(
        id = "openfreemap_3d",
        url = "https://tiles.openfreemap.org/styles/3d",
        attribution = "&copy; <a href='https://openfreemap.org'>OpenFreeMap</a> &copy; <a href='https://www.openmaptiles.org/'>OpenMapTiles</a> Data from <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a>",
        isVectorStyle = true
    )
}
