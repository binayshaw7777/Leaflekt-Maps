package com.binayshaw7777.leaflekt

internal object LeaflektMapJson {
    fun encodeString(value: String): String {
        val sb = StringBuilder("\"")
        for (c in value) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    sb.append("\\u${c.code.toString(16).padStart(4, '0')}")
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append("\"")
        return sb.toString()
    }

    fun escapeJsString(value: String): String {
        val sb = StringBuilder("'")
        for (c in value) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '\'' -> sb.append("\\'")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    sb.append("\\u${c.code.toString(16).padStart(4, '0')}")
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append("'")
        return sb.toString()
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
