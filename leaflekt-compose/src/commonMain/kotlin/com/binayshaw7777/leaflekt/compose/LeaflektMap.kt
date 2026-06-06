package com.binayshaw7777.leaflekt.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

val LocalLeaflektController = compositionLocalOf<LeaflektController?> { null }
internal val LocalLeaflektCameraPositionState = staticCompositionLocalOf { LeaflektCameraPositionState() }

@Composable
fun LeaflektMap(
    modifier: Modifier = Modifier,
    cameraPositionState: LeaflektCameraPositionState = rememberLeaflektCameraPositionState(),
    contentDescription: String? = null,
    properties: LeaflektMapProperties = DefaultLeaflektMapProperties,
    uiSettings: LeaflektMapUiSettings = DefaultLeaflektMapUiSettings,
    onMapLoaded: (() -> Unit)? = null,
    onReady: ((LeaflektController) -> Unit)? = null,
    onMapClick: ((LeaflektLatLng) -> Unit)? = null,
    onCameraMoveStarted: (() -> Unit)? = null,
    onCameraMove: (() -> Unit)? = null,
    onCameraIdle: (() -> Unit)? = null,
    onMarkerClick: ((String) -> Unit)? = null,
    content: @Composable @LeaflektMapComposable () -> Unit = {},
) {
    val controller = remember { LeaflektController() }
    var hasReportedReady by remember { mutableStateOf(false) }

    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnMapLoaded by rememberUpdatedState(onMapLoaded)
    val currentOnMapClick by rememberUpdatedState(onMapClick)
    val currentOnCameraMoveStarted by rememberUpdatedState(onCameraMoveStarted)
    val currentOnCameraMove by rememberUpdatedState(onCameraMove)
    val currentOnCameraIdle by rememberUpdatedState(onCameraIdle)
    val currentOnMarkerClick by rememberUpdatedState(onMarkerClick)
    val currentCameraPositionState by rememberUpdatedState(cameraPositionState)

    val bridge = remember {
        object : LeaflektBridgeCallbacks {
            override fun onMapReady() {
                controller.notifyMapReady()
                if (!hasReportedReady) {
                    hasReportedReady = true
                    currentOnReady?.invoke(controller)
                    currentOnMapLoaded?.invoke()
                }
            }

            override fun onMapClick(lat: Double, lng: Double) {
                currentOnMapClick?.invoke(LeaflektLatLng(latitude = lat, longitude = lng))
            }

            override fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double) {
                currentCameraPositionState.onCameraMoveStarted(
                    LeaflektCameraPosition(target = LeaflektLatLng(lat, lng), zoom = zoom)
                )
                currentOnCameraMoveStarted?.invoke()
            }

            override fun onCameraMove(lat: Double, lng: Double, zoom: Double) {
                currentCameraPositionState.onCameraMove(
                    LeaflektCameraPosition(target = LeaflektLatLng(lat, lng), zoom = zoom)
                )
                currentOnCameraMove?.invoke()
            }

            override fun onCameraIdle(lat: Double, lng: Double, zoom: Double) {
                currentCameraPositionState.onCameraIdle(
                    LeaflektCameraPosition(target = LeaflektLatLng(lat, lng), zoom = zoom)
                )
                currentOnCameraIdle?.invoke()
            }

            override fun onMarkerClick(markerId: String) {
                controller.notifyMarkerClick(markerId)
                currentOnMarkerClick?.invoke(markerId)
            }

            override fun onPolylineClick(polylineId: String) {
                controller.notifyPolylineClick(polylineId)
            }

            override fun onPolygonClick(polygonId: String) {
                controller.notifyPolygonClick(polygonId)
            }

            override fun onCircleClick(circleId: String) {
                controller.notifyCircleClick(circleId)
            }
        }
    }

    DisposableEffect(controller, cameraPositionState) {
        cameraPositionState.setController(controller)
        onDispose { cameraPositionState.setController(null) }
    }

    LaunchedEffect(controller) {
        controller.initializeMap(
            initialLat = cameraPositionState.position.target.latitude,
            initialLng = cameraPositionState.position.target.longitude,
            initialZoom = cameraPositionState.position.zoom,
            isZoomControlEnabled = uiSettings.zoomControlsEnabled,
            initialMapStyle = properties.mapStyle,
            initialGeoJsonOverlay = properties.geoJsonOverlay
        )
    }

    LaunchedEffect(properties.mapStyle) { controller.setMapStyle(properties.mapStyle) }
    LaunchedEffect(properties.geoJsonOverlay) { controller.setGeoJsonOverlay(properties.geoJsonOverlay) }
    LaunchedEffect(uiSettings.zoomControlsEnabled) { controller.setZoomControlsEnabled(uiSettings.zoomControlsEnabled) }
    LaunchedEffect(uiSettings.scrollGesturesEnabled) { controller.setScrollGesturesEnabled(uiSettings.scrollGesturesEnabled) }
    LaunchedEffect(uiSettings.zoomGesturesEnabled) { controller.setZoomGesturesEnabled(uiSettings.zoomGesturesEnabled) }

    Box(modifier = modifier) {
        PlatformWebView(
            modifier = Modifier.fillMaxSize(),
            controller = controller,
            bridge = bridge,
            contentDescription = contentDescription
        )

        CompositionLocalProvider(
            LocalLeaflektController provides controller,
            LocalLeaflektCameraPositionState provides cameraPositionState
        ) {
            LeaflektCurrentLocationOverlay(uiSettings = uiSettings)
            content()
        }
    }
}
