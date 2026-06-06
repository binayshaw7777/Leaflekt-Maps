package com.binayshaw7777.leaflekt

import android.webkit.JavascriptInterface

class LeaflektJsBridgeAndroid(private val callbacks: LeaflektBridgeCallbacks) {
    @JavascriptInterface fun onMapReady() = callbacks.onMapReady()
    @JavascriptInterface fun onMapClick(lat: Double, lng: Double) = callbacks.onMapClick(lat, lng)
    @JavascriptInterface fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double) = callbacks.onCameraMoveStarted(lat, lng, zoom)
    @JavascriptInterface fun onCameraMove(lat: Double, lng: Double, zoom: Double) = callbacks.onCameraMove(lat, lng, zoom)
    @JavascriptInterface fun onCameraIdle(lat: Double, lng: Double, zoom: Double) = callbacks.onCameraIdle(lat, lng, zoom)
    @JavascriptInterface fun onMarkerClick(markerId: String) = callbacks.onMarkerClick(markerId)
    @JavascriptInterface fun onPolylineClick(polylineId: String) = callbacks.onPolylineClick(polylineId)
    @JavascriptInterface fun onPolygonClick(polygonId: String) = callbacks.onPolygonClick(polygonId)
    @JavascriptInterface fun onCircleClick(circleId: String) = callbacks.onCircleClick(circleId)
}
