package com.binayshaw7777.leaflekt.internal.script

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.binayshaw7777.leaflekt.internal.serialization.MapViewJson
import com.binayshaw7777.leaflekt.library.circle.CircleInfo
import com.binayshaw7777.leaflekt.library.cluster.MarkerClusterOptions
import com.binayshaw7777.leaflekt.library.map.MapStyle
import com.binayshaw7777.leaflekt.library.marker.MarkerInfo
import com.binayshaw7777.leaflekt.library.polygon.PolygonInfo
import com.binayshaw7777.leaflekt.library.polyline.PolylineInfo
import com.binayshaw7777.leaflekt.library.shape.LeaflektStrokePattern

/**
 * Internal script builder that generates JavaScript commands for the Leaflet runtime.
 */
internal object LeaflektScriptBuilder {
    fun initMapScript(lat: Double, lng: Double, zoom: Double, bearing: Double, minZoom: Double, maxZoom: Double, restrictToWorldBounds: Boolean): String {
        return "window.LeaflektBridge.initMap($lat,$lng,$zoom,$bearing,$minZoom,$maxZoom,$restrictToWorldBounds);"
    }

    fun setZoomControlsEnabledScript(isEnabled: Boolean): String {
        return "window.LeaflektBridge.setZoomControlsEnabled($isEnabled);"
    }

    fun setScrollGesturesEnabledScript(isEnabled: Boolean): String {
        return "window.LeaflektBridge.setScrollGesturesEnabled($isEnabled);"
    }

    fun setZoomGesturesEnabledScript(isEnabled: Boolean): String {
        return "window.LeaflektBridge.setZoomGesturesEnabled($isEnabled);"
    }

    fun setRotateGesturesEnabledScript(isEnabled: Boolean): String {
        return "window.LeaflektBridge.setRotateGesturesEnabled($isEnabled);"
    }

    fun setMapStyleScript(style: MapStyle): String {
        return "window.LeaflektBridge.setMapStyle(${style.toJson()});"
    }

    fun moveCameraScript(lat: Double, lng: Double, zoom: Double, bearing: Double): String {
        return "window.LeaflektBridge.moveCamera($lat,$lng,$zoom,$bearing);"
    }

    fun setBearingScript(bearing: Double): String {
        return "window.LeaflektBridge.setBearing($bearing);"
    }

    fun addMarkersScript(markers: List<MarkerInfo>, clusterId: String? = null): String {
        val payload = markers.joinToString(prefix = "[", postfix = "]") { it.toJson() }
        val clusterArg = clusterId?.let { MapViewJson.encodeString(it) } ?: "null"
        return "window.LeaflektBridge.addMarkers($payload, $clusterArg);"
    }

    fun updateMarkerScript(marker: MarkerInfo): String {
        return "window.LeaflektBridge.updateMarker(${marker.toJson()});"
    }

    fun removeMarkerScript(markerId: String): String {
        return "window.LeaflektBridge.removeMarker('$markerId');"
    }

    fun clearMarkersScript(): String {
        return "window.LeaflektBridge.clearMarkers();"
    }

    fun createMarkerClusterGroupScript(clusterId: String, options: MarkerClusterOptions): String {
        return "window.LeaflektBridge.createMarkerClusterGroup(${MapViewJson.encodeString(clusterId)}, ${MapViewJson.encodeMarkerClusterOptions(options)});"
    }

    fun removeMarkerClusterGroupScript(clusterId: String): String {
        return "window.LeaflektBridge.removeMarkerClusterGroup(${MapViewJson.encodeString(clusterId)});"
    }

    fun addPolylineScript(polyline: PolylineInfo): String {
        return "window.LeaflektBridge.addPolyline(${polyline.toJson()});"
    }

    fun updatePolylineScript(polyline: PolylineInfo): String {
        return "window.LeaflektBridge.updatePolyline(${polyline.toJson()});"
    }

    fun removePolylineScript(polylineId: String): String {
        return "window.LeaflektBridge.removePolyline(${MapViewJson.encodeString(polylineId)});"
    }

    fun addPolygonScript(polygon: PolygonInfo): String {
        return "window.LeaflektBridge.addPolygon(${polygon.toJson()});"
    }

    fun updatePolygonScript(polygon: PolygonInfo): String {
        return "window.LeaflektBridge.updatePolygon(${polygon.toJson()});"
    }

    fun removePolygonScript(polygonId: String): String {
        return "window.LeaflektBridge.removePolygon(${MapViewJson.encodeString(polygonId)});"
    }

    fun addCircleScript(circle: CircleInfo): String {
        return "window.LeaflektBridge.addCircle(${circle.toJson()});"
    }

    fun updateCircleScript(circle: CircleInfo): String {
        return "window.LeaflektBridge.updateCircle(${circle.toJson()});"
    }

    fun removeCircleScript(circleId: String): String {
        return "window.LeaflektBridge.removeCircle(${MapViewJson.encodeString(circleId)});"
    }

    private fun MarkerInfo.toJson(): String {
        val iconJson = icon?.let {
            """
                {
                    "dataUrl": ${MapViewJson.encodeString(it.dataUrl)},
                    "widthPx": ${it.widthPx},
                    "heightPx": ${it.heightPx},
                    "anchorFractionX": ${it.anchorFractionX},
                    "anchorFractionY": ${it.anchorFractionY}
                }
            """.trimIndent()
        } ?: "null"

        return """
            {
                "id": ${MapViewJson.encodeString(id ?: "")},
                "lat": $lat,
                "lng": $lng,
                "title": ${MapViewJson.encodeNullableString(title)},
                "snippet": ${MapViewJson.encodeNullableString(snippet)},
                "visible": $visible,
                "alpha": $alpha,
                "zIndex": $zIndex,
                "icon": $iconJson,
                "rotationDegrees": $rotationDegrees
            }
        """.trimIndent()
    }

    private fun MapStyle.toJson(): String {
        val subdomainsJson = subdomains?.let(MapViewJson::encodeString) ?: "null"
        return """
            {
                "id": ${MapViewJson.encodeString(id)},
                "tileUrlTemplate": ${MapViewJson.encodeString(url)},
                "attributionHtml": ${MapViewJson.encodeString(attribution)},
                "maxZoom": $maxZoom,
                "subdomains": $subdomainsJson
            }
        """.trimIndent()
    }

    private fun PolylineInfo.toJson(): String {
        return """
            {
                "id": ${MapViewJson.encodeString(id)},
                "points": ${MapViewJson.encodeLatLngList(points)},
                "clickable": $clickable,
                "color": ${color.toLeaflektCssColor(alpha)},
                "geodesic": $geodesic,
                "pattern": ${pattern.toJson()},
                "visible": $visible,
                "width": $width,
                "zIndex": $zIndex
            }
        """.trimIndent()
    }

    private fun PolygonInfo.toJson(): String {
        return """
            {
                "id": ${MapViewJson.encodeString(id)},
                "points": ${MapViewJson.encodeLatLngList(points)},
                "clickable": $clickable,
                "fillColor": ${fillColor.toLeaflektCssColor(fillOpacity)},
                "geodesic": $geodesic,
                "holes": ${MapViewJson.encodeLatLngHoles(holes)},
                "strokeColor": ${strokeColor.toLeaflektCssColor(strokeOpacity)},
                "strokePattern": ${strokePattern.toJson()},
                "strokeWidth": $strokeWidth,
                "visible": $visible,
                "zIndex": $zIndex
            }
        """.trimIndent()
    }

    private fun CircleInfo.toJson(): String {
        return """
            {
                "id": ${MapViewJson.encodeString(id)},
                "center": ${MapViewJson.encodeLatLng(center)},
                "clickable": $clickable,
                "fillColor": ${fillColor.toLeaflektCssColor(fillOpacity)},
                "radiusMeters": $radiusMeters,
                "strokeColor": ${strokeColor.toLeaflektCssColor(strokeOpacity)},
                "strokePattern": ${strokePattern.toJson()},
                "strokeWidth": $strokeWidth,
                "visible": $visible,
                "zIndex": $zIndex
            }
        """.trimIndent()
    }

    private fun List<LeaflektStrokePattern>?.toJson(): String {
        if (this == null) {
            return "null"
        }

        return joinToString(prefix = "[", postfix = "]") { pattern ->
            when (pattern) {
                is LeaflektStrokePattern.Dash -> """{"type":"dash","length":${pattern.length}}"""
                is LeaflektStrokePattern.Gap -> """{"type":"gap","length":${pattern.length}}"""
                is LeaflektStrokePattern.Dot -> """{"type":"dot","radius":${pattern.radius}}"""
            }
        }
    }

    private fun Color.toLeaflektCssColor(alpha: Float): String {
        val argb = copy(alpha = alpha.coerceIn(0f, 1f)).toArgb()
        val red = argb shr 16 and 0xFF
        val green = argb shr 8 and 0xFF
        val blue = argb and 0xFF
        val resolvedAlpha = ((argb ushr 24) and 0xFF) / 255f
        return MapViewJson.encodeString(
            "rgba($red,$green,$blue,${"%.3f".format(java.util.Locale.US, resolvedAlpha)})"
        )
    }
}

