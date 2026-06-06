package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color

internal expect fun Color.toCssRgba(alpha: Float = this.alpha): String
