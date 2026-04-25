package com.binayshaw7777.leaflekt.library

import android.webkit.WebView
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Stateful command gateway from Compose into the Leaflekt JavaScript runtime.
 */
class LeaflektController internal constructor() {
    private var webView: WebView? = null
    private val pendingScripts = ConcurrentLinkedQueue<String>()
    private val markerClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val polylineClickHandlers = mutableMapOf<String, () -> Unit>()
    private val polygonClickHandlers = mutableMapOf<String, () -> Unit>()
    private val circleClickHandlers = mutableMapOf<String, () -> Unit>()
    private var currentLocationCenteringAction: ((Double) -> Unit)? = null
    private var isMapReady = false

    internal fun setWebView(view: WebView?) {
        webView = view
    }

    internal fun notifyMapReady() {
        isMapReady = true
        while (pendingScripts.isNotEmpty()) {
            pendingScripts.poll()?.let { executeJs(it) }
        }
    }

    fun moveCamera(lat: Double, lng: Double, zoom: Double, bearing: Double = 0.0) {
        enqueueOrRun(LeaflektScriptBuilder.moveCameraScript(lat = lat, lng = lng, zoom = zoom, bearing = bearing))
    }

    fun setZoomControlsEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isEnabled))
    }

    fun setScrollGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setScrollGesturesEnabledScript(isEnabled))
    }

    fun setZoomGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomGesturesEnabledScript(isEnabled))
    }

    fun setRotateGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setRotateGesturesEnabledScript(isEnabled))
    }

    fun setBearing(bearing: Double) {
        enqueueOrRun(LeaflektScriptBuilder.setBearingScript(bearing))
    }

    fun setMapStyle(style: LeaflektMapStyle) {
        enqueueOrRun(LeaflektScriptBuilder.setMapStyleScript(style))
    }

    fun centerOnCurrentLocation(zoom: Double = 16.0) {
        currentLocationCenteringAction?.invoke(zoom)
    }

    /**
     * Executes raw JavaScript against the managed Leaflet runtime.
     *
     * This is an extensibility escape hatch for features that are not yet wrapped by the public
     * Compose API. Scripts issued before the map is ready are queued and replayed after startup.
     */
    fun executeJavaScript(script: String) {
        enqueueOrRun(script)
    }

    internal fun addMarker(marker: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(listOf(marker)))
    }

    internal fun addMarkers(markers: List<LeaflektMarkerInfo>) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(markers))
    }

    internal fun updateMarker(marker: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updateMarkerScript(marker))
    }

    fun removeMarker(markerId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeMarkerScript(markerId))
    }

    fun clearMarkers() {
        enqueueOrRun(LeaflektScriptBuilder.clearMarkersScript())
    }

    internal fun registerMarkerClick(markerId: String, onClick: () -> Boolean) {
        markerClickHandlers[markerId] = onClick
    }

    internal fun unregisterMarkerClick(markerId: String) {
        markerClickHandlers.remove(markerId)
    }

    internal fun addPolyline(polyline: LeaflektPolylineInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addPolylineScript(polyline))
    }

    internal fun updatePolyline(polyline: LeaflektPolylineInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updatePolylineScript(polyline))
    }

    fun removePolyline(polylineId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removePolylineScript(polylineId))
    }

    internal fun registerPolylineClick(polylineId: String, onClick: () -> Unit) {
        polylineClickHandlers[polylineId] = onClick
    }

    internal fun unregisterPolylineClick(polylineId: String) {
        polylineClickHandlers.remove(polylineId)
    }

    internal fun addPolygon(polygon: LeaflektPolygonInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addPolygonScript(polygon))
    }

    internal fun updatePolygon(polygon: LeaflektPolygonInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updatePolygonScript(polygon))
    }

    fun removePolygon(polygonId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removePolygonScript(polygonId))
    }

    internal fun registerPolygonClick(polygonId: String, onClick: () -> Unit) {
        polygonClickHandlers[polygonId] = onClick
    }

    internal fun unregisterPolygonClick(polygonId: String) {
        polygonClickHandlers.remove(polygonId)
    }

    internal fun addCircle(circle: LeaflektCircleInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addCircleScript(circle))
    }

    internal fun updateCircle(circle: LeaflektCircleInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updateCircleScript(circle))
    }

    fun removeCircle(circleId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeCircleScript(circleId))
    }

    internal fun registerCircleClick(circleId: String, onClick: () -> Unit) {
        circleClickHandlers[circleId] = onClick
    }

    internal fun unregisterCircleClick(circleId: String) {
        circleClickHandlers.remove(circleId)
    }

    internal fun initializeMap(
        initialLat: Double,
        initialLng: Double,
        initialZoom: Double,
        initialBearing: Double,
        isZoomControlEnabled: Boolean,
        initialMapStyle: LeaflektMapStyle
    ) {
        enqueueOrRun(LeaflektScriptBuilder.initMapScript(initialLat, initialLng, initialZoom, initialBearing))
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isZoomControlEnabled))
        enqueueOrRun(LeaflektScriptBuilder.setMapStyleScript(initialMapStyle))
    }

    internal fun registerCurrentLocationCenteringAction(action: (Double) -> Unit) {
        currentLocationCenteringAction = action
    }

    internal fun unregisterCurrentLocationCenteringAction() {
        currentLocationCenteringAction = null
    }

    private fun enqueueOrRun(script: String) {
        if (isMapReady && webView != null) {
            executeJs(script)
        } else {
            pendingScripts.add(script)
        }
    }

    private fun executeJs(script: String) {
        webView?.post {
            webView?.evaluateJavascript(script, null)
        }
    }

    internal fun notifyMarkerClick(markerId: String): Boolean {
        return markerClickHandlers[markerId]?.invoke() ?: false
    }

    internal fun notifyPolylineClick(polylineId: String) {
        polylineClickHandlers[polylineId]?.invoke()
    }

    internal fun notifyPolygonClick(polygonId: String) {
        polygonClickHandlers[polygonId]?.invoke()
    }

    internal fun notifyCircleClick(circleId: String) {
        circleClickHandlers[circleId]?.invoke()
    }
}
