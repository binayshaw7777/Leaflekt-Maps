package com.binayshaw7777.leaflektsampleapp

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
import com.binayshaw7777.leaflekt.library.marker.MarkerIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun rememberSampleRemoteMarkerIcon(
    model: Any?,
    widthPx: Int? = null,
    heightPx: Int? = null,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 1f
): State<MarkerIcon?> {
    val context: Context = LocalContext.current

    return produceState(
        initialValue = null,
        model,
        widthPx,
        heightPx,
        anchorFractionX,
        anchorFractionY
    ) {
        if (model == null) {
            value = null
            return@produceState
        }

        val imageLoader: ImageLoader = Coil.imageLoader(context)
        val imageResult = withContext(Dispatchers.IO) {
            imageLoader.execute(
                ImageRequest.Builder(context)
                    .data(model)
                    .build()
            )
        }

        value = when (imageResult) {
            is SuccessResult -> imageResult.drawable.toMarkerBitmapIcon(
                widthPx = widthPx,
                heightPx = heightPx,
                anchorFractionX = anchorFractionX,
                anchorFractionY = anchorFractionY
            )

            else -> null
        }
    }
}

private fun Drawable.toMarkerBitmapIcon(
    widthPx: Int?,
    heightPx: Int?,
    anchorFractionX: Float,
    anchorFractionY: Float
): MarkerIcon {
    val markerBitmap = when (this) {
        is BitmapDrawable -> bitmap
        else -> toBitmap()
    }

    return MarkerIcon(
        bitmap = markerBitmap,
        widthPx = widthPx ?: markerBitmap.width,
        heightPx = heightPx ?: markerBitmap.height,
        anchorFractionX = anchorFractionX,
        anchorFractionY = anchorFractionY
    )
}

