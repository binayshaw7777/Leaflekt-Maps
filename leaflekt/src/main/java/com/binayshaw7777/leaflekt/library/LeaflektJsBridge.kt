package com.binayshaw7777.leaflekt.library

import android.webkit.JavascriptInterface

internal class LeaflektJsBridge(
    private val onMapReady: () -> Unit,
    private val onMapClick: (Double, Double) -> Unit,
    private val onCameraMoveStarted: (Double, Double, Double, Double) -> Unit,
    private val onCameraMove: (Double, Double, Double, Double) -> Unit,
    private val onCameraIdle: (Double, Double, Double, Double) -> Unit,
    private val onMarkerClick: (String) -> Unit,
    private val onClusterClick: (String, Double, Double, Int) -> Unit,
    private val onPolylineClick: (String) -> Unit,
    private val onPolygonClick: (String) -> Unit,
    private val onCircleClick: (String) -> Unit,
    private val onProjectionChanged: (String) -> Unit
) {
    @JavascriptInterface
    fun onMapReady() {
        onMapReady.invoke()
    }

    @JavascriptInterface
    fun onMapClick(lat: Double, lng: Double) {
        onMapClick.invoke(lat, lng)
    }

    @JavascriptInterface
    fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double, bearing: Double) {
        onCameraMoveStarted.invoke(lat, lng, zoom, bearing)
    }

    @JavascriptInterface
    fun onCameraMove(lat: Double, lng: Double, zoom: Double, bearing: Double) {
        onCameraMove.invoke(lat, lng, zoom, bearing)
    }

    @JavascriptInterface
    fun onCameraIdle(lat: Double, lng: Double, zoom: Double, bearing: Double) {
        onCameraIdle.invoke(lat, lng, zoom, bearing)
    }

    @JavascriptInterface
    fun onProjectionChanged(projectionsJson: String) {
        onProjectionChanged.invoke(projectionsJson)
    }

    @JavascriptInterface
    fun onMarkerClick(markerId: String) {
        onMarkerClick.invoke(markerId)
    }

    @JavascriptInterface
    fun onClusterClick(clusterId: String, lat: Double, lng: Double, count: Int) {
        onClusterClick.invoke(clusterId, lat, lng, count)
    }

    @JavascriptInterface
    fun onPolylineClick(polylineId: String) {
        onPolylineClick.invoke(polylineId)
    }

    @JavascriptInterface
    fun onPolygonClick(polygonId: String) {
        onPolygonClick.invoke(polygonId)
    }

    @JavascriptInterface
    fun onCircleClick(circleId: String) {
        onCircleClick.invoke(circleId)
    }
}
