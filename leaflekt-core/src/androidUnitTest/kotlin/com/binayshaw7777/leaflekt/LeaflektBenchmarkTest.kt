package com.binayshaw7777.leaflekt

import kotlin.concurrent.Volatile
import kotlin.test.Test
import kotlin.time.measureTime

/**
 * Micro-benchmark suite for LeaflektMapJson and LeaflektScriptBuilder hot paths.
 *
 * Run via:   ./gradlew :leaflekt-core:testDebugUnitTest --rerun-tasks --info 2>&1 | grep "BENCH"
 *
 * Designed to compile and run on BOTH develop and feature branches for 1:1 performance comparisons.
 */
class LeaflektBenchmarkTest {

    companion object {
        private const val WARMUP = 1_000
        private const val PASSES = 5
        private const val ITERATIONS = 5_000

        @Volatile
        private var blackhole: Any? = null

        private fun <T> bench(label: String, block: () -> T): BenchResult {
            // Warmup phase
            repeat(WARMUP) { blackhole = block() }

            // Measurement phase over multiple passes (takes min ns/op to eliminate GC/context-switch noise)
            var minNsPerOp = Long.MAX_VALUE
            repeat(PASSES) {
                var last: Any? = null
                val elapsed = measureTime {
                    repeat(ITERATIONS) {
                        last = block()
                    }
                }
                blackhole = last
                val nsPerOp = elapsed.inWholeNanoseconds / ITERATIONS.toLong()
                if (nsPerOp < minNsPerOp) minNsPerOp = nsPerOp
            }
            return BenchResult(label, ITERATIONS, minNsPerOp)
        }

        private fun benchOptional(
            label: String,
            targetObject: Any,
            methodName: String,
            vararg args: Any?
        ): BenchResult {
            val method = try {
                targetObject::class.java.methods.firstOrNull { m ->
                    m.name == methodName && m.parameterCount == args.size
                }
            } catch (e: Throwable) {
                null
            }

            if (method == null) {
                return BenchResult(label, ITERATIONS, -1L)
            }

            repeat(WARMUP) {
                blackhole = method.invoke(targetObject, *args)
            }

            var minNsPerOp = Long.MAX_VALUE
            repeat(PASSES) {
                var last: Any? = null
                val elapsed = measureTime {
                    repeat(ITERATIONS) {
                        last = method.invoke(targetObject, *args)
                    }
                }
                blackhole = last
                val nsPerOp = elapsed.inWholeNanoseconds / ITERATIONS.toLong()
                if (nsPerOp < minNsPerOp) minNsPerOp = nsPerOp
            }
            return BenchResult(label, ITERATIONS, minNsPerOp)
        }

        private data class BenchResult(val label: String, val iters: Int, val nsPerOp: Long) {
            override fun toString(): String {
                val nsString = if (nsPerOp >= 0) "$nsPerOp ns/op" else "N/A (Not on branch)"
                return "BENCH\t${label.padEnd(65)}\t$iters iters\t$nsString"
            }
        }

        private fun printHeader() {
            println("\n" + "=".repeat(110))
            println("BENCH\t${"Label".padEnd(65)}\tIterations\tns/op")
            println("=".repeat(110))
        }

        private fun printFooter() {
            println("=".repeat(110) + "\n")
        }
    }

    // ── LeaflektMapJson ──────────────────────────────────────────────────────

    @Test
    fun benchMapJson() {
        val shortString = "hello"
        val longString = "A".repeat(500)
        val specialString = "te\\st\nwith\t\"quotes\" and\rnewlines\b "
        val singlePoint = LeaflektLatLng(22.5726, 88.3639)
        val points10 = List(10) { i -> LeaflektLatLng(i.toDouble(), i.toDouble() + 0.1) }
        val points100 = List(100) { i -> LeaflektLatLng(i.toDouble() % 90, i.toDouble() % 180) }
        val points1000 = List(1000) { i -> LeaflektLatLng(i.toDouble() % 90, i.toDouble() % 180) }
        val holes = listOf(points10, points10)

        val results = listOf(
            bench("MapJson.encodeString [short 5 chars]") {
                LeaflektMapJson.encodeString(shortString)
            },
            bench("MapJson.encodeString [long 500 chars]") {
                LeaflektMapJson.encodeString(longString)
            },
            bench("MapJson.encodeString [special chars, escaping heavy]") {
                LeaflektMapJson.encodeString(specialString)
            },
            benchOptional("MapJson.escapeJsString [short]", LeaflektMapJson, "escapeJsString", shortString),
            bench("MapJson.encodeNullableString [null]") {
                LeaflektMapJson.encodeNullableString(null)
            },
            bench("MapJson.encodeNullableString [value]") {
                LeaflektMapJson.encodeNullableString(shortString)
            },
            bench("MapJson.encodeLatLng [single point]") {
                LeaflektMapJson.encodeLatLng(singlePoint)
            },
            bench("MapJson.encodeLatLngList [10 points]") {
                LeaflektMapJson.encodeLatLngList(points10)
            },
            bench("MapJson.encodeLatLngList [100 points]") {
                LeaflektMapJson.encodeLatLngList(points100)
            },
            bench("MapJson.encodeLatLngList [1000 points]") {
                LeaflektMapJson.encodeLatLngList(points1000)
            },
            bench("MapJson.encodeLatLngHoles [2 holes × 10 pts each]") {
                LeaflektMapJson.encodeLatLngHoles(holes)
            },
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }

    // ── LeaflektScriptBuilder ────────────────────────────────────────────────

    @Test
    fun benchScriptBuilderCamera() {
        val results = listOf(
            bench("ScriptBuilder.moveCameraScript") {
                LeaflektScriptBuilder.moveCameraScript(22.5726, 88.3639, 12.0)
            },
            bench("ScriptBuilder.animateCameraScript [duration=500]") {
                LeaflektScriptBuilder.animateCameraScript(22.5726, 88.3639, 12.0, 500)
            },
            bench("ScriptBuilder.setZoomBoundsScript") {
                LeaflektScriptBuilder.setZoomBoundsScript(3.0, 18.0)
            },
            bench("ScriptBuilder.setZoomControlsEnabledScript") {
                LeaflektScriptBuilder.setZoomControlsEnabledScript(true)
            },
            bench("ScriptBuilder.setScrollGesturesEnabledScript") {
                LeaflektScriptBuilder.setScrollGesturesEnabledScript(true)
            },
            bench("ScriptBuilder.setZoomGesturesEnabledScript") {
                LeaflektScriptBuilder.setZoomGesturesEnabledScript(false)
            },
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }

    @Test
    fun benchScriptBuilderMarkers() {
        val marker1 = listOf(
            LeaflektMarkerInfo(id = "m1", lat = 22.5726, lng = 88.3639, title = "Marker 1")
        )
        val markers10 = List(10) { i ->
            LeaflektMarkerInfo(id = "m$i", lat = 20.0 + i * 0.1, lng = 85.0 + i * 0.1, title = "Title $i")
        }
        val markers100 = List(100) { i ->
            LeaflektMarkerInfo(id = "marker_$i", lat = (i % 90).toDouble(), lng = (i % 180).toDouble(), title = "T$i", snippet = "S$i")
        }
        val singleId = "marker_id_001"
        val ids10 = List(10) { "id_$it" }

        val results = listOf(
            bench("ScriptBuilder.addMarkersScript [1 marker, no icon]") {
                LeaflektScriptBuilder.addMarkersScript(marker1)
            },
            bench("ScriptBuilder.addMarkersScript [10 markers, no icon]") {
                LeaflektScriptBuilder.addMarkersScript(markers10)
            },
            bench("ScriptBuilder.addMarkersScript [100 markers, no icon]") {
                LeaflektScriptBuilder.addMarkersScript(markers100)
            },
            bench("ScriptBuilder.updateMarkerScript [single]") {
                LeaflektScriptBuilder.updateMarkerScript(marker1.first())
            },
            bench("ScriptBuilder.removeMarkerScript [plain id]") {
                LeaflektScriptBuilder.removeMarkerScript(singleId)
            },
            bench("ScriptBuilder.removeMarkerScript [id with special chars]") {
                LeaflektScriptBuilder.removeMarkerScript("id\"with'special\\chars\nnewline")
            },
            benchOptional("ScriptBuilder.removeMarkersScript [10 ids]", LeaflektScriptBuilder, "removeMarkersScript", ids10),
            bench("ScriptBuilder.clearMarkersScript") {
                LeaflektScriptBuilder.clearMarkersScript()
            },
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }

    @Test
    fun benchScriptBuilderShapes() {
        val sw = LeaflektLatLng(10.0, 70.0)
        val ne = LeaflektLatLng(35.0, 90.0)

        val polyline = LeaflektPolylineInfo(
            id = "pl1",
            points = List(50) { i -> LeaflektLatLng(i.toDouble() % 90, i.toDouble() % 180) }
        )
        val polylineLarge = LeaflektPolylineInfo(
            id = "pl_large",
            points = List(500) { i -> LeaflektLatLng(i.toDouble() % 90, i.toDouble() % 180) }
        )
        val polygon = LeaflektPolygonInfo(
            id = "poly1",
            points = List(20) { i -> LeaflektLatLng(i.toDouble() % 89, i.toDouble() % 179) }
        )
        val circle = LeaflektCircleInfo(
            id = "c1",
            center = LeaflektLatLng(22.5726, 88.3639),
            radiusMeters = 1000.0
        )

        val results = listOf(
            benchOptional("ScriptBuilder.fitBoundsScript", LeaflektScriptBuilder, "fitBoundsScript", sw, ne, 32),
            bench("ScriptBuilder.addPolylineScript [50 pts]") {
                LeaflektScriptBuilder.addPolylineScript(polyline)
            },
            bench("ScriptBuilder.addPolylineScript [500 pts]") {
                LeaflektScriptBuilder.addPolylineScript(polylineLarge)
            },
            bench("ScriptBuilder.updatePolylineScript [50 pts]") {
                LeaflektScriptBuilder.updatePolylineScript(polyline)
            },
            bench("ScriptBuilder.removePolylineScript") {
                LeaflektScriptBuilder.removePolylineScript("pl1")
            },
            bench("ScriptBuilder.addPolygonScript [20 pts]") {
                LeaflektScriptBuilder.addPolygonScript(polygon)
            },
            bench("ScriptBuilder.updatePolygonScript [20 pts]") {
                LeaflektScriptBuilder.updatePolygonScript(polygon)
            },
            bench("ScriptBuilder.removePolygonScript") {
                LeaflektScriptBuilder.removePolygonScript("poly1")
            },
            bench("ScriptBuilder.addCircleScript") {
                LeaflektScriptBuilder.addCircleScript(circle)
            },
            bench("ScriptBuilder.updateCircleScript") {
                LeaflektScriptBuilder.updateCircleScript(circle)
            },
            bench("ScriptBuilder.removeCircleScript") {
                LeaflektScriptBuilder.removeCircleScript("c1")
            },
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }

    @Test
    fun benchScriptBuilderInit() {
        val results = listOf(
            bench("ScriptBuilder.initMapScript") {
                LeaflektScriptBuilder.initMapScript(22.5726, 88.3639, 12.0)
            },
            benchOptional(
                "ScriptBuilder.initMapBatchScript [OSM, India overlay]",
                LeaflektScriptBuilder,
                "initMapBatchScript",
                22.5726, 88.3639, 12.0, true, LeaflektMapStyle.OpenStreetMap, LeaflektGeoJsonOverlay.India, 8
            ),
            benchOptional(
                "ScriptBuilder.initMapBatchScript [OSM, No overlay]",
                LeaflektScriptBuilder,
                "initMapBatchScript",
                22.5726, 88.3639, 12.0, false, LeaflektMapStyle.OpenStreetMap, LeaflektGeoJsonOverlay.None, 4
            ),
            bench("ScriptBuilder.setMapStyleScript [OSM]") {
                LeaflektScriptBuilder.setMapStyleScript(LeaflektMapStyle.OpenStreetMap)
            },
            bench("ScriptBuilder.setGeoJsonOverlayScript [India]") {
                LeaflektScriptBuilder.setGeoJsonOverlayScript(LeaflektGeoJsonOverlay.India)
            },
            bench("ScriptBuilder.setGeoJsonOverlayScript [None]") {
                LeaflektScriptBuilder.setGeoJsonOverlayScript(LeaflektGeoJsonOverlay.None)
            },
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }

    @Test
    fun benchScriptBuilderClusters() {
        val markers50 = List(50) { i ->
            LeaflektMarkerInfo(id = "cm$i", lat = (i % 90).toDouble(), lng = (i % 180).toDouble())
        }

        val results = listOf(
            bench("ScriptBuilder.createClusterGroupScript") {
                LeaflektScriptBuilder.createClusterGroupScript("group1", 80)
            },
            bench("ScriptBuilder.addMarkersToClusterScript [50 markers]") {
                LeaflektScriptBuilder.addMarkersToClusterScript("group1", markers50)
            },
            bench("ScriptBuilder.removeClusterGroupScript") {
                LeaflektScriptBuilder.removeClusterGroupScript("group1")
            },
            benchOptional("ScriptBuilder.clearMapScript", LeaflektScriptBuilder, "clearMapScript"),
        )

        printHeader()
        results.forEach { println(it) }
        printFooter()
    }
}
