package com.binayshaw7777.leaflekt

sealed interface LeaflektStrokePattern {
    data class Dash(val length: Float) : LeaflektStrokePattern
    data class Gap(val length: Float) : LeaflektStrokePattern
    data class Dot(val radius: Float = 1f) : LeaflektStrokePattern
}
