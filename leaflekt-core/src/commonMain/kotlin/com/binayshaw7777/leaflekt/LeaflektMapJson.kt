package com.binayshaw7777.leaflekt

internal object LeaflektMapJson {
    fun encodeString(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        return "\"$escaped\""
    }

    fun encodeNullableString(value: String?): String =
        value?.let(::encodeString) ?: "null"

    fun encodeLatLng(point: LeaflektLatLng): String =
        """{"latitude":${point.latitude},"longitude":${point.longitude}}"""

    fun encodeLatLngList(points: List<LeaflektLatLng>): String =
        points.joinToString(prefix = "[", postfix = "]") { encodeLatLng(it) }

    fun encodeLatLngHoles(holes: List<List<LeaflektLatLng>>): String =
        holes.joinToString(prefix = "[", postfix = "]") { encodeLatLngList(it) }
}
