package com.binayshaw7777.leaflekt.library

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Public image model for rendering a marker with a custom bitmap.
 *
 * This mirrors the common "custom marker icon" use case from Google Maps style APIs while
 * remaining explicit about size and anchor behavior.
 *
 * ### Usage Example:
 * ```kotlin
 * val bikeBitmap = BitmapFactory.decodeResource(
 *     LocalContext.current.resources,
 *     R.drawable.ic_bike_marker
 * )
 *
 * LeaflektMarker(
 *     position = LeaflektLatLng(22.5726, 88.3639),
 *     icon = LeaflektMarkerIcon(
 *         bitmap = bikeBitmap,
 *         widthPx = 72,
 *         heightPx = 72,
 *         anchorFractionX = 0.5f,
 *         anchorFractionY = 1f
 *     )
 * )
 * ```
 */
data class LeaflektMarkerIcon(
    /** Bitmap that will be rendered for the marker. */
    val bitmap: Bitmap,

    /** Target render width in pixels. Defaults to [bitmap] width. */
    val widthPx: Int = bitmap.width,

    /** Target render height in pixels. Defaults to [bitmap] height. */
    val heightPx: Int = bitmap.height,

    /** Horizontal anchor fraction where `0f` is start and `1f` is end. */
    val anchorFractionX: Float = 0.5f,

    /** Vertical anchor fraction where `0f` is top and `1f` is bottom. */
    val anchorFractionY: Float = 1.0f
)

internal fun LeaflektMarkerIcon.toMarkerIconInfo(): LeaflektMarkerIconInfo {
    return buildMarkerIconInfo(
        bitmap = bitmap,
        widthPx = widthPx,
        heightPx = heightPx,
        anchorFractionX = anchorFractionX,
        anchorFractionY = anchorFractionY
    )
}

internal fun buildMarkerIconInfo(
    bitmap: Bitmap,
    widthPx: Int,
    heightPx: Int,
    anchorFractionX: Float,
    anchorFractionY: Float
): LeaflektMarkerIconInfo {
    val resolvedWidth = widthPx.coerceAtLeast(1)
    val resolvedHeight = heightPx.coerceAtLeast(1)
    val bitmapBytes = ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.toByteArray()
    }
    val dataUrl = "data:image/png;base64," + Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)

    return LeaflektMarkerIconInfo(
        dataUrl = dataUrl,
        widthPx = resolvedWidth,
        heightPx = resolvedHeight,
        anchorFractionX = anchorFractionX.coerceIn(0f, 1f),
        anchorFractionY = anchorFractionY.coerceIn(0f, 1f)
    )
}
