package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.toLeaflektCurrentLocationIcon(
    widthPx: Int = this.width,
    heightPx: Int = this.height,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 0.5f
): LeaflektCurrentLocationIcon {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return LeaflektCurrentLocationIcon(
        pngBytes = stream.toByteArray(),
        widthPx = widthPx,
        heightPx = heightPx,
        anchorFractionX = anchorFractionX,
        anchorFractionY = anchorFractionY
    )
}
