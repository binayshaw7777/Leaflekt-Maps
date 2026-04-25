package com.binayshaw7777.leaflektsampleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OlaMapsViewModel : ViewModel() {

    private val repository = OlaMapsRepository()

    var searchQuery = MutableStateFlow("")
        private set

    var predictions = MutableStateFlow<List<Prediction>>(emptyList())
        private set

    var isLoading = MutableStateFlow(false)
        private set

    var selectedPlace = MutableStateFlow<PlaceDetails?>(null)
        private set

    init {
        setupAutocomplete()
    }

    @OptIn(FlowPreview::class)
    private fun setupAutocomplete() {
        searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.length > 2) {
                    // Updating state on Main thread (via viewModelScope)
                    isLoading.value = true

                    // The repository function is main-safe (internally uses Dispatchers.IO)
                    val results = repository.autocomplete(query)

                    // Back on Main thread to update UI state
                    predictions.value = results
                    isLoading.value = false
                } else {
                    predictions.value = emptyList()
                }
            }
            .launchIn(viewModelScope) // Defaults to Dispatchers.Main.immediate
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun selectPrediction(prediction: Prediction) {
        searchQuery.value = prediction.description
        viewModelScope.launch {
            isLoading.value = true

            // repository.getPlaceDetails is main-safe
            val details = repository.getPlaceDetails(prediction.placeId)

            selectedPlace.value = details
            isLoading.value = false
        }
    }

    fun clearSearch() {
        searchQuery.value = ""
        predictions.value = emptyList()
        selectedPlace.value = null
    }
}
