package com.binayshaw7777.leaflektsampleapp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
)

@Serializable
data class Geometry(
    val location: Location? = null
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)
