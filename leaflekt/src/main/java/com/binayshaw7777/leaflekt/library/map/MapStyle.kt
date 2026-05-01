package com.binayshaw7777.leaflekt.library.map

/**
 * Defines the tile provider and visual style for the map.
 *
 * @param id Unique identifier for the style, used for overlay theming.
 * @param url The tile URL template.
 * @param attribution The data provider attribution string.
 * @param maxZoom The maximum zoom level supported by this tile provider.
 * @param subdomains Optional list of subdomains (e.g., "abc") for tile load balancing.
 */
enum class MapStyle(
    val id: String,
    val url: String,
    val attribution: String,
    val maxZoom: Int = 19,
    val subdomains: String? = null
) {
    OpenStreetMap(
        id = "open_street_map",
        url = "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
        attribution = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors"
    ),
    CartoLight(
        id = "carto_light",
        url = "https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}.png",
        attribution = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors &copy; <a href=\"https://carto.com/attributions\">CARTO</a>",
        maxZoom = 20,
        subdomains = "abcd"
    ),
    CartoDark(
        id = "carto_dark",
        url = "https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}.png",
        attribution = "&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors &copy; <a href=\"https://carto.com/attributions\">CARTO</a>",
        maxZoom = 20,
        subdomains = "abcd"
    ),
    OpenTopoMap(
        id = "open_topo_map",
        url = "https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png",
        attribution = "Map data: &copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors, SRTM | Map style: &copy; <a href=\"https://opentopomap.org\">OpenTopoMap</a>",
        maxZoom = 17,
        subdomains = "abc"
    ),
    EsriWorldImagery(
        id = "esri_world_imagery",
        url = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
        attribution = "Tiles &copy; Esri &mdash; Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community"
    )
}

