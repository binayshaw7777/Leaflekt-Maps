package com.binayshaw7777.leaflekt

import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface

class LeaflektJsBridgeAndroid(private val callbacks: LeaflektBridgeCallbacks) {
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface fun onMapReady() = main.post { callbacks.onMapReady() }
    @JavascriptInterface fun onMapFirstRender() = main.post { callbacks.onMapFirstRender() }
    @JavascriptInterface fun onMapClick(lat: Double, lng: Double) = main.post { callbacks.onMapClick(lat, lng) }
    @JavascriptInterface fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double) = main.post { callbacks.onCameraMoveStarted(lat, lng, zoom) }
    @JavascriptInterface fun onCameraMove(lat: Double, lng: Double, zoom: Double) = main.post { callbacks.onCameraMove(lat, lng, zoom) }
    @JavascriptInterface fun onCameraIdle(lat: Double, lng: Double, zoom: Double) = main.post { callbacks.onCameraIdle(lat, lng, zoom) }
    @JavascriptInterface fun onMarkerClick(markerId: String) = main.post { callbacks.onMarkerClick(markerId) }
    @JavascriptInterface fun onPolylineClick(polylineId: String) = main.post { callbacks.onPolylineClick(polylineId) }
    @JavascriptInterface fun onPolygonClick(polygonId: String) = main.post { callbacks.onPolygonClick(polygonId) }
    @JavascriptInterface fun onCircleClick(circleId: String) = main.post { callbacks.onCircleClick(circleId) }
}
