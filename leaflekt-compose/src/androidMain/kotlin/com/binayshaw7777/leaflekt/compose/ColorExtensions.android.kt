package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

internal actual fun Color.toCssRgba(alpha: Float): String {
    val argb = copy(alpha = alpha.coerceIn(0f, 1f)).toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    val a = ((argb ushr 24) and 0xFF) / 255f
    return "rgba($r,$g,$b,${"%.3f".format(java.util.Locale.US, a)})"
}
