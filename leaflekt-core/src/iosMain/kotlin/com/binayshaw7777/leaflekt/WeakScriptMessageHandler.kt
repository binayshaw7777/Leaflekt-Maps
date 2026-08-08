package com.binayshaw7777.leaflekt

import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.darwin.NSObject

class WeakScriptMessageHandler(
    callbacks: LeaflektBridgeCallbacks
) : NSObject(), WKScriptMessageHandlerProtocol {
    var callbacks: LeaflektBridgeCallbacks = callbacks
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage
    ) {
        val body = didReceiveScriptMessage.body as? String ?: return
        val parts = body.split(":")
        when (parts.firstOrNull()) {
            "onMapReady" -> callbacks.onMapReady()
            "onMapFirstRender" -> callbacks.onMapFirstRender()
            "onMapClick" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                callbacks.onMapClick(lat, lng)
            }
            "onCameraMoveStarted" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraMoveStarted(lat, lng, zoom)
            }
            "onCameraMove" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraMove(lat, lng, zoom)
            }
            "onCameraIdle" -> {
                val lat = parts.getOrNull(1)?.toDoubleOrNull() ?: return
                val lng = parts.getOrNull(2)?.toDoubleOrNull() ?: return
                val zoom = parts.getOrNull(3)?.toDoubleOrNull() ?: return
                callbacks.onCameraIdle(lat, lng, zoom)
            }
            "onMarkerClick" -> callbacks.onMarkerClick(parts.getOrNull(1) ?: return)
            "onPolylineClick" -> callbacks.onPolylineClick(parts.getOrNull(1) ?: return)
            "onPolygonClick" -> callbacks.onPolygonClick(parts.getOrNull(1) ?: return)
            "onCircleClick" -> callbacks.onCircleClick(parts.getOrNull(1) ?: return)
        }
    }
}
