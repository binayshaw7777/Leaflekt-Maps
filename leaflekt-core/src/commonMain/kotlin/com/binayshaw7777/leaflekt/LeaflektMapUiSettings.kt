package com.binayshaw7777.leaflekt

val DefaultLeaflektMapUiSettings: LeaflektMapUiSettings = LeaflektMapUiSettings()

class LeaflektMapUiSettings(
    val zoomControlsEnabled: Boolean = true,
    val scrollGesturesEnabled: Boolean = true,
    val zoomGesturesEnabled: Boolean = true,
    val showCurrentLocation: Boolean = false,
    val currentLocationIcon: LeaflektCurrentLocationIcon? = null,
    val myLocationButtonEnabled: Boolean = false,
) {
    override fun toString(): String = "LeaflektMapUiSettings(" +
            "zoomControlsEnabled=$zoomControlsEnabled, " +
            "scrollGesturesEnabled=$scrollGesturesEnabled, " +
            "zoomGesturesEnabled=$zoomGesturesEnabled, " +
            "showCurrentLocation=$showCurrentLocation, " +
            "hasCurrentLocationIcon=${currentLocationIcon != null}, " +
            "myLocationButtonEnabled=$myLocationButtonEnabled)"

    override fun equals(other: Any?): Boolean = other is LeaflektMapUiSettings &&
            zoomControlsEnabled == other.zoomControlsEnabled &&
            scrollGesturesEnabled == other.scrollGesturesEnabled &&
            zoomGesturesEnabled == other.zoomGesturesEnabled &&
            showCurrentLocation == other.showCurrentLocation &&
            currentLocationIcon == other.currentLocationIcon &&
            myLocationButtonEnabled == other.myLocationButtonEnabled

    override fun hashCode(): Int {
        var result = zoomControlsEnabled.hashCode()
        result = 31 * result + scrollGesturesEnabled.hashCode()
        result = 31 * result + zoomGesturesEnabled.hashCode()
        result = 31 * result + showCurrentLocation.hashCode()
        result = 31 * result + (currentLocationIcon?.hashCode() ?: 0)
        result = 31 * result + myLocationButtonEnabled.hashCode()
        return result
    }

    fun copy(
        zoomControlsEnabled: Boolean = this.zoomControlsEnabled,
        scrollGesturesEnabled: Boolean = this.scrollGesturesEnabled,
        zoomGesturesEnabled: Boolean = this.zoomGesturesEnabled,
        showCurrentLocation: Boolean = this.showCurrentLocation,
        currentLocationIcon: LeaflektCurrentLocationIcon? = this.currentLocationIcon,
        myLocationButtonEnabled: Boolean = this.myLocationButtonEnabled,
    ): LeaflektMapUiSettings = LeaflektMapUiSettings(
        zoomControlsEnabled = zoomControlsEnabled,
        scrollGesturesEnabled = scrollGesturesEnabled,
        zoomGesturesEnabled = zoomGesturesEnabled,
        showCurrentLocation = showCurrentLocation,
        currentLocationIcon = currentLocationIcon,
        myLocationButtonEnabled = myLocationButtonEnabled
    )
}
