package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.darwin.NSObject

internal actual class PlatformLocationProvider {
    private val manager = CLLocationManager()
    private var delegate: LocationDelegate? = null

    actual fun requestLocationUpdates(onLocation: (LeaflektLatLng) -> Unit) {
        val d = LocationDelegate(onLocation)
        delegate = d
        manager.delegate = d
        manager.desiredAccuracy = kCLLocationAccuracyBest
        manager.requestWhenInUseAuthorization()
        manager.startUpdatingLocation()
    }

    actual fun removeLocationUpdates() {
        manager.stopUpdatingLocation()
        manager.delegate = null
        delegate = null
    }

    private class LocationDelegate(
        private val onLocation: (LeaflektLatLng) -> Unit
    ) : NSObject(), CLLocationManagerDelegateProtocol {
        @OptIn(ExperimentalForeignApi::class)
        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>
        ) {
            val loc = didUpdateLocations.lastOrNull() as? CLLocation ?: return
            onLocation(
                LeaflektLatLng(
                    latitude = loc.coordinate.useContents { latitude },
                    longitude = loc.coordinate.useContents { longitude }
                )
            )
        }
    }
}
