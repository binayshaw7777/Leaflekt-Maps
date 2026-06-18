package com.binayshaw7777.leaflekt

internal object LeaflektScriptBuilder {
    fun initMapScript(lat: Double, lng: Double, zoom: Double, tileBufferSize: Int): String =
        "window.LeaflektBridge.initMap($lat,$lng,$zoom,$tileBufferSize);"

    fun initMapBatchScript(
        lat: Double, lng: Double, zoom: Double,
        isZoomControlEnabled: Boolean,
        style: LeaflektMapStyle,
        overlay: LeaflektGeoJsonOverlay,
        tileBufferSize: Int
    ): String = buildList {
        add(initMapScript(lat, lng, zoom, tileBufferSize))
        add(setZoomControlsEnabledScript(isZoomControlEnabled))
        add(setMapStyleScript(style))
        if (overlay !is LeaflektGeoJsonOverlay.India) add(setGeoJsonOverlayScript(overlay))
    }.joinToString("")

    fun setZoomControlsEnabledScript(isEnabled: Boolean): String =
        "window.LeaflektBridge.setZoomControlsEnabled($isEnabled);"

    fun setScrollGesturesEnabledScript(isEnabled: Boolean): String =
        "window.LeaflektBridge.setScrollGesturesEnabled($isEnabled);"

    fun setZoomGesturesEnabledScript(isEnabled: Boolean): String =
        "window.LeaflektBridge.setZoomGesturesEnabled($isEnabled);"

    fun setMapStyleScript(style: LeaflektMapStyle): String =
        "window.LeaflektBridge.setMapStyle(${style.toJson()});"

    fun setZoomBoundsScript(minZoom: Double, maxZoom: Double): String =
        "window.LeaflektBridge.setZoomBounds($minZoom,$maxZoom);"

    fun moveCameraScript(lat: Double, lng: Double, zoom: Double): String =
        "window.LeaflektBridge.moveCamera($lat,$lng,$zoom);"

    fun animateCameraScript(lat: Double, lng: Double, zoom: Double, duration: Int): String =
        "window.LeaflektBridge.animateCamera($lat,$lng,$zoom,$duration);"

    fun addMarkersScript(markers: List<LeaflektMarkerInfo>): String {
        val payload = markers.joinToString(prefix = "[", postfix = "]") { it.toJson() }
        return "window.LeaflektBridge.addMarkers($payload);"
    }

    fun updateMarkerScript(marker: LeaflektMarkerInfo): String =
        "window.LeaflektBridge.updateMarker(${marker.toJson()});"

    fun removeMarkerScript(markerId: String): String =
        "window.LeaflektBridge.removeMarker('$markerId');"

    fun clearMarkersScript(): String = "window.LeaflektBridge.clearMarkers();"

    fun addPolylineScript(polyline: LeaflektPolylineInfo): String =
        "window.LeaflektBridge.addPolyline(${polyline.toJson()});"

    fun updatePolylineScript(polyline: LeaflektPolylineInfo): String =
        "window.LeaflektBridge.updatePolyline(${polyline.toJson()});"

    fun removePolylineScript(polylineId: String): String =
        "window.LeaflektBridge.removePolyline(${LeaflektMapJson.encodeString(polylineId)});"

    fun addPolygonScript(polygon: LeaflektPolygonInfo): String =
        "window.LeaflektBridge.addPolygon(${polygon.toJson()});"

    fun updatePolygonScript(polygon: LeaflektPolygonInfo): String =
        "window.LeaflektBridge.updatePolygon(${polygon.toJson()});"

    fun removePolygonScript(polygonId: String): String =
        "window.LeaflektBridge.removePolygon(${LeaflektMapJson.encodeString(polygonId)});"

    fun addCircleScript(circle: LeaflektCircleInfo): String =
        "window.LeaflektBridge.addCircle(${circle.toJson()});"

    fun updateCircleScript(circle: LeaflektCircleInfo): String =
        "window.LeaflektBridge.updateCircle(${circle.toJson()});"

    fun removeCircleScript(circleId: String): String =
        "window.LeaflektBridge.removeCircle(${LeaflektMapJson.encodeString(circleId)});"

    fun createClusterGroupScript(groupId: String, maxClusterRadius: Int): String =
        "window.LeaflektBridge.createClusterGroup(${LeaflektMapJson.encodeString(groupId)},$maxClusterRadius);"

    fun addMarkersToClusterScript(groupId: String, markers: List<LeaflektMarkerInfo>): String {
        val payload = markers.joinToString(prefix = "[", postfix = "]") { it.toJson() }
        return "window.LeaflektBridge.addMarkersToCluster(${LeaflektMapJson.encodeString(groupId)},$payload);"
    }

    fun removeClusterGroupScript(groupId: String): String =
        "window.LeaflektBridge.removeClusterGroup(${LeaflektMapJson.encodeString(groupId)});"

    fun setGeoJsonOverlayScript(overlay: LeaflektGeoJsonOverlay): String = when (overlay) {
        is LeaflektGeoJsonOverlay.India -> "window.LeaflektBridge.setGeoJsonOverlay(null);"
        is LeaflektGeoJsonOverlay.None -> "window.LeaflektBridge.setGeoJsonOverlay(\"none\");"
        is LeaflektGeoJsonOverlay.Custom ->
            "window.LeaflektBridge.setGeoJsonOverlay(${LeaflektMapJson.encodeString(overlay.geojson)});"
    }

    private fun LeaflektMarkerInfo.toJson(): String {
        val iconJson = icon?.let {
            """{"dataUrl":${LeaflektMapJson.encodeString(it.dataUrl)},"widthPx":${it.widthPx},"heightPx":${it.heightPx},"anchorFractionX":${it.anchorFractionX},"anchorFractionY":${it.anchorFractionY}}"""
        } ?: "null"
        return """{"id":${LeaflektMapJson.encodeString(id ?: "")},"lat":$lat,"lng":$lng,"title":${LeaflektMapJson.encodeNullableString(title)},"snippet":${LeaflektMapJson.encodeNullableString(snippet)},"visible":$visible,"alpha":$alpha,"zIndex":$zIndex,"rotationDegrees":$rotationDegrees,"icon":$iconJson}"""
    }

    private fun LeaflektMapStyle.toJson(): String {
        val subdomainsJson = subdomains?.let(LeaflektMapJson::encodeString) ?: "null"
        return """{"id":${LeaflektMapJson.encodeString(id)},"tileUrlTemplate":${LeaflektMapJson.encodeString(url)},"attributionHtml":${LeaflektMapJson.encodeString(attribution)},"maxZoom":$maxZoom,"subdomains":$subdomainsJson,"isVectorStyle":$isVectorStyle,"backgroundColor":${LeaflektMapJson.encodeString(backgroundColor)}}"""
    }

    private fun LeaflektPolylineInfo.toJson(): String =
        """{"id":${LeaflektMapJson.encodeString(id)},"points":${LeaflektMapJson.encodeLatLngList(points)},"clickable":$clickable,"color":${LeaflektMapJson.encodeString(color.toCssRgba(alpha))},"geodesic":$geodesic,"pattern":${pattern.toJson()},"visible":$visible,"width":$width,"zIndex":$zIndex}"""

    private fun LeaflektPolygonInfo.toJson(): String =
        """{"id":${LeaflektMapJson.encodeString(id)},"points":${LeaflektMapJson.encodeLatLngList(points)},"clickable":$clickable,"fillColor":${LeaflektMapJson.encodeString(fillColor.toCssRgba(fillOpacity))},"geodesic":$geodesic,"holes":${LeaflektMapJson.encodeLatLngHoles(holes)},"strokeColor":${LeaflektMapJson.encodeString(strokeColor.toCssRgba(strokeOpacity))},"strokePattern":${strokePattern.toJson()},"strokeWidth":$strokeWidth,"visible":$visible,"zIndex":$zIndex}"""

    private fun LeaflektCircleInfo.toJson(): String =
        """{"id":${LeaflektMapJson.encodeString(id)},"center":${LeaflektMapJson.encodeLatLng(center)},"clickable":$clickable,"fillColor":${LeaflektMapJson.encodeString(fillColor.toCssRgba(fillOpacity))},"radiusMeters":$radiusMeters,"strokeColor":${LeaflektMapJson.encodeString(strokeColor.toCssRgba(strokeOpacity))},"strokePattern":${strokePattern.toJson()},"strokeWidth":$strokeWidth,"visible":$visible,"zIndex":$zIndex}"""

    private fun List<LeaflektStrokePattern>?.toJson(): String {
        this ?: return "null"
        return joinToString(prefix = "[", postfix = "]") { pattern ->
            when (pattern) {
                is LeaflektStrokePattern.Dash -> """{"type":"dash","length":${pattern.length}}"""
                is LeaflektStrokePattern.Gap -> """{"type":"gap","length":${pattern.length}}"""
                is LeaflektStrokePattern.Dot -> """{"type":"dot","radius":${pattern.radius}}"""
            }
        }
    }
}

internal fun LeaflektColor.toCssJsonString(alpha: Float): String =
    LeaflektMapJson.encodeString(toCssRgba(alpha))
