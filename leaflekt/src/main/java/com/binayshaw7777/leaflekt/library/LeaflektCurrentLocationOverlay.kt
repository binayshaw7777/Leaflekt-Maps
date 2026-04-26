package com.binayshaw7777.leaflekt.library

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@Composable
@LeaflektMapComposable
internal fun LeaflektCurrentLocationOverlay(
    uiSettings: LeaflektMapUiSettings
) {
    if (!uiSettings.showCurrentLocation) {
        return
    }

    val context = LocalContext.current
    val controller = LocalLeaflektController.current ?: return
    val fusedLocationClient = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val customMarkerIcon = remember(uiSettings.currentLocationIcon) {
        uiSettings.currentLocationIcon?.toMarkerIconInfo()
    }
    var hasLocationPermission by remember {
        mutableStateOf(context.hasLocationPermission())
    }
    var currentLocation by remember {
        mutableStateOf<LeaflektResolvedCurrentLocation?>(null)
    }
    var pendingCenterZoom by remember {
        mutableStateOf<Double?>(null)
    }
    val pulseTransition = rememberInfiniteTransition(label = "leaflektCurrentLocationPulse")
    val pulseRadiusScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leaflektCurrentLocationPulseRadius"
    )
    val pulseOpacity by pulseTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "leaflektCurrentLocationPulseOpacity"
    )
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        hasLocationPermission = grants.hasLocationPermission()
        if (!hasLocationPermission) {
            currentLocation = null
        }
    }

    LaunchedEffect(uiSettings.showCurrentLocation) {
        if (!uiSettings.showCurrentLocation) {
            currentLocation = null
            pendingCenterZoom = null
            return@LaunchedEffect
        }

        val canAccessLocation = context.hasLocationPermission()
        hasLocationPermission = canAccessLocation
        if (!canAccessLocation) {
            locationPermissionLauncher.launch(CurrentLocationPermissions)
        }
    }

    DisposableEffect(controller, currentLocation, hasLocationPermission) {
        controller.registerCurrentLocationCenteringAction { zoom ->
            val resolvedLocation = currentLocation
            if (resolvedLocation != null) {
                controller.moveCamera(
                    lat = resolvedLocation.position.latitude,
                    lng = resolvedLocation.position.longitude,
                    zoom = zoom
                )
                return@registerCurrentLocationCenteringAction
            }

            pendingCenterZoom = zoom
            if (!hasLocationPermission) {
                locationPermissionLauncher.launch(CurrentLocationPermissions)
            }
        }

        onDispose {
            controller.unregisterCurrentLocationCenteringAction()
        }
    }

    DisposableEffect(uiSettings.showCurrentLocation, hasLocationPermission, fusedLocationClient) {
        if (!uiSettings.showCurrentLocation || !hasLocationPermission) {
            currentLocation = null
            return@DisposableEffect onDispose {}
        }

        val locationUpdates = buildCurrentLocationRequest()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val latestLocation = result.lastLocation ?: return
                currentLocation = latestLocation.toResolvedCurrentLocation()
            }
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location?.toResolvedCurrentLocation()
        }
        fusedLocationClient.requestLocationUpdates(
            locationUpdates,
            locationCallback,
            Looper.getMainLooper()
        )

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    LaunchedEffect(currentLocation, pendingCenterZoom) {
        val zoom = pendingCenterZoom ?: return@LaunchedEffect
        val resolvedLocation = currentLocation ?: return@LaunchedEffect
        controller.moveCamera(
            lat = resolvedLocation.position.latitude,
            lng = resolvedLocation.position.longitude,
            zoom = zoom
        )
        pendingCenterZoom = null
    }

    currentLocation?.let { location ->
        LeaflektCircle(
            center = location.position,
            radiusMeters = location.accuracyMeters,
            fillColor = CurrentLocationBlue,
            strokeColor = CurrentLocationBlue,
            strokeWidth = 1f,
            fillOpacity = 0.12f,
            strokeOpacity = 0.22f,
            zIndex = 85f,
            id = CurrentLocationAccuracyId
        )

        LeaflektCircle(
            center = location.position,
            radiusMeters = location.pulseRadiusMeters(pulseRadiusScale),
            fillColor = CurrentLocationBlue,
            strokeColor = CurrentLocationBlue,
            strokeWidth = 1f,
            fillOpacity = pulseOpacity,
            strokeOpacity = 0f,
            zIndex = 90f,
            id = CurrentLocationPulseId
        )

        if (customMarkerIcon == null) {
            LeaflektCircle(
                center = location.position,
                radiusMeters = 12.0,
                fillColor = CurrentLocationBlue,
                strokeColor = Color.White,
                strokeWidth = 3f,
                fillOpacity = 1f,
                strokeOpacity = 1f,
                zIndex = 100f,
                id = CurrentLocationDotId
            )
        } else {
            LeaflektCurrentLocationMarker(
                controller = controller,
                location = location.position,
                icon = customMarkerIcon
            )
        }
    }
}

@Composable
private fun LeaflektCurrentLocationMarker(
    controller: LeaflektController,
    location: LeaflektLatLng,
    icon: LeaflektMarkerIconInfo
) {
    DisposableEffect(controller) {
        controller.addMarker(
            LeaflektMarkerInfo(
                id = CurrentLocationMarkerId,
                lat = location.latitude,
                lng = location.longitude,
                visible = true,
                alpha = 1f,
                icon = icon
            )
        )
        onDispose {
            controller.removeMarker(CurrentLocationMarkerId)
        }
    }

    LaunchedEffect(controller, location, icon) {
        controller.updateMarker(
            LeaflektMarkerInfo(
                id = CurrentLocationMarkerId,
                lat = location.latitude,
                lng = location.longitude,
                visible = true,
                alpha = 1f,
                icon = icon
            )
        )
    }
}

private fun buildCurrentLocationRequest(): LocationRequest {
    return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4_000L)
        .setMinUpdateIntervalMillis(2_000L)
        .setWaitForAccurateLocation(false)
        .build()
}

private fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
}

private fun Map<String, Boolean>.hasLocationPermission(): Boolean {
    return this[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
        this[Manifest.permission.ACCESS_COARSE_LOCATION] == true
}

private fun LeaflektCurrentLocationIcon.toMarkerIconInfo(): LeaflektMarkerIconInfo {
    return buildMarkerIconInfo(
        bitmap = bitmap,
        widthPx = widthPx,
        heightPx = heightPx,
        anchorFractionX = anchorFractionX,
        anchorFractionY = anchorFractionY
    )
}

private fun Location.toResolvedCurrentLocation(): LeaflektResolvedCurrentLocation {
    return LeaflektResolvedCurrentLocation(
        position = LeaflektLatLng(latitude = latitude, longitude = longitude),
        accuracyMeters = accuracy.toDouble().coerceIn(20.0, 300.0)
    )
}

private data class LeaflektResolvedCurrentLocation(
    val position: LeaflektLatLng,
    val accuracyMeters: Double
) {
    fun pulseRadiusMeters(pulseRadiusScale: Float): Double {
        return (18.0 * pulseRadiusScale).toDouble()
    }
}

private val CurrentLocationBlue = Color(0xFF1A73E8)

private val CurrentLocationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

private const val CurrentLocationAccuracyId = "leaflekt-current-location-accuracy"
private const val CurrentLocationPulseId = "leaflekt-current-location-pulse"
private const val CurrentLocationDotId = "leaflekt-current-location-dot"
private const val CurrentLocationMarkerId = "leaflekt-current-location-marker"
