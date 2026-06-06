package com.binayshaw7777.leaflekt.compose

internal expect class PlatformLocationProvider {
    fun requestLocationUpdates(onLocation: (LeaflektLatLng) -> Unit)
    fun removeLocationUpdates()
}
