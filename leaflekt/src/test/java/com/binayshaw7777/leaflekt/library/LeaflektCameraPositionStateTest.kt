package com.binayshaw7777.leaflekt.library

import com.binayshaw7777.leaflekt.library.camera.LeaflektCameraPosition
import com.binayshaw7777.leaflekt.library.camera.LeaflektCameraPositionState
import com.binayshaw7777.leaflekt.library.camera.LeaflektLatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaflektCameraPositionStateTest {

    @Test
    fun cameraMoveStartedUpdatesPositionAndMarksStateMoving() {
        val state = LeaflektCameraPositionState()
        val movedPosition = LeaflektCameraPosition(
            target = LeaflektLatLng(12.9716, 77.5946),
            zoom = 14.0
        )

        state.onCameraMoveStarted(movedPosition)

        assertEquals(movedPosition, state.position)
        assertTrue(state.isMoving)
    }

    @Test
    fun cameraMoveUpdatesPositionAndKeepsStateMoving() {
        val state = LeaflektCameraPositionState()
        val movedPosition = LeaflektCameraPosition(
            target = LeaflektLatLng(28.6139, 77.2090),
            zoom = 11.5
        )

        state.onCameraMove(movedPosition)

        assertEquals(movedPosition, state.position)
        assertTrue(state.isMoving)
    }

    @Test
    fun cameraIdleUpdatesPositionAndMarksStateIdle() {
        val state = LeaflektCameraPositionState()
        val idlePosition = LeaflektCameraPosition(
            target = LeaflektLatLng(19.0760, 72.8777),
            zoom = 10.0
        )

        state.onCameraMoveStarted(
            LeaflektCameraPosition(
                target = LeaflektLatLng(18.5204, 73.8567),
                zoom = 9.0
            )
        )

        state.onCameraIdle(idlePosition)

        assertEquals(idlePosition, state.position)
        assertFalse(state.isMoving)
    }
}
