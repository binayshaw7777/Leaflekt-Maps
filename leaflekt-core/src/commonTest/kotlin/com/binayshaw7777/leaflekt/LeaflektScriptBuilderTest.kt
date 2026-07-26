package com.binayshaw7777.leaflekt

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LeaflektScriptBuilderTest {

    private val builder get() = LeaflektScriptBuilder

    @Test
    fun moveCameraScriptContainsBridgeCall() {
        val script = builder.moveCameraScript(22.5, 88.3, 12.0)
        assertContains(script, "moveCamera")
        assertContains(script, "22.5")
        assertContains(script, "88.3")
        assertContains(script, "12.0")
    }

    @Test
    fun animateCameraScriptContainsDuration() {
        val script = builder.animateCameraScript(0.0, 0.0, 10.0, 500)
        assertContains(script, "animateCamera")
        assertContains(script, "500")
    }

    @Test
    fun addMarkersScriptIsArray() {
        val marker = LeaflektMarkerInfo(id = "m1", lat = 22.0, lng = 88.0)
        val script = builder.addMarkersScript(listOf(marker))
        assertContains(script, "addMarkers")
        assertContains(script, "m1")
        assertContains(script, "[")
        assertContains(script, "]")
    }

    @Test
    fun removeMarkerScriptEncodesIdSafely() {
        val id = "id\"with\"quotes"
        val script = builder.removeMarkerScript(id)
        assertContains(script, "removeMarker")
        assertFalse(script.contains("\"id\"with\"quotes\""))
        assertTrue(script.contains("\\\"with\\\""))
    }

    @Test
    fun removeMarkersScriptBatches() {
        val script = builder.removeMarkersScript(listOf("a", "b", "c"))
        assertContains(script, "removeMarkers")
        assertContains(script, "\"a\"")
        assertContains(script, "\"b\"")
        assertContains(script, "\"c\"")
    }

    @Test
    fun clearMapScriptPresent() {
        val script = builder.clearMapScript()
        assertContains(script, "clearMap")
    }

    @Test
    fun fitBoundsScriptContainsCoords() {
        val sw = LeaflektLatLng(10.0, 20.0)
        val ne = LeaflektLatLng(30.0, 40.0)
        val script = builder.fitBoundsScript(sw, ne, 10)
        assertContains(script, "fitBounds")
        assertContains(script, "10.0")
        assertContains(script, "20.0")
        assertContains(script, "30.0")
        assertContains(script, "40.0")
    }

    @Test
    fun addPolylineScriptContainsPoints() {
        val info = LeaflektPolylineInfo(
            id = "pl1",
            points = listOf(LeaflektLatLng(0.0, 0.0), LeaflektLatLng(1.0, 1.0))
        )
        val script = builder.addPolylineScript(info)
        assertContains(script, "addPolyline")
        assertContains(script, "pl1")
    }

    @Test
    fun addCircleScriptContainsRadius() {
        val info = LeaflektCircleInfo(
            id = "c1",
            center = LeaflektLatLng(22.0, 88.0),
            radiusMeters = 500.0
        )
        val script = builder.addCircleScript(info)
        assertContains(script, "addCircle")
        assertContains(script, "500.0")
    }

    @Test
    fun setZoomBoundsScriptContainsValues() {
        val script = builder.setZoomBoundsScript(3.0, 18.0)
        assertContains(script, "setZoomBounds")
        assertContains(script, "3.0")
        assertContains(script, "18.0")
    }

    @Test
    fun allScriptsWrappedInTryCatch() {
        val script = builder.moveCameraScript(0.0, 0.0, 10.0)
        assertTrue(script.startsWith("try{"))
        assertTrue(script.contains("catch(e)"))
        assertTrue(script.contains("nativeBridge.onError"))
    }

    @Test
    fun initMapBatchScriptWrapsEachCall() {
        val script = builder.initMapBatchScript(
            0.0, 0.0, 10.0, true, LeaflektMapStyle.OpenStreetMap,
            LeaflektGeoJsonOverlay.India, 10
        )
        assertTrue(script.count { it == '{' } > 1, "expected multiple try blocks")
        assertContains(script, "initMap")
        assertContains(script, "setZoomControlsEnabled")
        assertContains(script, "setMapStyle")
    }

    @Test
    fun setGeoJsonOverlayScriptIndia() {
        val script = builder.setGeoJsonOverlayScript(LeaflektGeoJsonOverlay.India)
        assertContains(script, "setGeoJsonOverlay")
        assertContains(script, "null")
    }

    @Test
    fun setGeoJsonOverlayScriptNone() {
        val script = builder.setGeoJsonOverlayScript(LeaflektGeoJsonOverlay.None)
        assertContains(script, "setGeoJsonOverlay")
        assertContains(script, "\"none\"")
    }

    @Test
    fun setGeoJsonOverlayScriptCustomContainsData() {
        val script = builder.setGeoJsonOverlayScript(LeaflektGeoJsonOverlay.Custom("{\"type\":\"FeatureCollection\"}"))
        assertContains(script, "setGeoJsonOverlay")
        assertContains(script, "FeatureCollection")
    }

    @Test
    fun clearMarkersScriptPresent() {
        val script = builder.clearMarkersScript()
        assertContains(script, "clearMarkers")
        assertTrue(script.startsWith("try{"))
    }

    @Test
    fun addPolygonScriptContainsId() {
        val polygon = LeaflektPolygonInfo(
            id = "poly1",
            points = listOf(
                LeaflektLatLng(0.0, 0.0),
                LeaflektLatLng(1.0, 0.0),
                LeaflektLatLng(1.0, 1.0)
            )
        )
        val script = builder.addPolygonScript(polygon)
        assertContains(script, "addPolygon")
        assertContains(script, "poly1")
    }

    @Test
    fun updateCircleScriptContainsId() {
        val circle = LeaflektCircleInfo(
            id = "circle1",
            center = LeaflektLatLng(10.0, 20.0),
            radiusMeters = 100.0
        )
        val script = builder.updateCircleScript(circle)
        assertContains(script, "updateCircle")
        assertContains(script, "circle1")
    }

    @Test
    fun createClusterGroupScriptContainsGroupId() {
        val script = builder.createClusterGroupScript("group1", 80)
        assertContains(script, "createClusterGroup")
        assertContains(script, "group1")
        assertContains(script, "80")
    }

    @Test
    fun removeClusterGroupScriptContainsGroupId() {
        val script = builder.removeClusterGroupScript("group1")
        assertContains(script, "removeClusterGroup")
        assertContains(script, "group1")
    }
}
