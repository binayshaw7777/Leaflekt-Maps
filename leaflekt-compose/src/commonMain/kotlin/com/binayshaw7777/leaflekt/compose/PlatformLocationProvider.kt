package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

internal expect class PlatformLocationProvider {
    fun requestLocationUpdates(onLocation: (LeaflektLatLng) -> Unit)
    fun removeLocationUpdates()
}
