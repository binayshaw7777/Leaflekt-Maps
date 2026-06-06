package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.ComposableTargetMarker

@ComposableTargetMarker(description = "Leaflekt Map Composable")
@Target(
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.TYPE,
    AnnotationTarget.TYPE_PARAMETER,
)
@Retention(AnnotationRetention.BINARY)
annotation class LeaflektMapComposable
