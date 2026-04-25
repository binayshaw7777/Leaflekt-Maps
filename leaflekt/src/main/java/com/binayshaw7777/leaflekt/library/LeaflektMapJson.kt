package com.binayshaw7777.leaflekt.library

import kotlinx.serialization.json.Json

internal object LeaflektMapJson {
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
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        return "\"$escaped\""
    }

    fun encodeNullableString(value: String?): String {
        return value?.let(::encodeString) ?: "null"
    }

    fun encodeLatLng(point: LeaflektLatLng): String {
        return """{"latitude":${point.latitude},"longitude":${point.longitude}}"""
    }

    fun encodeLatLngList(points: List<LeaflektLatLng>): String {
        return points.joinToString(prefix = "[", postfix = "]") { encodeLatLng(it) }
    }

    fun encodeLatLngHoles(holes: List<List<LeaflektLatLng>>): String {
        return holes.joinToString(prefix = "[", postfix = "]") { encodeLatLngList(it) }
    }
}
