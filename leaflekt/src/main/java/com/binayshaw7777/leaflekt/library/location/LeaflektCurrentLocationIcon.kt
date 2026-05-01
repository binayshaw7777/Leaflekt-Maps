package com.binayshaw7777.leaflekt.library.location

import android.graphics.Bitmap

/**
 * Optional image model for rendering the current location marker with a custom bitmap.
 *
 * When omitted, LeafleKT renders its default blue-dot current location indicator.
 */
data class LeaflektCurrentLocationIcon(
    val bitmap: Bitmap,
    val widthPx: Int = bitmap.width,
    val heightPx: Int = bitmap.height,
    val anchorFractionX: Float = 0.5f,
    val anchorFractionY: Float = 0.5f
)
