package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

internal actual class PlatformLocationProvider(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    actual fun requestLocationUpdates(onLocation: (LeaflektLatLng) -> Unit) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4_000L)
            .setMinUpdateIntervalMillis(2_000L)
            .setWaitForAccurateLocation(false)
            .build()
        val cb = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                onLocation(LeaflektLatLng(latitude = loc.latitude, longitude = loc.longitude))
            }
        }
        locationCallback = cb
        fusedClient.lastLocation.addOnSuccessListener { loc ->
            loc?.let { onLocation(LeaflektLatLng(latitude = it.latitude, longitude = it.longitude)) }
        }
        fusedClient.requestLocationUpdates(request, cb, Looper.getMainLooper())
    }

    actual fun removeLocationUpdates() {
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        locationCallback = null
    }
}
