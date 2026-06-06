import Foundation
import Combine

@MainActor
final class SampleViewModel: ObservableObject {

    private let service = OlaMapsService()
    private var searchDebounceTask: Task<Void, Never>?

    // MARK: - Explore
    @Published var exploreQuery = ""
    @Published var explorePredictions: [Prediction] = []
    @Published var isExploreLoading = false
    @Published var selectedExplorePlace: PlaceDetails?

    func onExploreQueryChange(_ query: String) {
        exploreQuery = query
        searchDebounceTask?.cancel()
        guard query.count > 2 else {
            explorePredictions = []
            return
        }
        searchDebounceTask = Task {
            try? await Task.sleep(nanoseconds: 450_000_000)
            guard !Task.isCancelled else { return }
            isExploreLoading = true
            explorePredictions = await service.autocomplete(input: query)
            isExploreLoading = false
        }
    }

    func selectExplorePrediction(_ prediction: Prediction) {
        exploreQuery = prediction.description
        explorePredictions = []
        Task {
            selectedExplorePlace = await service.getPlaceDetails(placeId: prediction.placeId)
        }
    }

    func clearExploreSearch() {
        exploreQuery = ""
        explorePredictions = []
        selectedExplorePlace = nil
    }

    // MARK: - Directions
    @Published var directionsQuery = ""
    @Published var directionsPredictions: [Prediction] = []
    @Published var isDirectionsLoading = false
    @Published var activeEndpoint: DirectionsEndpoint = .origin
    @Published var originPlace: PlaceDetails?
    @Published var destinationPlace: PlaceDetails?
    @Published var activeRoute: DirectionsRoute?
    @Published var routeErrorMessage: String?
    @Published var isRouteLoading = false

    func beginDirectionsSearch(_ endpoint: DirectionsEndpoint) {
        activeEndpoint = endpoint
        directionsQuery = ""
        directionsPredictions = []
    }

    func onDirectionsQueryChange(_ query: String) {
        directionsQuery = query
        searchDebounceTask?.cancel()
        guard query.count > 2 else {
            directionsPredictions = []
            return
        }
        searchDebounceTask = Task {
            try? await Task.sleep(nanoseconds: 450_000_000)
            guard !Task.isCancelled else { return }
            isDirectionsLoading = true
            directionsPredictions = await service.autocomplete(input: query)
            isDirectionsLoading = false
        }
    }

    func selectDirectionsPrediction(_ prediction: Prediction) {
        directionsQuery = ""
        directionsPredictions = []
        Task {
            let details = await service.getPlaceDetails(placeId: prediction.placeId)
            if activeEndpoint == .origin {
                originPlace = details
            } else {
                destinationPlace = details
            }
            await fetchRouteIfReady()
        }
    }

    func clearDirectionsSearch() {
        directionsQuery = ""
        directionsPredictions = []
    }

    func clearDirectionsPlace(_ endpoint: DirectionsEndpoint) {
        if endpoint == .origin { originPlace = nil } else { destinationPlace = nil }
        activeRoute = nil
        routeErrorMessage = nil
    }

    func swapDirectionsPlaces() {
        let tmp = originPlace
        originPlace = destinationPlace
        destinationPlace = tmp
        activeRoute = nil
        Task { await fetchRouteIfReady() }
    }

    func refreshRouteIfPossible() {
        Task { await fetchRouteIfReady() }
    }

    private func fetchRouteIfReady() async {
        guard let o = originPlace?.geometry?.location,
              let d = destinationPlace?.geometry?.location else { return }
        isRouteLoading = true
        routeErrorMessage = nil
        let result = await service.getDirections(origin: o, destination: d)
        isRouteLoading = false
        if let route = result {
            activeRoute = route
        } else {
            routeErrorMessage = "Could not fetch route. Check API key and network."
        }
    }
}
