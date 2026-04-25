package com.binayshaw7777.leaflektsampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OlaMapsViewModel : ViewModel() {

    private val repository = OlaMapsRepository()

    val exploreSearchQuery = MutableStateFlow("")
    val isExploreSearchLoading = MutableStateFlow(false)
    val selectedExplorePlace = MutableStateFlow<PlaceDetails?>(null)

    @OptIn(FlowPreview::class)
    val explorePredictions: StateFlow<List<Prediction>> = exploreSearchQuery
        .debounce(450)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length <= 2) {
                flow { emit(emptyList<Prediction>()) }
            } else {
                flow {
                    isExploreSearchLoading.value = true
                    emit(repository.autocomplete(query))
                    isExploreSearchLoading.value = false
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val directionsSearchQuery = MutableStateFlow("")
    val isDirectionsSearchLoading = MutableStateFlow(false)
    val selectedOriginPlace = MutableStateFlow<PlaceDetails?>(null)
    val selectedDestinationPlace = MutableStateFlow<PlaceDetails?>(null)
    val activeDirectionsEndpoint = MutableStateFlow(DirectionsEndpoint.Origin)
    val activeRoute = MutableStateFlow<DirectionsRoute?>(null)
    val isRouteLoading = MutableStateFlow(false)
    val routeErrorMessage = MutableStateFlow<String?>(null)

    @OptIn(FlowPreview::class)
    val directionsPredictions: StateFlow<List<Prediction>> = directionsSearchQuery
        .debounce(450)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length <= 2) {
                flow { emit(emptyList<Prediction>()) }
            } else {
                flow {
                    isDirectionsSearchLoading.value = true
                    emit(repository.autocomplete(query))
                    isDirectionsSearchLoading.value = false
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onExploreSearchQueryChange(newQuery: String) {
        exploreSearchQuery.value = newQuery
    }

    fun selectExplorePrediction(prediction: Prediction) {
        exploreSearchQuery.value = prediction.description
        viewModelScope.launch {
            isExploreSearchLoading.value = true
            selectedExplorePlace.value = repository.getPlaceDetails(prediction.placeId)
            isExploreSearchLoading.value = false
        }
    }

    fun clearExploreSearch() {
        exploreSearchQuery.value = ""
        selectedExplorePlace.value = null
    }

    fun beginDirectionsSearch(endpoint: DirectionsEndpoint) {
        activeDirectionsEndpoint.value = endpoint
        directionsSearchQuery.value = ""
    }

    fun onDirectionsSearchQueryChange(newQuery: String) {
        directionsSearchQuery.value = newQuery
    }

    fun selectDirectionsPrediction(prediction: Prediction) {
        viewModelScope.launch {
            isDirectionsSearchLoading.value = true
            directionsSearchQuery.value = prediction.description

            val selectedPlace = repository.getPlaceDetails(prediction.placeId)
            assignDirectionsPlace(selectedPlace)

            directionsSearchQuery.value = ""
            isDirectionsSearchLoading.value = false

            refreshRouteIfPossible()
        }
    }

    fun clearDirectionsSearch() {
        directionsSearchQuery.value = ""
    }

    fun clearDirectionsPlace(endpoint: DirectionsEndpoint) {
        when (endpoint) {
            DirectionsEndpoint.Origin -> selectedOriginPlace.value = null
            DirectionsEndpoint.Destination -> selectedDestinationPlace.value = null
        }

        activeRoute.value = null
        routeErrorMessage.value = null
    }

    fun swapDirectionsPlaces() {
        val previousOrigin = selectedOriginPlace.value
        selectedOriginPlace.value = selectedDestinationPlace.value
        selectedDestinationPlace.value = previousOrigin
        refreshRouteIfPossible()
    }

    fun refreshRouteIfPossible() {
        val origin = selectedOriginPlace.value?.geometry?.location
        val destination = selectedDestinationPlace.value?.geometry?.location

        if (origin == null || destination == null) {
            activeRoute.value = null
            routeErrorMessage.value = null
            isRouteLoading.value = false
            return
        }

        viewModelScope.launch {
            isRouteLoading.value = true
            routeErrorMessage.value = null

            val route = repository.getDirections(origin, destination)
            activeRoute.value = route
            routeErrorMessage.value = if (route == null) {
                "Unable to load directions for the selected places."
            } else {
                null
            }

            isRouteLoading.value = false
        }
    }

    private fun assignDirectionsPlace(selectedPlace: PlaceDetails?) {
        when (activeDirectionsEndpoint.value) {
            DirectionsEndpoint.Origin -> selectedOriginPlace.value = selectedPlace
            DirectionsEndpoint.Destination -> selectedDestinationPlace.value = selectedPlace
        }
    }
}
