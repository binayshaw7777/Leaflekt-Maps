package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

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
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
@LeaflektMapComposable
internal actual fun LeaflektCurrentLocationOverlay(uiSettings: LeaflektMapUiSettings) {
    if (!uiSettings.showCurrentLocation) return

    val controller = LocalLeaflektController.current ?: return
    val locationProvider = remember { PlatformLocationProvider() }
    val customMarkerIcon = remember(uiSettings.currentLocationIcon) {
        uiSettings.currentLocationIcon?.toMarkerIconInfo()
    }

    var currentLocation by remember { mutableStateOf<LeaflektLatLng?>(null) }

    val pulseTransition = rememberInfiniteTransition(label = "iosLocationPulse")
    val pulseRadiusScale by pulseTransition.animateFloat(
        initialValue = 1f, targetValue = 1.8f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "iosLocationPulseRadius"
    )
    val pulseOpacity by pulseTransition.animateFloat(
        initialValue = 0.22f, targetValue = 0.04f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "iosLocationPulseOpacity"
    )

    DisposableEffect(Unit) {
        locationProvider.requestLocationUpdates { latLng -> currentLocation = latLng }
        onDispose { locationProvider.removeLocationUpdates() }
    }

    DisposableEffect(controller) {
        controller.registerCurrentLocationCenteringAction { zoom ->
            currentLocation?.let { controller.moveCamera(it.latitude, it.longitude, zoom) }
        }
        onDispose { controller.unregisterCurrentLocationCenteringAction() }
    }

    currentLocation?.let { loc ->
        LeaflektCircle(
            center = loc, radiusMeters = 50.0,
            fillColor = IosLocationBlue, strokeColor = IosLocationBlue,
            strokeWidth = 1f, fillOpacity = 0.12f, strokeOpacity = 0.22f,
            zIndex = 85f, id = IOS_LOCATION_ACCURACY_ID
        )
        LeaflektCircle(
            center = loc, radiusMeters = 18.0 * pulseRadiusScale,
            fillColor = IosLocationBlue, strokeColor = IosLocationBlue,
            strokeWidth = 1f, fillOpacity = pulseOpacity, strokeOpacity = 0f,
            zIndex = 90f, id = IOS_LOCATION_PULSE_ID
        )
        if (customMarkerIcon == null) {
            LeaflektCircle(
                center = loc, radiusMeters = 12.0,
                fillColor = IosLocationBlue, strokeColor = Color.White,
                strokeWidth = 3f, fillOpacity = 1f, strokeOpacity = 1f,
                zIndex = 100f, id = IOS_LOCATION_DOT_ID
            )
        } else {
            DisposableEffect(controller) {
                controller.addMarker(
                    LeaflektMarkerInfo(
                        id = IOS_LOCATION_MARKER_ID,
                        lat = loc.latitude,
                        lng = loc.longitude,
                        visible = true,
                        alpha = 1f,
                        icon = customMarkerIcon
                    )
                )
                onDispose { controller.removeMarker(IOS_LOCATION_MARKER_ID) }
            }
            LaunchedEffect(loc) {
                controller.updateMarker(
                    LeaflektMarkerInfo(
                        id = IOS_LOCATION_MARKER_ID,
                        lat = loc.latitude,
                        lng = loc.longitude,
                        visible = true,
                        alpha = 1f,
                        icon = customMarkerIcon
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun LeaflektCurrentLocationIcon.toMarkerIconInfo(): LeaflektMarkerIconInfo {
    val base64 = Base64.encode(pngBytes)
    val dataUrl = "data:image/png;base64,$base64"
    return LeaflektMarkerIconInfo(
        dataUrl = dataUrl,
        widthPx = widthPx.coerceAtLeast(1),
        heightPx = heightPx.coerceAtLeast(1),
        anchorFractionX = anchorFractionX.coerceIn(0f, 1f),
        anchorFractionY = anchorFractionY.coerceIn(0f, 1f)
    )
}

private val IosLocationBlue = Color(0xFF1A73E8)
private const val IOS_LOCATION_ACCURACY_ID = "leaflekt-cmp-location-accuracy"
private const val IOS_LOCATION_PULSE_ID = "leaflekt-cmp-location-pulse"
private const val IOS_LOCATION_DOT_ID = "leaflekt-cmp-location-dot"
private const val IOS_LOCATION_MARKER_ID = "leaflekt-cmp-location-marker"
