package com.binayshaw7777.leaflekt.library

import com.binayshaw7777.leaflekt.library.camera.CameraPosition
import com.binayshaw7777.leaflekt.library.camera.CameraPositionState
import com.binayshaw7777.leaflekt.library.camera.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraPositionStateTest {

    @Test
    fun cameraMoveStartedUpdatesPositionAndMarksStateMoving() {
        val state = CameraPositionState()
        val movedPosition = CameraPosition(
            target = LatLng(12.9716, 77.5946),
            zoom = 14.0
        )

        state.onCameraMoveStarted(movedPosition)

        assertEquals(movedPosition, state.position)
        assertTrue(state.isMoving)
    }

    @Test
    fun cameraMoveUpdatesPositionAndKeepsStateMoving() {
        val state = CameraPositionState()
        val movedPosition = CameraPosition(
            target = LatLng(28.6139, 77.2090),
            zoom = 11.5
        )

        state.onCameraMove(movedPosition)

        assertEquals(movedPosition, state.position)
        assertTrue(state.isMoving)
    }

    @Test
    fun cameraIdleUpdatesPositionAndMarksStateIdle() {
        val state = CameraPositionState()
        val idlePosition = CameraPosition(
            target = LatLng(19.0760, 72.8777),
            zoom = 10.0
        )

        state.onCameraMoveStarted(
            CameraPosition(
                target = LatLng(18.5204, 73.8567),
                zoom = 9.0
            )
        )

        state.onCameraIdle(idlePosition)

        assertEquals(idlePosition, state.position)
        assertFalse(state.isMoving)
    }
}

