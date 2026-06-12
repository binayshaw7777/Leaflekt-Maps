package com.binayshaw7777.leaflekt

actual fun deviceTotalRamMb(): Long = try {
    val reader = java.io.BufferedReader(java.io.FileReader("/proc/meminfo"))
    val line = reader.readLine()
    reader.close()
    val kb = line.trim().split("\\s+".toRegex())[1].toLong()
    kb / 1024L
} catch (_: Exception) {
    2048L
}
