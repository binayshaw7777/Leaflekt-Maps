package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.LeaflektLatLng
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SaverTest {

    @Test
    fun cameraPositionRoundTrip() {
        val original = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 14.0
        )

        val state = LeaflektCameraPositionState(initialPosition = original)
        assertEquals(22.5726, state.position.target.latitude, 0.0001)
        assertEquals(88.3639, state.position.target.longitude, 0.0001)
        assertEquals(14.0, state.position.zoom, 0.0001)
    }

    @Test
    fun latLngEquality() {
        val a = LeaflektLatLng(22.5726, 88.3639)
        val b = LeaflektLatLng(22.5726, 88.3639)
        assertEquals(a, b)
    }

    @Test
    fun cameraPositionEquality() {
        val a = LeaflektCameraPosition(LeaflektLatLng(22.0, 88.0), 12.0)
        val b = LeaflektCameraPosition(LeaflektLatLng(22.0, 88.0), 12.0)
        assertEquals(a, b)
    }

    @Test
    fun markerStatePosition() {
        val state = LeaflektMarkerState(LeaflektLatLng(28.6139, 77.2090))
        assertEquals(28.6139, state.position.latitude, 0.0001)
        assertEquals(77.2090, state.position.longitude, 0.0001)
    }

    @Test
    fun polylineStateSelection() {
        val state = LeaflektPolylineState(listOf(LeaflektLatLng(1.0, 1.0), LeaflektLatLng(2.0, 2.0)))
        state.select()
        assertTrue(state.isSelected)
        state.deselect()
        assertTrue(!state.isSelected)
        state.toggleSelection()
        assertTrue(state.isSelected)
    }

    @Test
    fun circleStateRadiusUpdate() {
        val state = LeaflektCircleState(LeaflektLatLng(22.0, 88.0), radiusMeters = 100.0)
        assertEquals(100.0, state.radiusMeters, 0.001)
        state.radiusMeters = 200.0
        assertEquals(200.0, state.radiusMeters, 0.001)
    }

}
