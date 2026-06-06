package com.binayshaw7777.leaflekt.compose

internal interface LeaflektBridgeCallbacks {
    fun onMapReady()
    fun onMapClick(lat: Double, lng: Double)
    fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double)
    fun onCameraMove(lat: Double, lng: Double, zoom: Double)
    fun onCameraIdle(lat: Double, lng: Double, zoom: Double)
    fun onMarkerClick(markerId: String)
    fun onPolylineClick(polylineId: String)
    fun onPolygonClick(polygonId: String)
    fun onCircleClick(circleId: String)
}
