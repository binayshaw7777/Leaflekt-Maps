package com.binayshaw7777.leaflekt.compose

interface LeaflektControllerInterface {
    fun moveCamera(lat: Double, lng: Double, zoom: Double)
    fun setZoomControlsEnabled(isEnabled: Boolean)
    fun executeJavaScript(script: String)
    fun addMarker(info: LeaflektMarkerInfo)
    fun removeMarker(id: String)
    fun updateMarker(info: LeaflektMarkerInfo)
    fun clearMarkers()
    fun addPolyline(info: LeaflektPolylineInfo)
    fun updatePolyline(info: LeaflektPolylineInfo)
    fun removePolyline(id: String)
    fun addPolygon(info: LeaflektPolygonInfo)
    fun updatePolygon(info: LeaflektPolygonInfo)
    fun removePolygon(id: String)
    fun addCircle(info: LeaflektCircleInfo)
    fun updateCircle(info: LeaflektCircleInfo)
    fun removeCircle(id: String)
}
