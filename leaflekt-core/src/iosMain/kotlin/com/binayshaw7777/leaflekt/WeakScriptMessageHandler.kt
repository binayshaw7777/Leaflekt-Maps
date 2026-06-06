package com.binayshaw7777.leaflekt

import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.darwin.NSObject

class WeakScriptMessageHandler(
    private val callbacks: LeaflektBridgeCallbacks
) : NSObject(), WKScriptMessageHandlerProtocol {
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage
    ) {
        val body = didReceiveScriptMessage.body as? String ?: return
        val parts = body.split(":")
        when (parts.firstOrNull()) {
            "mapReady" -> callbacks.onMapReady()
            "mapClick" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                callbacks.onMapClick(lat, lng)
            }
            "cameraMoveStarted" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraMoveStarted(lat, lng, zoom)
            }
            "cameraMove" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraMove(lat, lng, zoom)
            }
            "cameraIdle" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraIdle(lat, lng, zoom)
            }
            "markerClick" -> callbacks.onMarkerClick(parts.getOrNull(1) ?: return)
            "polylineClick" -> callbacks.onPolylineClick(parts.getOrNull(1) ?: return)
            "polygonClick" -> callbacks.onPolygonClick(parts.getOrNull(1) ?: return)
            "circleClick" -> callbacks.onCircleClick(parts.getOrNull(1) ?: return)
        }
    }
}
