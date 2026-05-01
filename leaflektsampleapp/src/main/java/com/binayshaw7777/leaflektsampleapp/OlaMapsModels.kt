package com.binayshaw7777.leaflektsampleapp

import com.binayshaw7777.leaflekt.library.camera.LatLng
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.abs

@Serializable
data class AutocompleteResponse(
    val predictions: List<Prediction> = emptyList(),
    val status: String? = null,
    @SerialName("error_message") val errorMessage: String? = null
)

@Serializable
data class Prediction(
    val description: String,
    @SerialName("place_id") val placeId: String,
    @SerialName("structured_formatting") val structuredFormatting: StructuredFormatting? = null,
    val terms: List<Term>? = null,
    val types: List<String>? = null,
    val reference: String? = null,
    val distanceMeters: Int? = null
)

@Serializable
data class StructuredFormatting(
    @SerialName("main_text") val mainText: String,
    @SerialName("secondary_text") val secondaryText: String? = null
)

@Serializable
data class Term(
    val offset: Int,
    val value: String
)

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceDetails? = null,
    val status: String? = null
)

@Serializable
data class PlaceDetails(
    val geometry: Geometry? = null,
    val name: String? = null,
    @SerialName("formatted_address") val formattedAddress: String? = null
) {
    fun headline(): String {
        return name?.takeIf { it.isNotBlank() }
            ?: formattedAddress?.takeIf { it.isNotBlank() }
            ?: "Selected place"
    }

    fun supportingLine(): String? {
        val address = formattedAddress?.takeIf { it.isNotBlank() }
        return if (address == name) null else address
    }
}

@Serializable
data class Geometry(
    val location: Location? = null
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

enum class DirectionsEndpoint {
    Origin,
    Destination
}

data class DirectionsRoute(
    val points: List<LatLng>,
    val distanceMeters: Double?,
    val durationSeconds: Double?,
    val summary: String? = null
) {
    fun cameraTarget(): LatLng {
        val firstPoint = points.firstOrNull() ?: LatLng(22.5726, 88.3639)
        val lastPoint = points.lastOrNull() ?: firstPoint
        return LatLng(
            latitude = (firstPoint.latitude + lastPoint.latitude) / 2,
            longitude = (firstPoint.longitude + lastPoint.longitude) / 2
        )
    }

    fun recommendedZoom(): Double {
        val firstPoint = points.firstOrNull() ?: return 12.0
        val lastPoint = points.lastOrNull() ?: return 12.0
        val latitudeSpan = abs(firstPoint.latitude - lastPoint.latitude)
        val longitudeSpan = abs(firstPoint.longitude - lastPoint.longitude)
        val dominantSpan = maxOf(latitudeSpan, longitudeSpan)

        return when {
            dominantSpan > 8.0 -> 4.5
            dominantSpan > 4.0 -> 5.5
            dominantSpan > 2.0 -> 6.5
            dominantSpan > 1.0 -> 7.5
            dominantSpan > 0.5 -> 9.0
            dominantSpan > 0.2 -> 10.5
            dominantSpan > 0.1 -> 11.5
            dominantSpan > 0.05 -> 12.5
            else -> 13.5
        }
    }

    fun distanceLabel(): String? {
        val distance = distanceMeters ?: return null
        return if (distance >= 1000) {
            String.format("%.1f km", distance / 1000.0)
        } else {
            "${distance.toInt()} m"
        }
    }

    fun durationLabel(): String? {
        val duration = durationSeconds ?: return null
        val totalMinutes = (duration / 60.0).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            else -> "${minutes}m"
        }
    }
}

internal fun JsonElement.toDirectionsRoute(): DirectionsRoute? {
    val root = this as? JsonObject ?: return null
    val routes = root["routes"]?.jsonArray ?: return null
    val firstRoute = routes.firstOrNull()?.jsonObject ?: return null
    val routePoints = firstRoute.routePoints()
    if (routePoints.isEmpty()) {
        return null
    }

    return DirectionsRoute(
        points = routePoints,
        distanceMeters = firstRoute.routeDistanceMeters(),
        durationSeconds = firstRoute.routeDurationSeconds(),
        summary = firstRoute.stringValue("summary")
    )
}

private fun JsonObject.routePoints(): List<LatLng> {
    return decodePolylineSource(this["overview_polyline"])
        ?: decodePolylineSource(this["overviewPolyline"])
        ?: decodePolylineSource(this["geometry"])
        ?: decodePolylineSource(this["polyline"])
        ?: decodePolylineSource(this["route"])
        ?: routeStepPoints()
        ?: emptyList()
}

private fun JsonObject.routeDistanceMeters(): Double? {
    return numberValue("distance")
        ?: numberValue("distance_meters")
        ?: sumLegValue("distance")
        ?: sumLegValue("distance_meters")
}

private fun JsonObject.routeDurationSeconds(): Double? {
    return numberValue("duration")
        ?: numberValue("duration_seconds")
        ?: sumLegValue("duration")
        ?: sumLegValue("duration_seconds")
}

private fun JsonObject.routeStepPoints(): List<LatLng>? {
    val legs = this["legs"]?.jsonArray ?: return null
    val points = buildList {
        legs.forEach { legElement ->
            val steps = legElement.jsonObject["steps"]?.jsonArray.orEmpty()
            steps.forEach { stepElement ->
                val stepPoints = decodePolylineSource(stepElement.jsonObject["polyline"])
                    ?: decodePolylineSource(stepElement.jsonObject["geometry"])
                addAll(stepPoints.orEmpty())
            }
        }
    }

    return points.removeAdjacentDuplicates().takeIf { it.isNotEmpty() }
}

private fun decodePolylineSource(source: JsonElement?): List<LatLng>? {
    if (source == null) {
        return null
    }

    return when (source) {
        is JsonPrimitive -> {
            val encodedPolyline = source.contentOrNull ?: return null
            decodePolyline(encodedPolyline)
        }

        is JsonObject -> {
            val encodedPolyline = source["points"]?.jsonPrimitive?.contentOrNull
                ?: source["polyline"]?.jsonPrimitive?.contentOrNull
                ?: source["encodedPolyline"]?.jsonPrimitive?.contentOrNull
                ?: source["encoded_polyline"]?.jsonPrimitive?.contentOrNull
            if (encodedPolyline != null) {
                return decodePolyline(encodedPolyline)
            }

            val coordinates = source["coordinates"]?.jsonArray
                ?: source["path"]?.jsonArray
            coordinates?.toRoutePoints()
        }

        is JsonArray -> source.toRoutePoints()
        else -> null
    }
}

private fun JsonArray.toRoutePoints(): List<LatLng>? {
    val points = mapNotNull { coordinate ->
        val items = coordinate.jsonArrayOrNull() ?: return@mapNotNull null
        if (items.size < 2) {
            return@mapNotNull null
        }

        val firstValue = items[0].jsonPrimitive.doubleOrNull ?: return@mapNotNull null
        val secondValue = items[1].jsonPrimitive.doubleOrNull ?: return@mapNotNull null
        LatLng(latitude = secondValue, longitude = firstValue)
    }

    return points.takeIf { it.isNotEmpty() }
}

private fun JsonObject.sumLegValue(key: String): Double? {
    val legs = this["legs"]?.jsonArray ?: return null
    val sum = legs.sumOf { leg ->
        leg.jsonObject.numberValue(key) ?: 0.0
    }
    return sum.takeIf { it > 0.0 }
}

private fun JsonObject.numberValue(key: String): Double? {
    return this[key]?.jsonPrimitive?.doubleOrNull
}

private fun JsonObject.stringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonElement.jsonArrayOrNull(): JsonArray? {
    return this as? JsonArray
}

private fun List<LatLng>.removeAdjacentDuplicates(): List<LatLng> {
    if (isEmpty()) {
        return this
    }

    return buildList {
        add(first())
        drop(1).forEach { point ->
            val lastPoint = last()
            if (lastPoint.latitude != point.latitude || lastPoint.longitude != point.longitude) {
                add(point)
            }
        }
    }
}

private fun decodePolyline(encodedPolyline: String): List<LatLng> {
    if (encodedPolyline.isBlank()) {
        return emptyList()
    }

    val points = mutableListOf<LatLng>()
    var index = 0
    var latitude = 0
    var longitude = 0

    while (index < encodedPolyline.length) {
        latitude += decodePolylineValue(encodedPolyline) { index++ }
        longitude += decodePolylineValue(encodedPolyline) { index++ }

        points += LatLng(
            latitude = latitude / 1E5,
            longitude = longitude / 1E5
        )
    }

    return points
}

private fun decodePolylineValue(
    encodedPolyline: String,
    advanceIndex: () -> Int
): Int {
    var shift = 0
    var result = 0
    var chunk: Int

    do {
        val currentIndex = advanceIndex()
        chunk = encodedPolyline[currentIndex].code - 63
        result = result or ((chunk and 0x1F) shl shift)
        shift += 5
    } while (chunk >= 0x20)

    return if (result and 1 != 0) {
        (result shr 1).inv()
    } else {
        result shr 1
    }
}

