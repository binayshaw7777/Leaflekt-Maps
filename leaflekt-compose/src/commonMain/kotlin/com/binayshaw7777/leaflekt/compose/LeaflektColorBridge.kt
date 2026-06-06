package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.LeaflektColor

fun Color.toLeaflektColor(): LeaflektColor =
    LeaflektColor(red, green, blue, alpha)

fun LeaflektColor.toComposeColor(): Color =
    Color(red, green, blue, alpha)
