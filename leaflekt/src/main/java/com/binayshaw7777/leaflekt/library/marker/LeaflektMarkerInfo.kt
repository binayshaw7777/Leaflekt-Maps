package com.binayshaw7777.leaflekt.library.marker

/**
 * Data model for a Leaflekt marker.
 *
 * This class contains the configuration for a marker to be rendered on the Leaflet map.
 *
 * @property id Unique identifier for the marker.
 * @property lat Latitude coordinate.
 * @property lng Longitude coordinate.
 * @property title Text shown in the marker's popup.
 * @property snippet Sub-text for the marker.
 * @property visible Whether the marker is currently visible.
 * @property alpha The opacity of the marker (0.0 to 1.0).
 */
internal data class LeaflektMarkerInfo(
    val id: String? = null,
    val lat: Double,
    val lng: Double,
    val title: String? = null,
    val snippet: String? = null,
    val visible: Boolean = true,
    val alpha: Float = 1.0f,
    val zIndex: Float = 0f,
    val icon: LeaflektMarkerIconInfo? = null,
    val rotationDegrees: Float = 0f
)

internal data class LeaflektMarkerIconInfo(
    val dataUrl: String,
    val widthPx: Int,
    val heightPx: Int,
    val anchorFractionX: Float,
    val anchorFractionY: Float
)
