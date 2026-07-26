package com.binayshaw7777.leaflekt

internal object LeaflektScriptBuilder {

    private fun wrapSafe(call: String): String =
        "try{$call}catch(e){if(window.nativeBridge&&window.nativeBridge.onError)window.nativeBridge.onError(String(e));}"

    fun initMapBatchScript(
        lat: Double,
        lng: Double,
        zoom: Double,
        isZoomControlEnabled: Boolean,
        style: LeaflektMapStyle,
        geoJsonOverlay: LeaflektGeoJsonOverlay,
        tileBufferSize: Int
    ): String = buildString {
        append(wrapSafe("window.LeaflektBridge.initMap($lat,$lng,$zoom,$tileBufferSize)"))
        append(wrapSafe("window.LeaflektBridge.setZoomControlsEnabled($isZoomControlEnabled)"))
        append(wrapSafe("window.LeaflektBridge.setMapStyle(${style.toJson()})"))
        append(setGeoJsonOverlayScript(geoJsonOverlay))
    }

    fun initMapScript(lat: Double, lng: Double, zoom: Double): String =
        wrapSafe("window.LeaflektBridge.initMap($lat,$lng,$zoom)")

    fun setZoomControlsEnabledScript(isEnabled: Boolean): String =
        wrapSafe("window.LeaflektBridge.setZoomControlsEnabled($isEnabled)")

    fun setScrollGesturesEnabledScript(isEnabled: Boolean): String =
        wrapSafe("window.LeaflektBridge.setScrollGesturesEnabled($isEnabled)")

    fun setZoomGesturesEnabledScript(isEnabled: Boolean): String =
        wrapSafe("window.LeaflektBridge.setZoomGesturesEnabled($isEnabled)")

    fun setMapStyleScript(style: LeaflektMapStyle): String =
        wrapSafe("window.LeaflektBridge.setMapStyle(${style.toJson()})")

    fun setZoomBoundsScript(minZoom: Double, maxZoom: Double): String =
        wrapSafe("window.LeaflektBridge.setZoomBounds($minZoom,$maxZoom)")

    fun moveCameraScript(lat: Double, lng: Double, zoom: Double): String =
        wrapSafe("window.LeaflektBridge.moveCamera($lat,$lng,$zoom)")

    fun animateCameraScript(lat: Double, lng: Double, zoom: Double, duration: Int): String =
        wrapSafe("window.LeaflektBridge.animateCamera($lat,$lng,$zoom,$duration)")

    fun addMarkersScript(markers: List<LeaflektMarkerInfo>): String {
        val payload = markers.joinToString(prefix = "[", postfix = "]") { it.toJson() }
        return wrapSafe("window.LeaflektBridge.addMarkers($payload)")
    }

    fun updateMarkerScript(marker: LeaflektMarkerInfo): String =
        wrapSafe("window.LeaflektBridge.updateMarker(${marker.toJson()})")

    fun removeMarkerScript(markerId: String): String =
        wrapSafe("window.LeaflektBridge.removeMarker(${LeaflektMapJson.encodeString(markerId)})")

    fun removeMarkersScript(ids: List<String>): String {
        val payload = ids.joinToString(prefix = "[", postfix = "]") { LeaflektMapJson.encodeString(it) }
        return wrapSafe("window.LeaflektBridge.removeMarkers($payload)")
    }

    fun clearMarkersScript(): String = wrapSafe("window.LeaflektBridge.clearMarkers()")

    fun clearMapScript(): String = wrapSafe("window.LeaflektBridge.clearMap()")

    fun fitBoundsScript(sw: LeaflektLatLng, ne: LeaflektLatLng, paddingPx: Int): String =
        wrapSafe("window.LeaflektBridge.fitBounds(${LeaflektMapJson.encodeLatLng(sw)},${LeaflektMapJson.encodeLatLng(ne)},$paddingPx)")

    fun addPolylineScript(polyline: LeaflektPolylineInfo): String =
        wrapSafe("window.LeaflektBridge.addPolyline(${polyline.toJson()})")

    fun updatePolylineScript(polyline: LeaflektPolylineInfo): String =
        wrapSafe("window.LeaflektBridge.updatePolyline(${polyline.toJson()})")

    fun removePolylineScript(polylineId: String): String =
        wrapSafe("window.LeaflektBridge.removePolyline(${LeaflektMapJson.encodeString(polylineId)})")

    fun addPolygonScript(polygon: LeaflektPolygonInfo): String =
        wrapSafe("window.LeaflektBridge.addPolygon(${polygon.toJson()})")

    fun updatePolygonScript(polygon: LeaflektPolygonInfo): String =
        wrapSafe("window.LeaflektBridge.updatePolygon(${polygon.toJson()})")

    fun removePolygonScript(polygonId: String): String =
        wrapSafe("window.LeaflektBridge.removePolygon(${LeaflektMapJson.encodeString(polygonId)})")

    fun addCircleScript(circle: LeaflektCircleInfo): String =
        wrapSafe("window.LeaflektBridge.addCircle(${circle.toJson()})")

    fun updateCircleScript(circle: LeaflektCircleInfo): String =
        wrapSafe("window.LeaflektBridge.updateCircle(${circle.toJson()})")

    fun removeCircleScript(circleId: String): String =
        wrapSafe("window.LeaflektBridge.removeCircle(${LeaflektMapJson.encodeString(circleId)})")

    fun createClusterGroupScript(groupId: String, maxClusterRadius: Int): String =
        wrapSafe("window.LeaflektBridge.createClusterGroup(${LeaflektMapJson.encodeString(groupId)},$maxClusterRadius)")

    fun addMarkersToClusterScript(groupId: String, markers: List<LeaflektMarkerInfo>): String {
        val payload = markers.joinToString(prefix = "[", postfix = "]") { it.toJson() }
        return wrapSafe("window.LeaflektBridge.addMarkersToCluster(${LeaflektMapJson.encodeString(groupId)},$payload)")
    }

    fun removeClusterGroupScript(groupId: String): String =
        wrapSafe("window.LeaflektBridge.removeClusterGroup(${LeaflektMapJson.encodeString(groupId)})")

    fun setGeoJsonOverlayScript(overlay: LeaflektGeoJsonOverlay): String = when (overlay) {
        is LeaflektGeoJsonOverlay.India -> wrapSafe("window.LeaflektBridge.setGeoJsonOverlay(null)")
        is LeaflektGeoJsonOverlay.None -> wrapSafe("window.LeaflektBridge.setGeoJsonOverlay(\"none\")")
        is LeaflektGeoJsonOverlay.Custom ->
            wrapSafe("window.LeaflektBridge.setGeoJsonOverlay(${LeaflektMapJson.encodeString(overlay.geojson)})")
    }

    private fun LeaflektMarkerInfo.toJson(): String {
        val iconJson = icon?.let {
            """{"dataUrl":${LeaflektMapJson.encodeString(it.dataUrl)},"widthPx":${it.widthPx},"heightPx":${it.heightPx},"anchorFractionX":${it.anchorFractionX},"anchorFractionY":${it.anchorFractionY}}"""
        } ?: "null"
        return """{"id":${LeaflektMapJson.encodeString(id ?: "")},"lat":$lat,"lng":$lng,"title":${LeaflektMapJson.encodeNullableString(title)},"snippet":${LeaflektMapJson.encodeNullableString(snippet)},"visible":$visible,"alpha":$alpha,"zIndex":$zIndex,"rotationDegrees":$rotationDegrees,"icon":$iconJson}"""
    }

    private fun LeaflektMapStyle.toJson(): String {
        val subdomainsJson = subdomains?.let(LeaflektMapJson::encodeString) ?: "null"
        return """{"id":${LeaflektMapJson.encodeString(id)},"tileUrlTemplate":${LeaflektMapJson.encodeString(url)},"attributionHtml":${LeaflektMapJson.encodeString(attribution)},"maxZoom":$maxZoom,"subdomains":$subdomainsJson,"isVectorStyle":$isVectorStyle}"""
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
