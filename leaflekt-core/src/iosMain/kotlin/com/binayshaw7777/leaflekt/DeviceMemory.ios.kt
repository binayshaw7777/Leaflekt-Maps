package com.binayshaw7777.leaflekt

import platform.Foundation.NSProcessInfo

actual fun deviceTotalRamMb(): Long =
    (NSProcessInfo.processInfo.physicalMemory / 1_048_576UL).toLong()
