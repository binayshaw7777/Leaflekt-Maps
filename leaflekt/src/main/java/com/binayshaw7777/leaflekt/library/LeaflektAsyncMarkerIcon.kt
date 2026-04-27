package com.binayshaw7777.leaflekt.library

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A [State] holder that exposes an asynchronously loaded [LeaflektMarkerIcon].
 *
 * This API mirrors Coil's async image loading pattern, enabling marker icons from URLs,
 * resources, files, or any Coil-supported model. The [value] is `null` while loading or
 * on error, and becomes non-null when the image has been successfully decoded.
 *
 * The state is remembered across recompositions and automatically updates when the image
 * load completes. Loading happens on a background thread via [Dispatchers.IO].
 *
 * ### Usage
 * ```kotlin
 * // Load from URL
 * val bikeIcon = rememberLeaflektAsyncMarkerIcon(
 *     model = "https://example.com/bike.png",
 *     widthPx = 64,
 *     heightPx = 64,
 *     anchorFractionX = 0.5f,
 *     anchorFractionY = 0.5f
 * )
 *
 * // Use in marker — icon is null during load, non-null when ready
 * LeaflektMarker(
 *     position = LeaflektLatLng(22.5726, 88.3639),
 *     icon = bikeIcon.value
 * )
 * ```
 *
 * ### Supported Model Types
 * - `String` — HTTP/HTTPS URL or absolute file path
 * - `@DrawableRes Int` — Android drawable resource ID
 * - `Bitmap` — in-memory bitmap
 * - `File` — local file
 * - `Uri` — content URI
 * - Any custom Coil `Model` (requires providing a custom [ImageLoader])
 *
 * ### Dependency
 * Requires Coil Compose in your app:
 * ```kotlin
 * implementation("io.coil-kt:coil-compose:2.7.0")
 * ```
 *
 * @param model The image model to load (URL, resource ID, Bitmap, File, Uri, etc.)
 * @param widthPx Optional target width in pixels. If null, uses the bitmap's natural width.
 * @param heightPx Optional target height in pixels. If null, uses the bitmap's natural height.
 * @param anchorFractionX Horizontal anchor fraction (0–1). 0 = left edge, 0.5 = center, 1 = right edge. Default: `0.5f`.
 * @param anchorFractionY Vertical anchor fraction (0–1). 0 = top edge, 0.5 = center, 1 = bottom edge. Default: `1f` (bottom, following Google Maps convention).
 * @return A [State] containing the loaded [LeaflektMarkerIcon] or `null` if loading/error.
 */
@Composable
fun rememberLeaflektAsyncMarkerIcon(
    model: Any?,
    widthPx: Int? = null,
    heightPx: Int? = null,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 1f
): State<LeaflektMarkerIcon?> {
    val context: Context = LocalContext.current

    return produceState(initialValue = null, model, widthPx, heightPx, anchorFractionX, anchorFractionY) {
        if (model == null) {
            value = null
            return@produceState
        }

        val imageLoader: ImageLoader = Coil.imageLoader(context)

        val result = withContext(Dispatchers.IO) {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(model)
                    .build()
            )
        }

        value = when (result) {
            is SuccessResult -> {
                val bitmap: Bitmap = when (val drawable: Drawable = result.drawable) {
                    is BitmapDrawable -> drawable.bitmap
                    else -> drawable.toBitmap()
                }
                LeaflektMarkerIcon(
                    bitmap = bitmap,
                    widthPx = widthPx ?: bitmap.width,
                    heightPx = heightPx ?: bitmap.height,
                    anchorFractionX = anchorFractionX,
                    anchorFractionY = anchorFractionY
                )
            }
            else -> null
        }
    }
}
