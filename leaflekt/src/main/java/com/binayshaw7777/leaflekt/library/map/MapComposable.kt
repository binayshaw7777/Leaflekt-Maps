package com.binayshaw7777.leaflekt.library.map

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
annotation class MapComposable

