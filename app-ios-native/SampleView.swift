import SwiftUI
import LeaflektMap

// MARK: - Root

struct SampleView: View {
    @StateObject private var vm = SampleViewModel()
    @State private var selectedTab: SampleTab = .explore
    @State private var selectedStyle: LeaflektMapStyle = .cartoDark
    var onBack: (() -> Void)? = nil

    var body: some View {
        ZStack(alignment: .topLeading) {
            TabView(selection: $selectedTab) {
                ExploreMapScreen(vm: vm, selectedStyle: $selectedStyle)
                    .tabItem { Label("Explore", systemImage: "magnifyingglass") }
                    .tag(SampleTab.explore)

                DirectionsMapScreen(vm: vm, selectedStyle: $selectedStyle)
                    .tabItem { Label("Directions", systemImage: "location.north.fill") }
                    .tag(SampleTab.directions)

                ClusteringMapScreen(vm: vm, selectedStyle: $selectedStyle)
                    .tabItem { Label("Clustering", systemImage: "square.3.layers.3d") }
                    .tag(SampleTab.clustering)
            }

            if let onBack = onBack {
                Button(action: onBack) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 16, weight: .semibold))
                        .frame(width: 36, height: 36)
                        .background(.ultraThinMaterial, in: Circle())
                }
                .padding(.leading, 16)
                .padding(.top, 8)
            }
        }
    }
}

private enum SampleTab { case explore, directions, clustering }

private let kolkata = LeaflektLatLng(latitude: 22.5726, longitude: 88.3639)

// MARK: - Explore

struct ExploreMapScreen: View {
    @ObservedObject var vm: SampleViewModel
    @Binding var selectedStyle: LeaflektMapStyle

    @State private var position = LeaflektCameraPosition(target: kolkata, zoom: 12)
    @State private var controller: LeaflektMapController?
    @State private var searchExpanded = false
    @State private var showStyleSheet = false

    private let selectedMarkerId = "explore-selected"

    var body: some View {
        ZStack(alignment: .top) {
            mapLayer
                .ignoresSafeArea(edges: .top)
            
            VStack(spacing: 0) {
                ExploreSearchBar(
                    isExpanded: $searchExpanded,
                    query: Binding(
                        get: { vm.exploreQuery },
                        set: { vm.onExploreQueryChange($0) }
                    ),
                    predictions: vm.explorePredictions,
                    isLoading: vm.isExploreLoading,
                    onSelect: { pred in
                        vm.selectExplorePrediction(pred)
                        searchExpanded = false
                    },
                    onClear: vm.clearExploreSearch
                )

                Spacer()
                HStack {
                    Spacer()
                    VStack(spacing: 10) {
                        MapFAB(systemImage: "square.3.layers.3d") { showStyleSheet = true }
                        MapFAB(systemImage: "location") {
                            controller?.moveCamera(
                                lat: position.target.latitude,
                                lng: position.target.longitude,
                                zoom: max(position.zoom, 16)
                            )
                        }
                    }
                    .padding(.trailing, 16)
                    .padding(.bottom, 90)
                }
            }
        }
        .sheet(isPresented: $showStyleSheet) {
            MapStylePickerSheet(selectedStyle: $selectedStyle) { showStyleSheet = false }
        }
        .onChange(of: vm.selectedExplorePlace) { place in
            guard let loc = place?.geometry?.location else { return }
            controller?.removeMarker(id: selectedMarkerId)
            controller?.addMarker(LeaflektMarker(
                id: selectedMarkerId,
                position: loc.latLng,
                title: place?.headline()
            ))
            withAnimation {
                position = LeaflektCameraPosition(target: loc.latLng, zoom: 15)
            }
        }
    }

    private var mapLayer: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(mapStyle: selectedStyle))
            .uiSettings(LeaflektMapUiSettings(zoomControlsEnabled: false))
            .onReady { ctrl in controller = ctrl }
            .onMapClick { _ in controller?.removeMarker(id: selectedMarkerId) }
    }
}

// MARK: - Directions

struct DirectionsMapScreen: View {
    @ObservedObject var vm: SampleViewModel
    @Binding var selectedStyle: LeaflektMapStyle

    @State private var position = LeaflektCameraPosition(target: kolkata, zoom: 12)
    @State private var controller: LeaflektMapController?
    @State private var showPicker = false
    @State private var showStyleSheet = false
    @State private var cardExpanded = false

    private let routePolylineId = "directions-route"
    private let originMarkerId = "directions-origin"
    private let destMarkerId = "directions-destination"

    var body: some View {
        ZStack(alignment: .top) {
            mapLayer
                .ignoresSafeArea(edges: .top)
            VStack(spacing: 0) {
                DirectionsPlacesCard(
                    vm: vm,
                    isExpanded: $cardExpanded,
                    onPickOrigin: { vm.beginDirectionsSearch(.origin); showPicker = true },
                    onPickDestination: { vm.beginDirectionsSearch(.destination); showPicker = true }
                )
                .padding(.top, 8)
                .padding(.horizontal, 16)
                Spacer()
                HStack {
                    Spacer()
                    VStack(spacing: 10) {
                        MapFAB(systemImage: "square.3.layers.3d") { showStyleSheet = true }
                        MapFAB(systemImage: "location") {
                            controller?.moveCamera(
                                lat: position.target.latitude,
                                lng: position.target.longitude,
                                zoom: max(position.zoom, 16)
                            )
                        }
                    }
                    .padding(.trailing, 16)
                    .padding(.bottom, 90)
                }
            }
        }
        .sheet(isPresented: $showPicker) {
            PlacePickerSheet(
                title: vm.activeEndpoint == .origin ? "Choose origin" : "Choose destination",
                query: Binding(
                    get: { vm.directionsQuery },
                    set: { vm.onDirectionsQueryChange($0) }
                ),
                predictions: vm.directionsPredictions,
                isLoading: vm.isDirectionsLoading,
                onSelect: { pred in vm.selectDirectionsPrediction(pred); showPicker = false },
                onDismiss: { vm.clearDirectionsSearch(); showPicker = false }
            )
        }
        .sheet(isPresented: $showStyleSheet) {
            MapStylePickerSheet(selectedStyle: $selectedStyle) { showStyleSheet = false }
        }
        .onChange(of: vm.originPlace) { place in
            updateOriginMarker(place)
            moveCameraToPlaces()
        }
        .onChange(of: vm.destinationPlace) { place in
            updateDestMarker(place)
            moveCameraToPlaces()
        }
        .onChange(of: vm.activeRoute) { route in
            controller?.removePolyline(id: routePolylineId)
            if let route = route {
                controller?.addPolyline(LeaflektPolyline(
                    id: routePolylineId,
                    points: route.points,
                    color: .systemBlue,
                    width: 8
                ))
                withAnimation {
                    position = LeaflektCameraPosition(target: route.cameraTarget(), zoom: route.recommendedZoom())
                }
            }
        }
    }

    private var mapLayer: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(mapStyle: selectedStyle, geoJsonOverlay: .india))
            .uiSettings(LeaflektMapUiSettings(zoomControlsEnabled: false))
            .onReady { ctrl in controller = ctrl }
            .onMapClick { _ in }
    }

    private func updateOriginMarker(_ place: PlaceDetails?) {
        controller?.removeMarker(id: originMarkerId)
        guard let loc = place?.geometry?.location else { return }
        controller?.addMarker(LeaflektMarker(id: originMarkerId, position: loc.latLng, title: "Origin"))
    }

    private func updateDestMarker(_ place: PlaceDetails?) {
        controller?.removeMarker(id: destMarkerId)
        guard let loc = place?.geometry?.location else { return }
        controller?.addMarker(LeaflektMarker(id: destMarkerId, position: loc.latLng, title: "Destination"))
    }

    private func moveCameraToPlaces() {
        guard vm.activeRoute == nil else { return }
        let o = vm.originPlace?.geometry?.location
        let d = vm.destinationPlace?.geometry?.location
        let target: LeaflektLatLng
        if let o, let d {
            target = LeaflektLatLng(latitude: (o.lat + d.lat) / 2, longitude: (o.lng + d.lng) / 2)
        } else if let o {
            target = LeaflektLatLng(latitude: o.lat, longitude: o.lng)
        } else if let d {
            target = LeaflektLatLng(latitude: d.lat, longitude: d.lng)
        } else {
            return
        }
        withAnimation { position = LeaflektCameraPosition(target: target, zoom: 12.5) }
    }
}

// MARK: - Clustering

struct ClusteringMapScreen: View {
    @ObservedObject var vm: SampleViewModel
    @Binding var selectedStyle: LeaflektMapStyle

    @State private var position = LeaflektCameraPosition(target: kolkata, zoom: 11)
    @State private var controller: LeaflektMapController?
    @State private var searchExpanded = false
    @State private var showStyleSheet = false
    @State private var markersAdded = false

    private let clusterPoints: [LeaflektLatLng] = {
        var rng = SystemRandomNumberGenerator()
        return (0..<100).map { _ in
            LeaflektLatLng(
                latitude: 22.5726 + Double.random(in: -0.1...0.1, using: &rng),
                longitude: 88.3639 + Double.random(in: -0.1...0.1, using: &rng)
            )
        }
    }()

    var body: some View {
        ZStack(alignment: .top) {
            mapLayer
                .ignoresSafeArea(edges: .top)
            VStack(spacing: 0) {
                ExploreSearchBar(
                    isExpanded: $searchExpanded,
                    query: Binding(
                        get: { vm.exploreQuery },
                        set: { vm.onExploreQueryChange($0) }
                    ),
                    predictions: vm.explorePredictions,
                    isLoading: vm.isExploreLoading,
                    onSelect: { pred in vm.selectExplorePrediction(pred); searchExpanded = false },
                    onClear: vm.clearExploreSearch
                )
                HStack {
                    Spacer()
                    Text("Marker Clustering: 100 points")
                        .font(.callout.weight(.medium))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 12))
                    Spacer()
                }
                .padding(.top, 8)
                Spacer()
                HStack {
                    Spacer()
                    VStack(spacing: 10) {
                        MapFAB(systemImage: "square.3.layers.3d") { showStyleSheet = true }
                        MapFAB(systemImage: "location") {
                            controller?.moveCamera(
                                lat: position.target.latitude,
                                lng: position.target.longitude,
                                zoom: max(position.zoom, 16)
                            )
                        }
                    }
                    .padding(.trailing, 16)
                    .padding(.bottom, 90)
                }
            }
        }
        .sheet(isPresented: $showStyleSheet) {
            MapStylePickerSheet(selectedStyle: $selectedStyle) { showStyleSheet = false }
        }
        .onChange(of: vm.selectedExplorePlace) { place in
            guard let loc = place?.geometry?.location else { return }
            withAnimation { position = LeaflektCameraPosition(target: loc.latLng, zoom: 15) }
        }
    }

    private var mapLayer: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(mapStyle: selectedStyle))
            .uiSettings(LeaflektMapUiSettings(zoomControlsEnabled: false))
            .onReady { ctrl in
                controller = ctrl
                guard !markersAdded else { return }
                markersAdded = true
                ctrl.createClusterGroup(groupId: "native-cluster", maxClusterRadius: 80)
                let markers = clusterPoints.enumerated().map { i, pt in
                    LeaflektMarker(id: "cluster-\(i)", position: pt, title: "Marker #\(i)")
                }
                ctrl.addMarkersToCluster(groupId: "native-cluster", markers: markers)
            }
    }
}

// MARK: - DirectionsPlacesCard

private struct DirectionsPlacesCard: View {
    @ObservedObject var vm: SampleViewModel
    @Binding var isExpanded: Bool
    let onPickOrigin: () -> Void
    let onPickDestination: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Directions")
                    .font(.headline)
                Spacer()
                Button {
                    withAnimation(.spring(response: 0.3)) { isExpanded.toggle() }
                } label: {
                    Image(systemName: isExpanded ? "eye.slash" : "eye")
                        .frame(width: 36, height: 36)
                }
                .buttonStyle(.plain)
            }

            if isExpanded {
                DirectionsPlaceButton(
                    label: "From",
                    place: vm.originPlace,
                    onTap: onPickOrigin,
                    onClear: { vm.clearDirectionsPlace(.origin) }
                )
                DirectionsPlaceButton(
                    label: "To",
                    place: vm.destinationPlace,
                    onTap: onPickDestination,
                    onClear: { vm.clearDirectionsPlace(.destination) }
                )

                HStack(spacing: 10) {
                    Button("Swap") { vm.swapDirectionsPlaces() }
                        .buttonStyle(.bordered)
                        .disabled(vm.originPlace == nil && vm.destinationPlace == nil)
                        .frame(maxWidth: .infinity)

                    Button(vm.isRouteLoading ? "Loading…" : "Refresh route") {
                        vm.refreshRouteIfPossible()
                    }
                    .buttonStyle(.bordered)
                    .disabled(vm.originPlace == nil || vm.destinationPlace == nil || vm.isRouteLoading)
                    .frame(maxWidth: .infinity)
                }

                if let route = vm.activeRoute {
                    let info = [route.distanceLabel(), route.durationLabel()].compactMap { $0 }.joined(separator: " | ")
                    VStack(alignment: .leading, spacing: 4) {
                        Text(route.summary ?? "Route ready")
                            .font(.callout.weight(.semibold))
                            .foregroundStyle(.tint)
                        if !info.isEmpty {
                            Text(info).font(.subheadline).foregroundStyle(.secondary)
                        }
                    }
                    .padding(10)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(.secondarySystemFill), in: RoundedRectangle(cornerRadius: 14))
                }

                if let err = vm.routeErrorMessage {
                    Text(err).font(.caption).foregroundStyle(.red)
                }
            }
        }
        .padding(16)
        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 22))
    }
}

private struct DirectionsPlaceButton: View {
    let label: String
    let place: PlaceDetails?
    let onTap: () -> Void
    let onClear: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text(label).font(.caption2).foregroundStyle(.tint)
                    Text(place?.headline() ?? "Search \(label)")
                        .font(.subheadline.weight(.medium))
                        .foregroundStyle(place != nil ? .primary : .secondary)
                        .lineLimit(1)
                }
                Spacer()
                if place != nil {
                    Button(action: onClear) {
                        Image(systemName: "xmark.circle.fill").foregroundStyle(.secondary)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .background(Color(.secondarySystemFill), in: RoundedRectangle(cornerRadius: 16))
        }
        .buttonStyle(.plain)
    }
}

// MARK: - ExploreSearchBar (inline expandable, mirrors Android SearchBar)

private struct ExploreSearchBar: View {
    @Binding var isExpanded: Bool
    @Binding var query: String
    let predictions: [Prediction]
    let isLoading: Bool
    let onSelect: (Prediction) -> Void
    let onClear: () -> Void

    @FocusState private var focused: Bool

    var body: some View {
        VStack(spacing: 0) {
            HStack(spacing: 10) {
                Image(systemName: "magnifyingglass").foregroundStyle(.secondary)

                if isExpanded {
                    TextField("Search places…", text: $query)
                        .focused($focused)
                        .autocorrectionDisabled()
                } else {
                    Text(query.isEmpty ? "Search places…" : query)
                        .foregroundStyle(query.isEmpty ? .secondary : .primary)
                        .lineLimit(1)
                }

                Spacer()

                if isLoading {
                    ProgressView().scaleEffect(0.8)
                } else if isExpanded {
                    Button("Cancel") {
                        onClear()
                        query = ""
                        isExpanded = false
                        focused = false
                    }
                    .transition(.opacity)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(.regularMaterial, in: RoundedRectangle(cornerRadius: isExpanded ? 0 : 12))
            .padding(.horizontal, isExpanded ? 0 : 16)
            .padding(.top, isExpanded ? 0 : 8)
            .contentShape(Rectangle())
            .onTapGesture {
                guard !isExpanded else { return }
                withAnimation(.easeInOut(duration: 0.2)) { isExpanded = true }
                focused = true
            }

            if isExpanded {
                predictionsList
            }
        }
        .animation(.easeInOut(duration: 0.2), value: isExpanded)
        .onChange(of: isExpanded) { expanded in
            if expanded { focused = true }
        }
    }

    private var predictionsList: some View {
        ScrollView {
            LazyVStack(spacing: 0) {
                if predictions.isEmpty && query.count > 2 && !isLoading {
                    Text("No results")
                        .foregroundStyle(.secondary)
                        .padding()
                        .frame(maxWidth: .infinity)
                } else {
                    ForEach(predictions) { pred in
                        Button {
                            onSelect(pred)
                        } label: {
                            HStack(spacing: 12) {
                                Image(systemName: "mappin").foregroundStyle(.tint)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(pred.structuredFormatting?.mainText ?? pred.description)
                                        .font(.subheadline.weight(.medium))
                                        .foregroundStyle(.primary)
                                        .lineLimit(1)
                                    if let sub = pred.structuredFormatting?.secondaryText, !sub.isEmpty {
                                        Text(sub).font(.caption).foregroundStyle(.secondary).lineLimit(1)
                                    }
                                }
                                Spacer()
                            }
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)
                        }
                        .buttonStyle(.plain)
                        Divider().padding(.leading, 48)
                    }
                }
            }
        }
        .frame(maxHeight: 320)
        .background(.regularMaterial)
    }
}

// MARK: - PlacePickerSheet

private struct PlacePickerSheet: View {
    let title: String
    @Binding var query: String
    let predictions: [Prediction]
    let isLoading: Bool
    let onSelect: (Prediction) -> Void
    let onDismiss: () -> Void

    @FocusState private var focused: Bool

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                HStack {
                    Image(systemName: "magnifyingglass").foregroundStyle(.secondary)
                    TextField("Search places…", text: $query)
                        .focused($focused)
                        .autocorrectionDisabled()
                    if isLoading {
                        ProgressView().scaleEffect(0.8)
                    } else if !query.isEmpty {
                        Button { query = "" } label: {
                            Image(systemName: "xmark.circle.fill").foregroundStyle(.secondary)
                        }
                    }
                }
                .padding(12)
                .background(Color(.secondarySystemFill), in: RoundedRectangle(cornerRadius: 12))
                .padding(.horizontal, 16)
                .padding(.vertical, 8)

                List(predictions) { pred in
                    Button {
                        onSelect(pred)
                    } label: {
                        VStack(alignment: .leading, spacing: 2) {
                            Text(pred.structuredFormatting?.mainText ?? pred.description)
                                .font(.subheadline.weight(.medium))
                                .foregroundStyle(.primary)
                                .lineLimit(1)
                            if let sub = pred.structuredFormatting?.secondaryText, !sub.isEmpty {
                                Text(sub).font(.caption).foregroundStyle(.secondary).lineLimit(1)
                            }
                        }
                    }
                }
                .listStyle(.plain)

                if predictions.isEmpty && query.count > 2 && !isLoading {
                    Text("No results").foregroundStyle(.secondary).padding()
                    Spacer()
                }
            }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel", action: onDismiss)
                }
            }
        }
        .navigationViewStyle(.stack)
        .onAppear { focused = true }
    }
}

// MARK: - MapStylePickerSheet

struct MapStylePickerSheet: View {
    @Binding var selectedStyle: LeaflektMapStyle
    let onDismiss: () -> Void

    private let styles: [LeaflektMapStyle] = [
        .openStreetMap, .cartoLight, .cartoDark, .openTopoMap, .esriWorldImagery,
        .openFreeMapLiberty, .openFreeMapFiord, .openFreeMapBright
    ]

    var body: some View {
        NavigationView {
            List(styles, id: \.rawValue) { style in
                Button {
                    selectedStyle = style
                    onDismiss()
                } label: {
                    HStack {
                        Text(style.displayName).foregroundStyle(.primary)
                        Spacer()
                        if style == selectedStyle {
                            Image(systemName: "checkmark").foregroundStyle(.tint)
                        }
                    }
                }
            }
            .navigationTitle("Map Style")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Done", action: onDismiss)
                }
            }
        }
        .navigationViewStyle(.stack)
        .modifier(MediumDetentModifier())
    }
}

// MARK: - Shared helpers

private struct MapFAB: View {
    let systemImage: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: systemImage)
                .frame(width: 50, height: 50)
                .background(.ultraThinMaterial, in: Circle())
        }
    }
}

private struct MediumDetentModifier: ViewModifier {
    func body(content: Content) -> some View {
        if #available(iOS 16, *) {
            content.presentationDetents([.medium])
        } else {
            content
        }
    }
}

extension LeaflektMapStyle {
    var displayName: String {
        switch rawValue {
        case "open_street_map": return "OpenStreetMap"
        case "carto_light": return "CARTO Light"
        case "carto_dark": return "CARTO Dark"
        case "open_topo_map": return "OpenTopoMap"
        case "esri_world_imagery": return "Esri World Imagery"
        case "openfreemap_liberty": return "OpenFreeMap Liberty"
        case "openfreemap_fiord": return "OpenFreeMap Fiord"
        case "openfreemap_bright": return "OpenFreeMap Bright"
        default: return rawValue.replacingOccurrences(of: "_", with: " ").capitalized
        }
    }
}
