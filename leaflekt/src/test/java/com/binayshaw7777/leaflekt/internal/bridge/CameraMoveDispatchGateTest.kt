package com.binayshaw7777.leaflekt.internal.bridge

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CameraMoveDispatchGateTest {

    @Test
    fun firstMoveStartsNewDispatchWindow() {
        val time = TestTimeSource()
        val gate = CameraMoveDispatchGate(
            minimumDispatchIntervalMillis = 64L,
            timeSource = time::currentTimeMillis
        )

        assertTrue(gate.shouldDispatch())
    }

    @Test
    fun moveInsideDispatchWindowIsSkipped() {
        val time = TestTimeSource()
        val gate = CameraMoveDispatchGate(
            minimumDispatchIntervalMillis = 64L,
            timeSource = time::currentTimeMillis
        )

        assertTrue(gate.shouldDispatch())
        time.advanceBy(32L)

        assertFalse(gate.shouldDispatch())
    }

    @Test
    fun restartAllowsImmediateDispatch() {
        val time = TestTimeSource()
        val gate = CameraMoveDispatchGate(
            minimumDispatchIntervalMillis = 64L,
            timeSource = time::currentTimeMillis
        )

        assertTrue(gate.shouldDispatch())
        time.advanceBy(16L)
        assertFalse(gate.shouldDispatch())

        gate.restart()

        assertTrue(gate.shouldDispatch())
    }
}

private class TestTimeSource {
    private var currentTimeMillis = 0L

    fun advanceBy(durationMillis: Long) {
        currentTimeMillis += durationMillis
    }

    fun currentTimeMillis(): Long = currentTimeMillis
}
