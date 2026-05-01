package com.binayshaw7777.leaflekt.internal.bridge

internal class CameraMoveDispatchGate(
    private val minimumDispatchIntervalMillis: Long = DEFAULT_CAMERA_MOVE_DISPATCH_INTERVAL_MILLIS,
    private val timeSource: () -> Long = { System.currentTimeMillis() }
) {
    private var lastDispatchAtMillis = Long.MIN_VALUE

    fun restart() {
        lastDispatchAtMillis = Long.MIN_VALUE
    }

    fun shouldDispatch(): Boolean {
        val now = timeSource()
        if (shouldStartNewDispatchWindow(now)) {
            lastDispatchAtMillis = now
            return true
        }
        return false
    }

    private fun shouldStartNewDispatchWindow(now: Long): Boolean {
        if (lastDispatchAtMillis == Long.MIN_VALUE) {
            return true
        }
        return now - lastDispatchAtMillis >= minimumDispatchIntervalMillis
    }
}

internal const val DEFAULT_CAMERA_MOVE_DISPATCH_INTERVAL_MILLIS = 64L
