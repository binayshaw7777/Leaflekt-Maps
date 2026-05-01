package com.binayshaw7777.leaflekt.library.controller

import android.webkit.WebView
import androidx.compose.runtime.mutableStateMapOf
import com.binayshaw7777.leaflekt.internal.projection.MapProjection
import com.binayshaw7777.leaflekt.internal.script.LeaflektScriptBuilder
import com.binayshaw7777.leaflekt.internal.serialization.LeaflektMapJson
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng
import com.binayshaw7777.leaflekt.library.circle.LeaflektCircleInfo
import com.binayshaw7777.leaflekt.library.cluster.MarkerClusterOptions
import com.binayshaw7777.leaflekt.library.map.LeaflektMapStyle
import com.binayshaw7777.leaflekt.library.marker.LeaflektMarkerInfo
import com.binayshaw7777.leaflekt.library.polygon.LeaflektPolygonInfo
import com.binayshaw7777.leaflekt.library.polyline.LeaflektPolylineInfo
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Stateful command gateway from Compose into the Leaflekt JavaScript runtime.
 */
class LeaflektController internal constructor() {
    private var webView: WebView? = null
    private val pendingScripts = ConcurrentLinkedQueue<String>()
    private val markerClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val clusterClickHandlers = mutableMapOf<String, (lat: Double, lng: Double, count: Int) -> Unit>()
    private val polylineClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val polygonClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val circleClickHandlers = mutableMapOf<String, () -> Boolean>()
    private var currentLocationCenteringAction: ((Double) -> Unit)? = null
    private var isMapReady = false

    internal val projectionState = mutableStateMapOf<LeaflektLatLng, MapOverlayProjection>()
    private val overlayPointRefCount = mutableMapOf<LeaflektLatLng, Int>()

    fun registerOverlayPoint(latLng: LeaflektLatLng) {
        val count = overlayPointRefCount.getOrDefault(latLng, 0)
        overlayPointRefCount[latLng] = count + 1
        
        if (count == 0) {
            enqueueOrRun("window.LeaflektBridge.registerOverlayPoint(${latLng.latitude}, ${latLng.longitude});")
        }
    }

    fun unregisterOverlayPoint(latLng: LeaflektLatLng) {
        val count = overlayPointRefCount.getOrDefault(latLng, 0)
        if (count <= 1) {
            overlayPointRefCount.remove(latLng)
            enqueueOrRun("window.LeaflektBridge.unregisterOverlayPoint(${latLng.latitude}, ${latLng.longitude});")
            projectionState.remove(latLng)
        } else {
            overlayPointRefCount[latLng] = count - 1
        }
    }

    internal fun onProjectionChanged(projectionsJson: String) {
        try {
            val list: List<MapProjection> = LeaflektMapJson.decodeFromString(projectionsJson)
            list.forEach { projection ->
                projectionState[LeaflektLatLng(projection.lat, projection.lng)] = MapOverlayProjection(
                    xFraction = projection.xFraction.toFloat(),
                    yFraction = projection.yFraction.toFloat()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    internal fun addMarker(marker: LeaflektMarkerInfo, clusterId: String? = null) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(listOf(marker), clusterId))
    }

    internal fun addMarkers(markers: List<LeaflektMarkerInfo>, clusterId: String? = null) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(markers, clusterId))
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

    internal fun createMarkerClusterGroup(clusterId: String, options: MarkerClusterOptions) {
        enqueueOrRun(LeaflektScriptBuilder.createMarkerClusterGroupScript(clusterId, options))
    }

    internal fun removeMarkerClusterGroup(clusterId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeMarkerClusterGroupScript(clusterId))
    }

    internal fun showMarkerInfoWindow(markerId: String) {
        enqueueOrRun("window.LeaflektBridge.showMarkerInfoWindow(${LeaflektMapJson.encodeString(markerId)});")
    }

    internal fun hideMarkerInfoWindow(markerId: String) {
        enqueueOrRun("window.LeaflektBridge.hideMarkerInfoWindow(${LeaflektMapJson.encodeString(markerId)});")
    }

    internal fun registerMarkerClick(markerId: String, onClick: () -> Boolean) {
        markerClickHandlers[markerId] = onClick
    }

    internal fun unregisterMarkerClick(markerId: String) {
        markerClickHandlers.remove(markerId)
    }

    internal fun registerClusterClick(clusterId: String, onClick: (lat: Double, lng: Double, count: Int) -> Unit) {
        clusterClickHandlers[clusterId] = onClick
    }

    internal fun unregisterClusterClick(clusterId: String) {
        clusterClickHandlers.remove(clusterId)
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

    internal fun registerPolylineClick(polylineId: String, onClick: () -> Boolean) {
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

    internal fun registerPolygonClick(polygonId: String, onClick: () -> Boolean) {
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

    internal fun registerCircleClick(circleId: String, onClick: () -> Boolean) {
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
        minZoom: Double,
        maxZoom: Double,
        restrictToWorldBounds: Boolean
    ) {
        enqueueOrRun(LeaflektScriptBuilder.initMapScript(initialLat, initialLng, initialZoom, initialBearing, minZoom, maxZoom, restrictToWorldBounds))
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isZoomControlEnabled))
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

    internal fun notifyClusterClick(clusterId: String, lat: Double, lng: Double, count: Int) {
        clusterClickHandlers[clusterId]?.invoke(lat, lng, count)
    }

    internal fun notifyPolylineClick(polylineId: String): Boolean {
        return polylineClickHandlers[polylineId]?.invoke() ?: false
    }

    internal fun notifyPolygonClick(polygonId: String): Boolean {
        return polygonClickHandlers[polygonId]?.invoke() ?: false
    }

    internal fun notifyCircleClick(circleId: String): Boolean {
        return circleClickHandlers[circleId]?.invoke() ?: false
    }
}

internal data class MapOverlayProjection(
    val xFraction: Float,
    val yFraction: Float
)
