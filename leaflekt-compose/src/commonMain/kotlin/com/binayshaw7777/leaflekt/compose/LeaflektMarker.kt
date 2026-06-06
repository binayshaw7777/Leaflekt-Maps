package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
@LeaflektMapComposable
fun LeaflektMarker(
    state: LeaflektMarkerState = rememberLeaflektMarkerState(),
    title: String? = null,
    snippet: String? = null,
    visible: Boolean = true,
    alpha: Float = 1.0f,
    id: String = remember { generateId() },
    onClick: () -> Boolean = { false }
) {
    val controller = LocalLeaflektController.current ?: return

    DisposableEffect(id) {
        controller.addMarker(
            LeaflektMarkerInfo(id = id, lat = state.position.latitude, lng = state.position.longitude, title = title, snippet = snippet, visible = visible, alpha = alpha)
        )
        controller.registerMarkerClick(id, onClick)
        onDispose {
            controller.unregisterMarkerClick(id)
            controller.removeMarker(id)
        }
    }

    DisposableEffect(id, onClick) {
        controller.registerMarkerClick(id, onClick)
        onDispose { controller.unregisterMarkerClick(id) }
    }

    LaunchedEffect(state.position, title, snippet, visible, alpha) {
        controller.updateMarker(
            LeaflektMarkerInfo(id = id, lat = state.position.latitude, lng = state.position.longitude, title = title, snippet = snippet, visible = visible, alpha = alpha)
        )
    }
}

@Composable
@LeaflektMapComposable
fun LeaflektMarker(
    position: LeaflektLatLng,
    title: String? = null,
    snippet: String? = null,
    visible: Boolean = true,
    alpha: Float = 1.0f,
    id: String = remember { generateId() },
    onClick: () -> Boolean = { false }
) {
    LeaflektMarker(
        state = rememberLeaflektMarkerState(position = position),
        title = title, snippet = snippet, visible = visible, alpha = alpha, id = id, onClick = onClick
    )
}
