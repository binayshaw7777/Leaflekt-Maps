package com.binayshaw7777.leaflekt

expect fun deviceTotalRamMb(): Long

internal fun defaultTileBufferSize(): Int = when {
    deviceTotalRamMb() < 2048 -> 4
    deviceTotalRamMb() < 4096 -> 8
    else                      -> 12
}
