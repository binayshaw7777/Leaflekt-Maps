package com.binayshaw7777.leaflekt.cmp

import com.binayshaw7777.leaflekt.LeaflektLatLng
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CmpOlaMapsRepository {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) { json(json) }
    }

    private val apiKey = BuildConfig.OLA_MAPS_KEY

    suspend fun autocomplete(input: String): List<CmpPrediction> = withContext(Dispatchers.IO) {
        if (input.isBlank()) return@withContext emptyList()
        runCatching {
            val body = client.get("https://api.olamaps.io/places/v1/autocomplete") {
                parameter("input", input)
                parameter("api_key", apiKey)
            }.bodyAsText()
            json.decodeFromString<CmpAutocompleteResponse>(body).predictions
        }.getOrElse { emptyList() }
    }

    suspend fun getPlaceDetails(placeId: String): CmpPlaceDetails? = withContext(Dispatchers.IO) {
        runCatching {
            client.get("https://api.olamaps.io/places/v1/details") {
                parameter("place_id", placeId)
                parameter("api_key", apiKey)
            }.body<CmpPlaceDetailsResponse>().result
        }.getOrNull()
    }

    suspend fun getDirections(origin: CmpLocation, destination: CmpLocation): CmpDirectionsRoute? =
        withContext(Dispatchers.IO) {
            val o = "${origin.lat},${origin.lng}"
            val d = "${destination.lat},${destination.lng}"
            val response = runCatching {
                client.post("https://api.olamaps.io/routing/v1/directions") {
                    parameter("origin", o); parameter("destination", d)
                    parameter("overview", "full"); parameter("alternatives", false)
                    parameter("steps", false); parameter("api_key", apiKey)
                }.bodyAsText()
            }.getOrElse {
                runCatching {
                    client.get("https://api.olamaps.io/routing/v1/directions") {
                        parameter("origin", o); parameter("destination", d)
                        parameter("overview", "full"); parameter("alternatives", false)
                        parameter("steps", false); parameter("api_key", apiKey)
                    }.bodyAsText()
                }.getOrNull()
            } ?: return@withContext null
            runCatching { json.parseToJsonElement(response).toDirectionsRoute() }.getOrNull()
        }
}

private fun JsonElement.toDirectionsRoute(): CmpDirectionsRoute? {
    val root = this as? JsonObject ?: return null
    val routes = root["routes"]?.jsonArray ?: return null
    val first = routes.firstOrNull()?.jsonObject ?: return null
    val points = first.routePoints()
    if (points.isEmpty()) return null
    return CmpDirectionsRoute(
        points = points,
        distanceMeters = first.numVal("distance") ?: first.numVal("distance_meters") ?: first.legSum("distance"),
        durationSeconds = first.numVal("duration") ?: first.numVal("duration_seconds") ?: first.legSum("duration"),
        summary = first["summary"]?.jsonPrimitive?.contentOrNull
    )
}

private fun JsonObject.routePoints(): List<LeaflektLatLng> {
    listOf("overview_polyline", "overviewPolyline", "geometry", "polyline", "route").forEach { k ->
        this[k]?.let { decodeSource(it) }?.let { return it }
    }
    return stepPoints() ?: emptyList()
}

private fun JsonObject.stepPoints(): List<LeaflektLatLng>? {
    val legs = this["legs"]?.jsonArray ?: return null
    val all = mutableListOf<LeaflektLatLng>()
    legs.forEach { leg ->
        leg.jsonObject["steps"]?.jsonArray?.forEach { step ->
            val s = step.jsonObject
            (decodeSource(s["polyline"]) ?: decodeSource(s["geometry"]))?.let { all += it }
        }
    }
    return all.takeIf { it.isNotEmpty() }
}

private fun decodeSource(src: JsonElement?): List<LeaflektLatLng>? {
    src ?: return null
    return when (src) {
        is JsonPrimitive -> src.contentOrNull?.let { decodePolyline(it) }
        is JsonObject -> {
            val enc = listOf("points", "polyline", "encodedPolyline", "encoded_polyline")
                .mapNotNull { src[it]?.jsonPrimitive?.contentOrNull }.firstOrNull()
            enc?.let { decodePolyline(it) }
                ?: (src["coordinates"] ?: src["path"])?.jsonArray?.toPoints()
        }
        is JsonArray -> src.toPoints()
        else -> null
    }
}

private fun JsonArray.toPoints(): List<LeaflektLatLng>? {
    val pts = mapNotNull { c ->
        val arr = c as? JsonArray ?: return@mapNotNull null
        if (arr.size < 2) return@mapNotNull null
        LeaflektLatLng(arr[1].jsonPrimitive.doubleOrNull ?: return@mapNotNull null,
            arr[0].jsonPrimitive.doubleOrNull ?: return@mapNotNull null)
    }
    return pts.takeIf { it.isNotEmpty() }
}

private fun JsonObject.numVal(key: String) = this[key]?.jsonPrimitive?.doubleOrNull

private fun JsonObject.legSum(key: String): Double? {
    val legs = this["legs"]?.jsonArray ?: return null
    val sum = legs.sumOf { it.jsonObject[key]?.jsonPrimitive?.doubleOrNull ?: 0.0 }
    return sum.takeIf { it > 0.0 }
}

private fun decodePolyline(encoded: String): List<LeaflektLatLng> {
    val pts = mutableListOf<LeaflektLatLng>()
    var i = 0; var lat = 0; var lng = 0
    while (i < encoded.length) {
        lat += chunk(encoded) { i++ }
        lng += chunk(encoded) { i++ }
        pts += LeaflektLatLng(lat / 1e5, lng / 1e5)
    }
    return pts
}

private inline fun chunk(s: String, advance: () -> Int): Int {
    var shift = 0; var result = 0; var c: Int
    do { c = s[advance()].code - 63; result = result or ((c and 0x1F) shl shift); shift += 5 } while (c >= 0x20)
    return if (result and 1 != 0) (result shr 1).inv() else result shr 1
}
