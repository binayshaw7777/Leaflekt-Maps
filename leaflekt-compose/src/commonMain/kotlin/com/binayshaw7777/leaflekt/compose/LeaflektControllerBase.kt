package com.binayshaw7777.leaflekt.compose

abstract class LeaflektControllerBase : LeaflektControllerInterface {
    private val pendingScripts = ArrayDeque<String>()
    private val markerClickHandlers = mutableMapOf<String, () -> Boolean>()
    private val polylineClickHandlers = mutableMapOf<String, () -> Unit>()
    private val polygonClickHandlers = mutableMapOf<String, () -> Unit>()
    private val circleClickHandlers = mutableMapOf<String, () -> Unit>()
    private var currentLocationCenteringAction: ((Double) -> Unit)? = null
    protected var isMapReady = false

    protected open fun platformExecuteJs(script: String) {}

    internal fun notifyMapReady() {
        isMapReady = true
        while (pendingScripts.isNotEmpty()) {
            platformExecuteJs(pendingScripts.removeFirst())
        }
    }

    protected fun enqueueOrRun(script: String) {
        if (isMapReady) platformExecuteJs(script) else pendingScripts.add(script)
    }

    override fun moveCamera(lat: Double, lng: Double, zoom: Double) {
        enqueueOrRun(LeaflektScriptBuilder.moveCameraScript(lat, lng, zoom))
    }

    override fun setZoomControlsEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isEnabled))
    }

    override fun executeJavaScript(script: String) {
        enqueueOrRun(script)
    }

    override fun addMarker(info: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.addMarkersScript(listOf(info)))
    }

    override fun removeMarker(id: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeMarkerScript(id))
    }

    override fun updateMarker(info: LeaflektMarkerInfo) {
        enqueueOrRun(LeaflektScriptBuilder.updateMarkerScript(info))
    }

    override fun clearMarkers() {
        enqueueOrRun(LeaflektScriptBuilder.clearMarkersScript())
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

    fun setScrollGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setScrollGesturesEnabledScript(isEnabled))
    }

    fun setZoomGesturesEnabled(isEnabled: Boolean) {
        enqueueOrRun(LeaflektScriptBuilder.setZoomGesturesEnabledScript(isEnabled))
    }

    fun setMapStyle(style: LeaflektMapStyle) {
        enqueueOrRun(LeaflektScriptBuilder.setMapStyleScript(style))
    }

    fun setGeoJsonOverlay(overlay: LeaflektGeoJsonOverlay) {
        enqueueOrRun(LeaflektScriptBuilder.setGeoJsonOverlayScript(overlay))
    }

    fun createClusterGroup(groupId: String, maxClusterRadius: Int = 80) {
        enqueueOrRun(LeaflektScriptBuilder.createClusterGroupScript(groupId, maxClusterRadius))
    }

    fun addMarkersToCluster(groupId: String, markers: List<LeaflektMarkerInfo>) {
        if (markers.isEmpty()) return
        enqueueOrRun(LeaflektScriptBuilder.addMarkersToClusterScript(groupId, markers))
    }

    fun removeClusterGroup(groupId: String) {
        enqueueOrRun(LeaflektScriptBuilder.removeClusterGroupScript(groupId))
    }

    fun centerOnCurrentLocation(zoom: Double = 16.0) {
        currentLocationCenteringAction?.invoke(zoom)
    }

    internal fun initializeMap(
        initialLat: Double, initialLng: Double, initialZoom: Double,
        isZoomControlEnabled: Boolean, initialMapStyle: LeaflektMapStyle,
        initialGeoJsonOverlay: LeaflektGeoJsonOverlay = LeaflektGeoJsonOverlay.India
    ) {
        enqueueOrRun(LeaflektScriptBuilder.initMapScript(initialLat, initialLng, initialZoom))
        enqueueOrRun(LeaflektScriptBuilder.setZoomControlsEnabledScript(isZoomControlEnabled))
        enqueueOrRun(LeaflektScriptBuilder.setMapStyleScript(initialMapStyle))
        if (initialGeoJsonOverlay !is LeaflektGeoJsonOverlay.India) {
            enqueueOrRun(LeaflektScriptBuilder.setGeoJsonOverlayScript(initialGeoJsonOverlay))
        }
    }

    internal fun registerMarkerClick(markerId: String, onClick: () -> Boolean) {
        markerClickHandlers[markerId] = onClick
    }
    internal fun unregisterMarkerClick(markerId: String) { markerClickHandlers.remove(markerId) }

    internal fun registerPolylineClick(polylineId: String, onClick: () -> Unit) {
        polylineClickHandlers[polylineId] = onClick
    }
    internal fun unregisterPolylineClick(polylineId: String) { polylineClickHandlers.remove(polylineId) }

    internal fun registerPolygonClick(polygonId: String, onClick: () -> Unit) {
        polygonClickHandlers[polygonId] = onClick
    }
    internal fun unregisterPolygonClick(polygonId: String) { polygonClickHandlers.remove(polygonId) }

    internal fun registerCircleClick(circleId: String, onClick: () -> Unit) {
        circleClickHandlers[circleId] = onClick
    }
    internal fun unregisterCircleClick(circleId: String) { circleClickHandlers.remove(circleId) }

    internal fun registerCurrentLocationCenteringAction(action: (Double) -> Unit) {
        currentLocationCenteringAction = action
    }
    internal fun unregisterCurrentLocationCenteringAction() { currentLocationCenteringAction = null }

    internal fun notifyMarkerClick(markerId: String): Boolean =
        markerClickHandlers[markerId]?.invoke() ?: false

    internal fun notifyPolylineClick(polylineId: String) { polylineClickHandlers[polylineId]?.invoke() }
    internal fun notifyPolygonClick(polygonId: String) { polygonClickHandlers[polygonId]?.invoke() }
    internal fun notifyCircleClick(circleId: String) { circleClickHandlers[circleId]?.invoke() }
}
