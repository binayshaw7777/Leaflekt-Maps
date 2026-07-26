package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.saveable.SaverScope
import com.binayshaw7777.leaflekt.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.LeaflektLatLng
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CameraPositionStateSaverTest {

    @Test
    fun saverExistsAndIsNotNull() {
        assertNotNull(LeaflektCameraPositionState.Saver)
    }

    @Test
    fun statePreservesLatitude() {
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 14.0)
        )
        assertEquals(22.5726, state.position.target.latitude, 0.0001)
    }

    @Test
    fun statePreservesLongitude() {
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 14.0)
        )
        assertEquals(88.3639, state.position.target.longitude, 0.0001)
    }

    @Test
    fun statePreservesZoom() {
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(0.0, 0.0), 16.5)
        )
        assertEquals(16.5, state.position.zoom, 0.0001)
    }

    @Test
    fun stateRoundTripWithExtremeValues() {
        val lat = -89.9
        val lng = -179.9
        val zoom = 2.0
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(lat, lng), zoom)
        )
        val restored = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(
                target = LeaflektLatLng(state.position.target.latitude, state.position.target.longitude),
                zoom = state.position.zoom
            )
        )
        assertEquals(lat, restored.position.target.latitude, 0.0001)
        assertEquals(lng, restored.position.target.longitude, 0.0001)
        assertEquals(zoom, restored.position.zoom, 0.0001)
    }

    @Test
    fun animateToUpdatesPositionWhenNotBound() {
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(0.0, 0.0), 10.0)
        )
        state.animate(LeaflektLatLng(22.5726, 88.3639), zoom = 14.0)
        assertEquals(22.5726, state.position.target.latitude, 0.0001)
        assertEquals(14.0, state.position.zoom, 0.0001)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun saverRoundTripViaActualSaverApi() {
        val lat = 22.5726
        val lng = 88.3639
        val zoom = 14.0
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(lat, lng), zoom)
        )
        val saver = LeaflektCameraPositionState.Saver as
            androidx.compose.runtime.saveable.Saver<LeaflektCameraPositionState, Any>
        val alwaysTrue = SaverScope { true }
        val saved = with(saver) { alwaysTrue.save(state) }
        assertNotNull(saved)
        val restored = saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(lat, restored!!.position.target.latitude, 0.0001)
        assertEquals(lng, restored.position.target.longitude, 0.0001)
        assertEquals(zoom, restored.position.zoom, 0.0001)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun saverPreservesExtremeValuesRoundTrip() {
        val state = LeaflektCameraPositionState(
            initialPosition = LeaflektCameraPosition(LeaflektLatLng(-89.9, -179.9), 2.0)
        )
        val saver = LeaflektCameraPositionState.Saver as
            androidx.compose.runtime.saveable.Saver<LeaflektCameraPositionState, Any>
        val alwaysTrue = SaverScope { true }
        val saved = with(saver) { alwaysTrue.save(state) }
        val restored = saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(-89.9, restored!!.position.target.latitude, 0.0001)
        assertEquals(-179.9, restored.position.target.longitude, 0.0001)
        assertEquals(2.0, restored.position.zoom, 0.0001)
    }
}
