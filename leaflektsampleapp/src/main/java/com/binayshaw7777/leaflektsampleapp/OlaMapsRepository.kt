package com.binayshaw7777.leaflektsampleapp

import android.util.Log
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

class OlaMapsRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    private val apiKey = BuildConfig.OLA_MAPS_KEY

    suspend fun autocomplete(input: String): List<Prediction> = withContext(Dispatchers.IO) {
        if (input.isBlank()) {
            return@withContext emptyList()
        }

        Log.d("OlaMaps", "Searching for: $input (API Key: ${apiKey.take(5)}...)")

        runCatching {
            val response = client.get("https://api.olamaps.io/places/v1/autocomplete") {
                parameter("input", input)
                parameter("api_key", apiKey)
            }
            val body = response.bodyAsText()
            Log.d("OlaMaps", "Response: $body")
            json.decodeFromString<AutocompleteResponse>(body).predictions
        }.getOrElse {
            Log.e("OlaMaps", "Error fetching autocomplete", it)
            emptyList()
        }
    }

    suspend fun getPlaceDetails(placeId: String): PlaceDetails? = withContext(Dispatchers.IO) {
        runCatching {
            client.get("https://api.olamaps.io/places/v1/details") {
                parameter("place_id", placeId)
                parameter("api_key", apiKey)
            }.body<PlaceDetailsResponse>().result
        }.getOrNull()
    }

    suspend fun getDirections(
        origin: Location,
        destination: Location
    ): DirectionsRoute? = withContext(Dispatchers.IO) {
        val directionsResponse = requestDirectionsResponse(origin, destination) ?: return@withContext null

        runCatching {
            json.parseToJsonElement(directionsResponse).toDirectionsRoute()
        }.getOrNull()
    }

    private suspend fun requestDirectionsResponse(
        origin: Location,
        destination: Location
    ): String? {
        val originValue = "${origin.lat},${origin.lng}"
        val destinationValue = "${destination.lat},${destination.lng}"

        val response = runCatching {
            client.post("https://api.olamaps.io/routing/v1/directions") {
                parameter("origin", originValue)
                parameter("destination", destinationValue)
                parameter("overview", "full")
                parameter("alternatives", false)
                parameter("steps", false)
                parameter("api_key", apiKey)
            }.bodyAsText()
        }.getOrElse {
            client.get("https://api.olamaps.io/routing/v1/directions") {
                parameter("origin", originValue)
                parameter("destination", destinationValue)
                parameter("overview", "full")
                parameter("alternatives", false)
                parameter("steps", false)
                parameter("api_key", apiKey)
            }.bodyAsText()
        }

        return response.takeIf { it.isNotBlank() }
    }
}
