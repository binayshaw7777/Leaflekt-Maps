/*
 * Copyright 2026 Binay Shaw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binayshaw7777.leaflekt.library

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [LeaflektScriptBuilder].
 */
class LeaflektScriptBuilderTest {

    @Test
    fun testAddMarkersScriptGeneration() {
        val markers = listOf(
            LeaflektMarkerInfo(id = "1", lat = 22.5726, lng = 88.3639, title = "Kolkata"),
            LeaflektMarkerInfo(id = "2", lat = 12.9716, lng = 77.5946, title = "Bengaluru")
        )

        val script = LeaflektScriptBuilder.addMarkersScript(markers)

        assertTrue(script.contains("window.LeaflektBridge.addMarkers"))
        assertTrue(script.contains("\"lat\": 22.5726"))
        assertTrue(script.contains("\"lng\": 88.3639"))
        assertTrue(script.contains("\"title\": \"Kolkata\""))
        assertTrue(script.contains("\"id\": \"1\""))
    }

    @Test
    fun testMoveCameraScriptGeneration() {
        val script = LeaflektScriptBuilder.moveCameraScript(22.5, 88.3, 10.0, 0.0)
        assertTrue(script.contains("window.LeaflektBridge.moveCamera(22.5,88.3,10.0,0.0)"))
    }

    @Test
    fun testAddMarkerScriptGenerationWithCustomIcon() {
        val script = LeaflektScriptBuilder.addMarkersScript(
            listOf(
                LeaflektMarkerInfo(
                    id = "current-location",
                    lat = 22.5726,
                    lng = 88.3639,
                    rotationDegrees = 42f,
                    icon = LeaflektMarkerIconInfo(
                        dataUrl = "data:image/png;base64,abc123",
                        widthPx = 32,
                        heightPx = 32,
                        anchorFractionX = 0.5f,
                        anchorFractionY = 0.5f
                    )
                )
            )
        )

        assertTrue(script.contains("\"icon\": {"))
        assertTrue(script.contains("\"dataUrl\": \"data:image/png;base64,abc123\""))
        assertTrue(script.contains("\"widthPx\": 32"))
        assertTrue(script.contains("\"heightPx\": 32"))
        assertTrue(script.contains("\"rotationDegrees\": 42.0"))
    }

    @Test
    fun testAddPolylineScriptGeneration() {
        val script = LeaflektScriptBuilder.addPolylineScript(
            LeaflektPolylineInfo(
                id = "poly-1",
                points = listOf(
                    LeaflektLatLng(22.5726, 88.3639),
                    LeaflektLatLng(22.5826, 88.3739)
                ),
                color = Color.Red,
                width = 6f,
                zIndex = 3f,
                alpha = 0.5f
            )
        )

        assertTrue(script.contains("window.LeaflektBridge.addPolyline"))
        assertTrue(script.contains("\"id\": \"poly-1\""))
        assertTrue(script.contains("\"points\": [{\"latitude\":22.5726,\"longitude\":88.3639}"))
        assertTrue(script.contains("\"color\": \"rgba(255,0,0,0.502)\""))
        assertTrue(script.contains("\"zIndex\": 3.0"))
        assertTrue(script.contains("\"geodesic\": false"))
    }

    @Test
    fun testAddPolygonScriptGeneration() {
        val script = LeaflektScriptBuilder.addPolygonScript(
            LeaflektPolygonInfo(
                id = "polygon-1",
                points = listOf(
                    LeaflektLatLng(22.56, 88.34),
                    LeaflektLatLng(22.59, 88.35),
                    LeaflektLatLng(22.58, 88.39)
                ),
                holes = listOf(
                    listOf(
                        LeaflektLatLng(22.571, 88.351),
                        LeaflektLatLng(22.575, 88.356),
                        LeaflektLatLng(22.57, 88.362)
                    )
                ),
                fillColor = Color(0xFF2A9D8F),
                strokeColor = Color(0xFF264653),
                strokeWidth = 4f,
                fillOpacity = 0.25f,
                strokeOpacity = 0.75f,
                zIndex = 2f
            )
        )

        assertTrue(script.contains("window.LeaflektBridge.addPolygon"))
        assertTrue(script.contains("\"id\": \"polygon-1\""))
        assertTrue(script.contains("\"holes\": [["))
        assertTrue(script.contains("\"fillColor\": \"rgba(42,157,143,0.251)\""))
        assertTrue(script.contains("\"strokeColor\": \"rgba(38,70,83,0.749)\""))
        assertTrue(script.contains("\"zIndex\": 2.0"))
    }

    @Test
    fun testAddCircleScriptGeneration() {
        val script = LeaflektScriptBuilder.addCircleScript(
            LeaflektCircleInfo(
                id = "circle-1",
                center = LeaflektLatLng(22.5726, 88.3639),
                radiusMeters = 1200.0,
                fillColor = Color(0xFFF4A261),
                strokeColor = Color(0xFFE76F51),
                strokeWidth = 3f,
                fillOpacity = 0.2f,
                strokeOpacity = 0.8f,
                zIndex = 4f
            )
        )

        assertTrue(script.contains("window.LeaflektBridge.addCircle"))
        assertTrue(script.contains("\"id\": \"circle-1\""))
        assertTrue(script.contains("\"radiusMeters\": 1200.0"))
        assertTrue(script.contains("\"fillColor\": \"rgba(244,162,97,0.200)\""))
        assertTrue(script.contains("\"strokeColor\": \"rgba(231,111,81,0.800)\""))
        assertTrue(script.contains("\"zIndex\": 4.0"))
    }
}
