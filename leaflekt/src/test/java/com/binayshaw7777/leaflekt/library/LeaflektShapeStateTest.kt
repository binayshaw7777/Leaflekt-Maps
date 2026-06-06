package com.binayshaw7777.leaflekt.library

import com.binayshaw7777.leaflekt.library.circle.CircleState
import com.binayshaw7777.leaflekt.library.polygon.PolygonState
import com.binayshaw7777.leaflekt.library.polyline.PolylineState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaflektShapeStateTest {

    @Test
    fun polylineStateToggleSelectionUpdatesSelectionFlag() {
        val state = PolylineState()

        assertFalse(state.isSelected)

        state.toggleSelection()
        assertTrue(state.isSelected)

        state.deselect()
        assertFalse(state.isSelected)
    }

    @Test
    fun polygonStateSelectUpdatesSelectionFlag() {
        val state = PolygonState()

        state.select()

        assertTrue(state.isSelected)
    }

    @Test
    fun circleStateToggleSelectionUpdatesSelectionFlag() {
        val state = CircleState()

        state.toggleSelection()
        assertTrue(state.isSelected)

        state.toggleSelection()
        assertFalse(state.isSelected)
    }
}

