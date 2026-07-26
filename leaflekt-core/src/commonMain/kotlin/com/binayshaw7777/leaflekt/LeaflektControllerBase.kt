package com.binayshaw7777.leaflekt

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class LeaflektControllerBase : LeaflektControllerInterface {
    private val pendingScripts = ArrayDeque<String>()
    // These maps are always accessed on the main thread (JS bridge posts to main, Compose runs on main).
    // ConcurrentHashMap is not available in KMP commonMain — main-thread-only invariant is the safety guarantee.
    private val markerClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val polylineClickHandlers = mutableMapOf<String, () -> Unit>()
    private val polygonClickHandlers = mutableMapOf<String, () -> Unit>()
    private val circleClickHandlers = mutableMapOf<String, () -> Unit>()
    private var currentLocationCenteringAction: ((Double) -> Unit)? = null
    protected var isMapReady = false

    private val _errors = MutableSharedFlow<LeaflektMapError>(extraBufferCapacity = 16)
    val errors: SharedFlow<LeaflektMapError> = _errors.asSharedFlow()

    fun emitError(error: LeaflektMapError) {
        _errors.tryEmit(error)
    }

    protected open fun platformExecuteJs(script: String) {}

    fun notifyMapReady() {
        isMapReady = true
        while (pendingScripts.isNotEmpty()) {
            platformExecuteJs(pendingScripts.removeFirst())
        }
    }

    fun resetState() {
        isMapReady = false
        pendingScripts.clear()
    }

    protected fun enqueueOrRun(script: String) {
        if (isMapReady) {
            platformExecuteJs(script)
        } else {
            if (pendingScripts.lastOrNull() == script) return
            if (pendingScripts.size >= MAX_PENDING_SCRIPTS) pendingScripts.removeFirst()
            pendingScripts.add(script)
        }
    }

    override fun moveCamera(lat: Double, lng: Double, zoom: Double) {
        enqueueOrRun(LeaflektScriptBuilder.moveCameraScript(lat, lng, zoom))
    }

    override fun animateCamera(lat: Double, lng: Double, zoom: Double, durationMillis: Int) {
        enqueueOrRun(LeaflektScriptBuilder.animateCameraScript(lat, lng, zoom, durationMillis))
    }

    override fun setZoomControlsEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isEnabled))
    }

    override fun executeJavaScript(script: String) {
        enqueueOrRun(script)
    }

    override fun setScrollGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setScrollGesturesEnabledScript(isEnabled))
    }

    override fun setZoomGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomGesturesEnabledScript(isEnabled))
    }

    override fun fitBounds(sw: LeaflektLatLng, ne: LeaflektLatLng, paddingPx: Int) {
        enqueueOrRun(LeaflektScriptBuilder.fitBoundsScript(sw, ne, paddingPx))
    }

    override fun addMarker(info: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(listOf(info)))
    }

    override fun addMarkers(markers: List<LeaflektMarkerInfo>) {
        if (markers.isEmpty()) return
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(markers))
    }

    override fun removeMarker(id: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeMarkerScript(id))
    }

    override fun removeMarkers(ids: List<String>) {
        if (ids.isEmpty()) return
        enqueueOrRun(LeaflektScriptBuilder.removeMarkersScript(ids))
    }

    override fun updateMarker(info: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updateMarkerScript(info))
    }

    override fun clearMarkers() {
        enqueueOrRun(LeaflektScriptBuilder.clearMarkersScript())
    }

    override fun clearMap() {
        enqueueOrRun(LeaflektScriptBuilder.clearMapScript())
    }

    override fun addPolyline(info: LeaflektPolylineInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addPolylineScript(info))
    }

    override fun updatePolyline(info: LeaflektPolylineInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updatePolylineScript(info))
    }

    override fun removePolyline(id: String) {
        enqueueOrRun(LeaflektScriptBuilder.removePolylineScript(id))
    }

    override fun addPolygon(info: LeaflektPolygonInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addPolygonScript(info))
    }

    override fun updatePolygon(info: LeaflektPolygonInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updatePolygonScript(info))
    }

    override fun removePolygon(id: String) {
        enqueueOrRun(LeaflektScriptBuilder.removePolygonScript(id))
    }

    override fun addCircle(info: LeaflektCircleInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addCircleScript(info))
    }

    override fun updateCircle(info: LeaflektCircleInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updateCircleScript(info))
    }

    override fun removeCircle(id: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeCircleScript(id))
    }

    override fun setMapStyle(style: LeaflektMapStyle) {
        enqueueOrRun(LeaflektScriptBuilder.setMapStyleScript(style))
    }

    override fun setGeoJsonOverlay(overlay: LeaflektGeoJsonOverlay) {
        enqueueOrRun(LeaflektScriptBuilder.setGeoJsonOverlayScript(overlay))
    }

    override fun setZoomBounds(minZoom: Double, maxZoom: Double) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomBoundsScript(minZoom, maxZoom))
    }

    override fun createClusterGroup(groupId: String, maxClusterRadius: Int) {
        enqueueOrRun(LeaflektScriptBuilder.createClusterGroupScript(groupId, maxClusterRadius))
    }

    override fun addMarkersToCluster(groupId: String, markers: List<LeaflektMarkerInfo>) {
        if (markers.isEmpty()) return
        enqueueOrRun(LeaflektScriptBuilder.addMarkersToClusterScript(groupId, markers))
    }

    override fun removeClusterGroup(groupId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeClusterGroupScript(groupId))
    }

    fun centerOnCurrentLocation(zoom: Double = 16.0) {
        currentLocationCenteringAction?.invoke(zoom)
    }

    fun prepareMap(
        initialLat: Double, initialLng: Double, initialZoom: Double,
        isZoomControlEnabled: Boolean, initialMapStyle: LeaflektMapStyle,
        initialGeoJsonOverlay: LeaflektGeoJsonOverlay = LeaflektGeoJsonOverlay.India,
        tileBufferSize: Int = 10
    ) {
        enqueueOrRun(LeaflektScriptBuilder.initMapBatchScript(
            initialLat, initialLng, initialZoom,
            isZoomControlEnabled, initialMapStyle, initialGeoJsonOverlay, tileBufferSize
        ))
    }

    fun registerMarkerClick(markerId: String, onClick: () -> Boolean) {
        markerClickHandlers[markerId] = onClick
    }
    fun unregisterMarkerClick(markerId: String) {
        markerClickHandlers.remove(markerId)
    }

    fun registerPolylineClick(polylineId: String, onClick: () -> Unit) {
        polylineClickHandlers[polylineId] = onClick
    }
    fun unregisterPolylineClick(polylineId: String) {
        polylineClickHandlers.remove(polylineId)
    }

    fun registerPolygonClick(polygonId: String, onClick: () -> Unit) {
        polygonClickHandlers[polygonId] = onClick
    }
    fun unregisterPolygonClick(polygonId: String) {
        polygonClickHandlers.remove(polygonId)
    }

    fun registerCircleClick(circleId: String, onClick: () -> Unit) {
        circleClickHandlers[circleId] = onClick
    }
    fun unregisterCircleClick(circleId: String) {
        circleClickHandlers.remove(circleId)
    }

    fun registerCurrentLocationCenteringAction(action: (Double) -> Unit) {
        currentLocationCenteringAction = action
    }
    fun unregisterCurrentLocationCenteringAction() {
        currentLocationCenteringAction = null
    }

    fun notifyMarkerClick(markerId: String): Boolean =
        markerClickHandlers[markerId]?.invoke() ?: false

    fun notifyPolylineClick(polylineId: String) {
        polylineClickHandlers[polylineId]?.invoke()
    }

    fun notifyPolygonClick(polygonId: String) {
        polygonClickHandlers[polygonId]?.invoke()
    }

    fun notifyCircleClick(circleId: String) {
        circleClickHandlers[circleId]?.invoke()
    }

    companion object {
        private const val MAX_PENDING_SCRIPTS = 200
    }
}
