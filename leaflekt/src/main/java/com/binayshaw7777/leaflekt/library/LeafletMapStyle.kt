package com.binayshaw7777.leaflekt.library

/**
 * Built-in tile styles exposed by the SDK.
 *
 * Each style keeps the tile template and attribution required by its provider.
 */
enum class LeafletMapStyle(
    internal val id: String,
    internal val tileUrlTemplate: String,
    internal val attributionHtml: String,
    internal val maxZoom: Int,
    internal val subdomains: String? = null,
    internal val isVectorStyle: Boolean = false
) {
    OpenStreetMap(
        id = "open_street_map",
        tileUrlTemplate = "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
        attributionHtml = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors",
        maxZoom = 19
    ),
    CartoLight(
        id = "carto_light",
        tileUrlTemplate = "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png",
        attributionHtml = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors &copy; <a href=\"https://carto.com/attributions\">CARTO</a>",
        maxZoom = 20,
        subdomains = "abcd"
    ),
    CartoDark(
        id = "carto_dark",
        tileUrlTemplate = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png",
        attributionHtml = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors &copy; <a href=\"https://carto.com/attributions\">CARTO</a>",
        maxZoom = 20,
        subdomains = "abcd"
    ),
    OpenTopoMap(
        id = "open_topo_map",
        tileUrlTemplate = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        attributionHtml = "Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, <a href=\"https://viewfinderpanoramas.org\">SRTM</a> | Map style: &copy; <a href=\"https://opentopomap.org\">OpenTopoMap</a>",
        maxZoom = 17,
        subdomains = "abc"
    ),
    EsriWorldImagery(
        id = "esri_world_imagery",
        tileUrlTemplate = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        attributionHtml = "Tiles &copy; Esri, Maxar, Earthstar Geographics, and the GIS User Community",
        maxZoom = 19
    ),
    OpenFreeMapLiberty(
        id = "openfreemap_liberty",
        tileUrlTemplate = "https://tiles.openfreemap.org/styles/liberty",
        attributionHtml = "&copy; <a href=\"https://openfreemap.org\">OpenFreeMap</a> &copy; <a href=\"https://www.openmaptiles.org/\">OpenMapTiles</a> Data from <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a>",
        maxZoom = 19,
        isVectorStyle = true
    ),
    OpenFreeMapFiord(
        id = "openfreemap_fiord",
        tileUrlTemplate = "https://tiles.openfreemap.org/styles/fiord",
        attributionHtml = "&copy; <a href=\"https://openfreemap.org\">OpenFreeMap</a> &copy; <a href=\"https://www.openmaptiles.org/\">OpenMapTiles</a> Data from <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a>",
        maxZoom = 19,
        isVectorStyle = true
    ),
    OpenFreeMap3D(
        id = "openfreemap_3d",
        tileUrlTemplate = "https://tiles.openfreemap.org/styles/3d",
        attributionHtml = "&copy; <a href=\"https://openfreemap.org\">OpenFreeMap</a> &copy; <a href=\"https://www.openmaptiles.org/\">OpenMapTiles</a> Data from <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a>",
        maxZoom = 19,
        isVectorStyle = true
    );
}
