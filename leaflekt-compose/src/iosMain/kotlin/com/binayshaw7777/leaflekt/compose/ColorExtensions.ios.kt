package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color

internal actual fun Color.toCssRgba(alpha: Float): String {
    val r = (red * 255).toInt()
    val g = (green * 255).toInt()
    val b = (blue * 255).toInt()
    val a = alpha.coerceIn(0f, 1f)
    val aStr = (kotlin.math.round(a.toDouble() * 1000) / 1000.0).toString()
    return "rgba($r,$g,$b,$aStr)"
}
