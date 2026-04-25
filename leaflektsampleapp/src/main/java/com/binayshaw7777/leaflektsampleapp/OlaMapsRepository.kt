package com.binayshaw7777.leaflektsampleapp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class OlaMapsRepository {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val apiKey = BuildConfig.OLA_MAPS_KEY

    /**
     * Fetches autocomplete predictions from Ola Maps API.
     * 
     * [Dispatchers.IO] is used as this is a network-bound operation.
     * This makes the function main-safe.
     */
    suspend fun autocomplete(input: String): List<Prediction> = withContext(Dispatchers.IO) {
        if (input.isBlank()) return@withContext emptyList()
        try {
            val response: AutocompleteResponse = client.get("https://api.olamaps.io/places/v1/autocomplete") {
                parameter("input", input)
                parameter("api_key", apiKey)
            }.body()
            response.predictions
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Fetches place details for a given placeId.
     * 
     * [Dispatchers.IO] is used as this is a network-bound operation.
     * This makes the function main-safe.
     */
    suspend fun getPlaceDetails(placeId: String): PlaceDetails? = withContext(Dispatchers.IO) {
        try {
            val response: PlaceDetailsResponse = client.get("https://api.olamaps.io/places/v1/details") {
                parameter("place_id", placeId)
                parameter("api_key", apiKey)
            }.body()
            response.result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
