package com.binayshaw7777.leaflekt

internal object LeaflektMapJson {
    fun encodeString(value: String): String {
        val len = value.length
        var needsEscape = false
        for (i in 0 until len) {
            val c = value[i]
            if (c == '\\' || c == '"' || c.code < 0x20) {
                needsEscape = true
                break
            }
        }
        if (!needsEscape) return "\"$value\""
        val sb = StringBuilder(len + 8)
        sb.append('"')
        for (i in 0 until len) {
            val c = value[i]
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    val hex = c.code.toString(16)
                    sb.append("\\u")
                    for (k in 0 until 4 - hex.length) sb.append('0')
                    sb.append(hex)
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append('"')
        return sb.toString()
    }

    fun escapeJsString(value: String): String {
        val len = value.length
        var needsEscape = false
        for (i in 0 until len) {
            val c = value[i]
            if (c == '\\' || c == '\'' || c == '"' || c.code < 0x20) {
                needsEscape = true
                break
            }
        }
        if (!needsEscape) return "'$value'"
        val sb = StringBuilder(len + 8)
        sb.append('\'')
        for (i in 0 until len) {
            val c = value[i]
            when (c) {
                '\\' -> sb.append("\\\\")
                '\'' -> sb.append("\\'")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> if (c.code < 0x20) {
                    val hex = c.code.toString(16)
                    sb.append("\\u")
                    for (k in 0 until 4 - hex.length) sb.append('0')
                    sb.append(hex)
                } else {
                    sb.append(c)
                }
            }
        }
        sb.append('\'')
        return sb.toString()
    }

    fun encodeNullableString(value: String?): String =
        value?.let(::encodeString) ?: "null"

    fun encodeLatLng(point: LeaflektLatLng): String =
        "{\"latitude\":" + point.latitude + ",\"longitude\":" + point.longitude + "}"

    fun encodeLatLngList(points: List<LeaflektLatLng>): String {
        if (points.isEmpty()) return "[]"
        val sb = StringBuilder(points.size * 36)
        sb.append('[')
        for (i in 0 until points.size) {
            if (i > 0) sb.append(',')
            val pt = points[i]
            sb.append("{\"latitude\":").append(pt.latitude).append(",\"longitude\":").append(pt.longitude).append('}')
        }
        sb.append(']')
        return sb.toString()
    }

    fun encodeLatLngHoles(holes: List<List<LeaflektLatLng>>): String {
        if (holes.isEmpty()) return "[]"
        val sb = StringBuilder()
        sb.append('[')
        for (i in 0 until holes.size) {
            if (i > 0) sb.append(',')
            sb.append(encodeLatLngList(holes[i]))
        }
        sb.append(']')
        return sb.toString()
    }
}
