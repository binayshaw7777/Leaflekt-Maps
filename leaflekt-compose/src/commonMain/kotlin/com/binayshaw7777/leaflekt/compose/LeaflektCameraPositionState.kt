package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class LeaflektCameraPositionState(
    initialPosition: LeaflektCameraPosition = LeaflektCameraPosition(
        target = LeaflektLatLng(22.5726, 88.3639),
        zoom = 12.0
    )
) {
    var isMoving: Boolean by mutableStateOf(false)
        internal set

    internal var rawPosition by mutableStateOf(initialPosition)

    var position: LeaflektCameraPosition
        get() = rawPosition
        set(value) {
            val controller = boundController
            if (controller == null) {
                rawPosition = value
            } else {
                controller.moveCamera(
                    lat = value.target.latitude,
                    lng = value.target.longitude,
                    zoom = value.zoom
                )
            }
        }

    private var boundController: LeaflektControllerInterface? = null

    internal fun setController(controller: LeaflektControllerInterface?) {
        if (this.boundController == null && controller == null) return
        this.boundController = controller
        if (controller != null) {
            controller.moveCamera(
                lat = position.target.latitude,
                lng = position.target.longitude,
                zoom = position.zoom
            )
        } else {
            isMoving = false
        }
    }

    fun move(target: LeaflektLatLng, zoom: Double = position.zoom) {
        this.position = LeaflektCameraPosition(target, zoom)
    }

    internal fun onCameraMoveStarted(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraMove(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = true
    }

    internal fun onCameraIdle(position: LeaflektCameraPosition) {
        rawPosition = position
        isMoving = false
    }

    companion object {
        val Saver: Saver<LeaflektCameraPositionState, *> = listSaver(
            save = {
                listOf(
                    it.position.target.latitude,
                    it.position.target.longitude,
                    it.position.zoom
                )
            },
            restore = {
                LeaflektCameraPositionState(
                    initialPosition = LeaflektCameraPosition(
                        target = LeaflektLatLng(it[0], it[1]),
                        zoom = it[2]
                    )
                )
            }
        )
    }
}

@Composable
fun rememberLeaflektCameraPositionState(
    init: LeaflektCameraPositionState.() -> Unit = {}
): LeaflektCameraPositionState = rememberSaveable(saver = LeaflektCameraPositionState.Saver) {
    LeaflektCameraPositionState().apply(init)
}
