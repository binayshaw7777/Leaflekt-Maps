package com.binayshaw7777.leaflekt.cmp

import com.binayshaw7777.leaflekt.LeaflektLatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
data class CmpAutocompleteResponse(
    val predictions: List<CmpPrediction> = emptyList(),
    val status: String? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

@Serializable
data class CmpPrediction(
    val description: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("structured_formatting") val structuredFormatting: CmpStructuredFormatting? = null
)

@Serializable
data class CmpStructuredFormatting(
    @SerialName("main_text") val mainText: String,
    @SerialName("secondary_text") val secondaryText: String? = null
)

@Serializable
data class CmpPlaceDetailsResponse(
    val result: CmpPlaceDetails? = null,
    val status: String? = null
)

@Serializable
data class CmpPlaceDetails(
    val geometry: CmpGeometry? = null,
    val name: String? = null,
    @SerialName("formatted_address") val formattedAddress: String? = null
) {
    fun headline(): String =
        name?.takeIf { it.isNotBlank() } ?: formattedAddress?.takeIf { it.isNotBlank() } ?: "Selected place"

    fun supportingLine(): String? {
        val addr = formattedAddress?.takeIf { it.isNotBlank() }
        return if (addr == name) null else addr
    }
}

@Serializable
data class CmpGeometry(val location: CmpLocation? = null)

@Serializable
data class CmpLocation(val lat: Double, val lng: Double) {
    val latLng get() = LeaflektLatLng(lat, lng)
}

enum class CmpDirectionsEndpoint { Origin, Destination }

data class CmpDirectionsRoute(
    val points: List<LeaflektLatLng>,
    val distanceMeters: Double?,
    val durationSeconds: Double?,
    val summary: String? = null
) {
    fun cameraTarget(): LeaflektLatLng {
        val first = points.firstOrNull() ?: LeaflektLatLng(22.5726, 88.3639)
        val last = points.lastOrNull() ?: first
        return LeaflektLatLng((first.latitude + last.latitude) / 2, (first.longitude + last.longitude) / 2)
    }

    fun recommendedZoom(): Double {
        val first = points.firstOrNull() ?: return 12.0
        val last = points.lastOrNull() ?: return 12.0
        val span = maxOf(abs(first.latitude - last.latitude), abs(first.longitude - last.longitude))
        return when {
            span > 8.0 -> 4.5; span > 4.0 -> 5.5; span > 2.0 -> 6.5; span > 1.0 -> 7.5
            span > 0.5 -> 9.0; span > 0.2 -> 10.5; span > 0.1 -> 11.5; span > 0.05 -> 12.5
            else -> 13.5
        }
    }

    fun distanceLabel(): String? {
        val d = distanceMeters ?: return null
        if (d >= 1000) {
            val km = (d / 100.0).toInt() / 10.0
            return "$km km"
        }
        return "${d.toInt()} m"
    }

    fun durationLabel(): String? {
        val s = durationSeconds ?: return null
        val mins = (s / 60).toInt()
        val h = mins / 60; val m = mins % 60
        return when { h > 0 && m > 0 -> "${h}h ${m}m"; h > 0 -> "${h}h"; else -> "${m}m" }
    }
}
