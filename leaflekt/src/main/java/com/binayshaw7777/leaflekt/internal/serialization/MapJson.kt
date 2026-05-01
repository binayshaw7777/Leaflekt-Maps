package com.binayshaw7777.leaflekt.internal.serialization

import com.binayshaw7777.leaflekt.library.camera.LatLng
import com.binayshaw7777.leaflekt.library.cluster.MarkerClusterOptions

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object MapViewJson {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun <T> decodeFromString(deserializer: kotlinx.serialization.KSerializer<T>, string: String): T {
        return json.decodeFromString(deserializer, string)
    }

    // Overload for convenience if possible or use reified in a wrapper
    inline fun <reified T> decodeFromString(string: String): T {
        return json.decodeFromString(string)
    }

    fun encodeString(value: String): String {
        return json.encodeToString(value)
    }

    fun encodeNullableString(value: String?): String {
        return value?.let(::encodeString) ?: "null"
    }

    fun encodeLatLng(point: LatLng): String {
        return """{"latitude":${point.latitude},"longitude":${point.longitude}}"""
    }

    fun encodeLatLngList(points: List<LatLng>): String {
        return points.joinToString(prefix = "[", postfix = "]") { encodeLatLng(it) }
    }

    fun encodeLatLngHoles(holes: List<List<LatLng>>): String {
        return holes.joinToString(prefix = "[", postfix = "]") { encodeLatLngList(it) }
    }

    fun encodeMarkerClusterOptions(options: MarkerClusterOptions): String {
        return """
            {
                "showCoverageOnHover": ${options.showCoverageOnHover},
                "zoomToBoundsOnClick": ${options.zoomToBoundsOnClick},
                "spiderfyOnMaxZoom": ${options.spiderfyOnMaxZoom},
                "disableClusteringAtZoom": ${options.disableClusteringAtZoom ?: "null"},
                "maxClusterRadius": ${options.maxClusterRadius}
            }
        """.trimIndent()
    }
}

