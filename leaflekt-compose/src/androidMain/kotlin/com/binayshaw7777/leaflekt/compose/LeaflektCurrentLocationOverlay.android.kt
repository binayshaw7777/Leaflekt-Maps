package com.binayshaw7777.leaflekt.compose

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
@LeaflektMapComposable
internal actual fun LeaflektCurrentLocationOverlay(uiSettings: LeaflektMapUiSettings) {
    if (!uiSettings.showCurrentLocation) return

    val context = LocalContext.current
    val controller = LocalLeaflektController.current ?: return

    val locationProvider = remember(context) { PlatformLocationProvider(context) }
    val customMarkerIcon = remember(uiSettings.currentLocationIcon) {
        uiSettings.currentLocationIcon?.toMarkerIconInfo()
    }

    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var currentLocation by remember { mutableStateOf<LeaflektResolvedLocation?>(null) }
    var pendingCenterZoom by remember { mutableStateOf<Double?>(null) }

    val pulseTransition = rememberInfiniteTransition(label = "cmpLocationPulse")
    val pulseRadiusScale by pulseTransition.animateFloat(
        initialValue = 1f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "cmpLocationPulseRadius"
    )
    val pulseOpacity by pulseTransition.animateFloat(
        initialValue = 0.22f, targetValue = 0.04f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "cmpLocationPulseOpacity"
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        hasLocationPermission = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (!hasLocationPermission) currentLocation = null
    }

    LaunchedEffect(uiSettings.showCurrentLocation) {
        hasLocationPermission = context.hasLocationPermission()
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    DisposableEffect(controller, currentLocation, hasLocationPermission) {
        controller.registerCurrentLocationCenteringAction { zoom ->
            val loc = currentLocation
            if (loc != null) {
                controller.moveCamera(loc.position.latitude, loc.position.longitude, zoom)
            } else {
                pendingCenterZoom = zoom
                if (!hasLocationPermission) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        }
        onDispose { controller.unregisterCurrentLocationCenteringAction() }
    }

    DisposableEffect(uiSettings.showCurrentLocation, hasLocationPermission) {
        if (!uiSettings.showCurrentLocation || !hasLocationPermission) {
            currentLocation = null
            return@DisposableEffect onDispose {}
        }
        locationProvider.requestLocationUpdates { latLng ->
            currentLocation = LeaflektResolvedLocation(latLng, 50.0)
        }
        onDispose { locationProvider.removeLocationUpdates() }
    }

    LaunchedEffect(currentLocation, pendingCenterZoom) {
        val zoom = pendingCenterZoom ?: return@LaunchedEffect
        val loc = currentLocation ?: return@LaunchedEffect
        controller.moveCamera(loc.position.latitude, loc.position.longitude, zoom)
        pendingCenterZoom = null
    }

    currentLocation?.let { loc ->
        LeaflektCircle(
            center = loc.position, radiusMeters = loc.accuracyMeters,
            fillColor = CurrentLocationBlue, strokeColor = CurrentLocationBlue,
            strokeWidth = 1f, fillOpacity = 0.12f, strokeOpacity = 0.22f,
            zIndex = 85f, id = LOCATION_ACCURACY_ID
        )
        LeaflektCircle(
            center = loc.position, radiusMeters = 18.0 * pulseRadiusScale,
            fillColor = CurrentLocationBlue, strokeColor = CurrentLocationBlue,
            strokeWidth = 1f, fillOpacity = pulseOpacity, strokeOpacity = 0f,
            zIndex = 90f, id = LOCATION_PULSE_ID
        )
        if (customMarkerIcon == null) {
            LeaflektCircle(
                center = loc.position, radiusMeters = 12.0,
                fillColor = CurrentLocationBlue, strokeColor = Color.White,
                strokeWidth = 3f, fillOpacity = 1f, strokeOpacity = 1f,
                zIndex = 100f, id = LOCATION_DOT_ID
            )
        } else {
            DisposableEffect(controller) {
                controller.addMarker(
                    LeaflektMarkerInfo(
                        id = LOCATION_MARKER_ID,
                        lat = loc.position.latitude,
                        lng = loc.position.longitude,
                        visible = true,
                        alpha = 1f,
                        icon = customMarkerIcon
                    )
                )
                onDispose { controller.removeMarker(LOCATION_MARKER_ID) }
            }
            LaunchedEffect(loc.position) {
                controller.updateMarker(
                    LeaflektMarkerInfo(
                        id = LOCATION_MARKER_ID,
                        lat = loc.position.latitude,
                        lng = loc.position.longitude,
                        visible = true,
                        alpha = 1f,
                        icon = customMarkerIcon
                    )
                )
            }
        }
    }
}

private fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
}

private fun LeaflektCurrentLocationIcon.toMarkerIconInfo(): LeaflektMarkerIconInfo {
    val dataUrl = "data:image/png;base64," + Base64.encodeToString(pngBytes, Base64.NO_WRAP)
    return LeaflektMarkerIconInfo(
        dataUrl = dataUrl,
        widthPx = widthPx.coerceAtLeast(1),
        heightPx = heightPx.coerceAtLeast(1),
        anchorFractionX = anchorFractionX.coerceIn(0f, 1f),
        anchorFractionY = anchorFractionY.coerceIn(0f, 1f)
    )
}

private data class LeaflektResolvedLocation(val position: LeaflektLatLng, val accuracyMeters: Double)
private val CurrentLocationBlue = Color(0xFF1A73E8)
private const val LOCATION_ACCURACY_ID = "leaflekt-cmp-location-accuracy"
private const val LOCATION_PULSE_ID = "leaflekt-cmp-location-pulse"
private const val LOCATION_DOT_ID = "leaflekt-cmp-location-dot"
private const val LOCATION_MARKER_ID = "leaflekt-cmp-location-marker"
