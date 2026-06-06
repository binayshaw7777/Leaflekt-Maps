package com.binayshaw7777.leaflekt.cmp

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
class CmpOlaMapsViewModel : ViewModel() {

    private val repo = CmpOlaMapsRepository()

    // Explore
    val exploreQuery = MutableStateFlow("")
    val isExploreLoading = MutableStateFlow(false)
    val selectedExplorePlace = MutableStateFlow<CmpPlaceDetails?>(null)

    @OptIn(FlowPreview::class)
    val explorePredictions: StateFlow<List<CmpPrediction>> = exploreQuery
        .debounce(450).distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.length <= 2) flow { emit(emptyList()) }
            else flow { isExploreLoading.value = true; emit(repo.autocomplete(q)); isExploreLoading.value = false }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onExploreQueryChange(q: String) { exploreQuery.value = q }

    fun selectExplorePrediction(p: CmpPrediction) {
        exploreQuery.value = p.description
        viewModelScope.launch {
            isExploreLoading.value = true
            selectedExplorePlace.value = repo.getPlaceDetails(p.placeId)
            isExploreLoading.value = false
        }
    }

    fun clearExplore() { exploreQuery.value = ""; selectedExplorePlace.value = null }

    // Directions
    val directionsQuery = MutableStateFlow("")
    val isDirectionsLoading = MutableStateFlow(false)
    val activeEndpoint = MutableStateFlow(CmpDirectionsEndpoint.Origin)
    val originPlace = MutableStateFlow<CmpPlaceDetails?>(null)
    val destinationPlace = MutableStateFlow<CmpPlaceDetails?>(null)
    val activeRoute = MutableStateFlow<CmpDirectionsRoute?>(null)
    val isRouteLoading = MutableStateFlow(false)
    val routeError = MutableStateFlow<String?>(null)

    @OptIn(FlowPreview::class)
    val directionsPredictions: StateFlow<List<CmpPrediction>> = directionsQuery
        .debounce(450).distinctUntilChanged()
        .flatMapLatest { q ->
            if (q.length <= 2) flow { emit(emptyList()) }
            else flow { isDirectionsLoading.value = true; emit(repo.autocomplete(q)); isDirectionsLoading.value = false }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun beginDirectionsSearch(ep: CmpDirectionsEndpoint) { activeEndpoint.value = ep; directionsQuery.value = "" }
    fun onDirectionsQueryChange(q: String) { directionsQuery.value = q }
    fun clearDirectionsSearch() { directionsQuery.value = "" }

    fun selectDirectionsPrediction(p: CmpPrediction) {
        viewModelScope.launch {
            isDirectionsLoading.value = true
            val place = repo.getPlaceDetails(p.placeId)
            when (activeEndpoint.value) {
                CmpDirectionsEndpoint.Origin -> originPlace.value = place
                CmpDirectionsEndpoint.Destination -> destinationPlace.value = place
            }
            directionsQuery.value = ""
            isDirectionsLoading.value = false
            fetchRouteIfReady()
        }
    }

    fun clearDirectionsPlace(ep: CmpDirectionsEndpoint) {
        when (ep) {
            CmpDirectionsEndpoint.Origin -> originPlace.value = null
            CmpDirectionsEndpoint.Destination -> destinationPlace.value = null
        }
        activeRoute.value = null; routeError.value = null
    }

    fun swapPlaces() {
        val tmp = originPlace.value
        originPlace.value = destinationPlace.value
        destinationPlace.value = tmp
        viewModelScope.launch { fetchRouteIfReady() }
    }

    fun refreshRoute() { viewModelScope.launch { fetchRouteIfReady() } }

    private suspend fun fetchRouteIfReady() {
        val o = originPlace.value?.geometry?.location ?: return
        val d = destinationPlace.value?.geometry?.location ?: return
        isRouteLoading.value = true; routeError.value = null
        val route = repo.getDirections(o, d)
        activeRoute.value = route
        routeError.value = if (route == null) "Could not fetch route. Check API key and network." else null
        isRouteLoading.value = false
    }
}
