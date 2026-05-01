package com.binayshaw7777.leaflekt.library.controller

import com.binayshaw7777.leaflekt.library.camera.LatLng
import org.junit.Assert.assertTrue
import org.junit.Test

class MapControllerTest {

    @Test
    fun releaseMapSessionClearsProjectionState() {
        val controller = MapController()

        controller.registerOverlayPoint(LatLng(22.5726, 88.3639))
        controller.onProjectionChanged(
            """[{"lat":22.5726,"lng":88.3639,"xFraction":0.4,"yFraction":0.6}]"""
        )

        controller.releaseMapSession()

        assertTrue(controller.projectionState.isEmpty())
    }
}
