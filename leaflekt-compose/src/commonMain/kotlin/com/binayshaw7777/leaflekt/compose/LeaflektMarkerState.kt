package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class LeaflektMarkerState(position: LeaflektLatLng = LeaflektLatLng(0.0, 0.0)) {
    var position: LeaflektLatLng by mutableStateOf(position)

    fun showInfoWindow() {}
    fun hideInfoWindow() {}

    companion object {
        val Saver: Saver<LeaflektMarkerState, *> = Saver(
            save = { listOf(it.position.latitude, it.position.longitude) },
            restore = { LeaflektMarkerState(LeaflektLatLng(it[0], it[1])) }
        )
    }
}

@Composable
fun rememberLeaflektMarkerState(
    key: String? = null,
    position: LeaflektLatLng = LeaflektLatLng(0.0, 0.0)
): LeaflektMarkerState = rememberSaveable(key = key, saver = LeaflektMarkerState.Saver) {
    LeaflektMarkerState(position)
}
