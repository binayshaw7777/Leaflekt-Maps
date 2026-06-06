import SwiftUI
import UIKit
import LeaflektMap

// MARK: - State

private enum Layer: String, CaseIterable, Identifiable {
    case marker = "Marker"
    case polyline = "Polyline"
    case polygon = "Polygon"
    case circle = "Circle"
    var id: String { rawValue }
}

private let kolkata = LeaflektLatLng(latitude: 22.5726, longitude: 88.3639)
private let delhi   = LeaflektLatLng(latitude: 28.6139, longitude: 77.2090)
private let mumbai  = LeaflektLatLng(latitude: 19.0760, longitude: 72.8777)
private let chennai = LeaflektLatLng(latitude: 13.0827, longitude: 80.2707)

// MARK: - Root View

struct ContentView: View {
    @State private var position = LeaflektCameraPosition(target: kolkata, zoom: 6)
    @State private var controller: LeaflektMapController?
    @State private var selectedStyle: LeaflektMapStyle = .openStreetMap
    @State private var lastTap: String = ""
    @State private var cameraInfo: String = ""
    @State private var showControls = false
    @State private var circleRadius: Double = 100_000
    @State private var zoomLevel: Double = 6

    @State private var showMarker   = true
    @State private var showPolyline = true
    @State private var showPolygon  = true
    @State private var showCircle   = true

    var body: some View {
        ZStack(alignment: .top) {
            mapLayer
            VStack(spacing: 0) {
                statusCard
                Spacer()
                controlsOverlay
            }
        }
        .ignoresSafeArea(edges: .top)
        .onChange(of: selectedStyle)  { style  in controller?.setMapStyle(style) }
        .onChange(of: circleRadius)   { radius in updateCircle(radius: radius) }
        .onChange(of: showMarker)     { show in toggleLayer(.marker,   show: show) }
        .onChange(of: showPolyline)   { show in toggleLayer(.polyline, show: show) }
        .onChange(of: showPolygon)    { show in toggleLayer(.polygon,  show: show) }
        .onChange(of: showCircle)     { show in toggleLayer(.circle,   show: show) }
    }

    // MARK: - Map

    private var mapLayer: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(mapStyle: selectedStyle))
            .uiSettings(LeaflektMapUiSettings(zoomControlsEnabled: true))
            .onReady { ctrl in
                controller = ctrl
                setupShapes(ctrl)
            }
            .onMapClick { latLng in
                lastTap = fmt(latLng.latitude) + ", " + fmt(latLng.longitude)
            }
            .onMarkerClick { id in lastTap = "Marker: \(id)" }
            .onCameraIdle { pos in
                cameraInfo = fmt(pos.target.latitude) + ", " + fmt(pos.target.longitude) + " z" + String(Int(pos.zoom))
                zoomLevel = Double(pos.zoom)
            }
            .ignoresSafeArea()
    }

    // MARK: - Status Card

    private var statusCard: some View {
        VStack(alignment: .leading, spacing: 4) {
            Label(selectedStyle.rawValue.replacingOccurrences(of: "_", with: " ").capitalized, systemImage: "map")
                .font(.caption.weight(.semibold))
            if !lastTap.isEmpty {
                Label(lastTap, systemImage: "hand.tap")
                    .font(.caption)
            }
            if !cameraInfo.isEmpty {
                Label(cameraInfo, systemImage: "camera")
                    .font(.caption)
            }
        }
        .padding(10)
        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal, 12)
        .padding(.top, 56)
    }

    // MARK: - Controls Overlay

    private var controlsOverlay: some View {
        VStack(spacing: 0) {
            if showControls {
                controlsPanel
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
            HStack {
                Spacer()
                Button {
                    withAnimation(.spring()) { showControls.toggle() }
                } label: {
                    Image(systemName: showControls ? "chevron.down" : "slider.horizontal.3")
                        .font(.system(size: 18, weight: .semibold))
                        .frame(width: 48, height: 48)
                        .background(.ultraThinMaterial, in: Circle())
                }
                .padding(.trailing, 16)
                .padding(.bottom, 8)
            }
        }
        .padding(.bottom, 24)
    }

    private var controlsPanel: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {

                // Layer toggles
                sectionHeader("Layers")
                ForEach(Layer.allCases) { layer in
                    Toggle(layer.rawValue, isOn: binding(for: layer))
                }

                Divider()

                // Show / hide all
                HStack(spacing: 12) {
                    Button("Show All") { setAllLayers(true) }
                        .buttonStyle(.borderedProminent)
                    Button("Hide All") { setAllLayers(false) }
                        .buttonStyle(.bordered)
                }

                Divider()

                // Map style
                sectionHeader("Map Style")
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(LeaflektMapStyle.allCases, id: \.rawValue) { style in
                            styleChip(style)
                        }
                    }
                }

                Divider()

                // Zoom
                sectionHeader("Zoom: \(Int(zoomLevel))")
                Slider(value: $zoomLevel, in: 1...19, step: 1) { _ in
                    controller?.moveCamera(lat: position.target.latitude, lng: position.target.longitude, zoom: zoomLevel)
                }

                Divider()

                // Circle radius
                sectionHeader("Circle Radius: \(Int(circleRadius / 1000)) km")
                Slider(value: $circleRadius, in: 10_000...500_000, step: 10_000)

                Divider()

                // Camera buttons
                sectionHeader("Navigate To")
                HStack(spacing: 8) {
                    cameraButton("Kolkata", kolkata)
                    cameraButton("Delhi",   delhi)
                    cameraButton("Mumbai",  mumbai)
                    cameraButton("Chennai", chennai)
                }
            }
            .padding(16)
        }
        .frame(maxHeight: 420)
        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 20, style: .continuous))
        .padding(.horizontal, 12)
    }

    // MARK: - Helpers

    private func sectionHeader(_ text: String) -> some View {
        Text(text).font(.subheadline.weight(.semibold))
    }

    private func styleChip(_ style: LeaflektMapStyle) -> some View {
        let label = style.rawValue.replacingOccurrences(of: "_", with: " ").capitalized
        let selected = style == selectedStyle
        return Button(label) { selectedStyle = style }
            .font(.caption.weight(selected ? .semibold : .regular))
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(selected ? Color.accentColor : Color(.systemGray5), in: Capsule())
            .foregroundStyle(selected ? .white : .primary)
    }

    private func cameraButton(_ label: String, _ target: LeaflektLatLng) -> some View {
        Button(label) {
            controller?.moveCamera(lat: target.latitude, lng: target.longitude, zoom: 10)
        }
        .font(.caption)
        .buttonStyle(.bordered)
    }

    private func binding(for layer: Layer) -> Binding<Bool> {
        switch layer {
        case .marker:   return $showMarker
        case .polyline: return $showPolyline
        case .polygon:  return $showPolygon
        case .circle:   return $showCircle
        }
    }

    private func setAllLayers(_ show: Bool) {
        showMarker = show; showPolyline = show; showPolygon = show; showCircle = show
    }

    // MARK: - Shape Setup

    private func setupShapes(_ ctrl: LeaflektMapController) {
        if showMarker {
            ctrl.addMarker(LeaflektMarker(id: "home", position: kolkata, title: "Kolkata"))
        }
        if showPolyline {
            ctrl.addPolyline(LeaflektPolyline(
                id: "route",
                points: [kolkata, delhi, mumbai, chennai],
                color: .systemBlue,
                width: 4
            ))
        }
        if showPolygon {
            ctrl.addPolygon(LeaflektPolygon(
                id: "region",
                points: [
                    LeaflektLatLng(latitude: 24.0, longitude: 85.0),
                    LeaflektLatLng(latitude: 24.0, longitude: 92.0),
                    LeaflektLatLng(latitude: 20.0, longitude: 92.0),
                    LeaflektLatLng(latitude: 20.0, longitude: 85.0)
                ],
                strokeColor: .systemRed,
                fillColor: UIColor.systemRed.withAlphaComponent(0.25)
            ))
        }
        if showCircle {
            ctrl.addCircle(LeaflektCircle(
                id: "zone",
                center: kolkata,
                radiusMeters: circleRadius,
                strokeColor: .systemGreen,
                fillColor: UIColor.systemGreen.withAlphaComponent(0.2)
            ))
        }
    }

    private func toggleLayer(_ layer: Layer, show: Bool) {
        guard let ctrl = controller else { return }
        switch layer {
        case .marker:
            if show {
                ctrl.addMarker(LeaflektMarker(id: "home", position: kolkata, title: "Kolkata"))
            } else {
                ctrl.removeMarker(id: "home")
            }
        case .polyline:
            if show {
                ctrl.addPolyline(LeaflektPolyline(
                    id: "route",
                    points: [kolkata, delhi, mumbai, chennai],
                    color: .systemBlue, width: 4
                ))
            } else {
                ctrl.removePolyline(id: "route")
            }
        case .polygon:
            if show {
                ctrl.addPolygon(LeaflektPolygon(
                    id: "region",
                    points: [
                        LeaflektLatLng(latitude: 24.0, longitude: 85.0),
                        LeaflektLatLng(latitude: 24.0, longitude: 92.0),
                        LeaflektLatLng(latitude: 20.0, longitude: 92.0),
                        LeaflektLatLng(latitude: 20.0, longitude: 85.0)
                    ],
                    strokeColor: .systemRed,
                    fillColor: UIColor.systemRed.withAlphaComponent(0.25)
                ))
            } else {
                ctrl.removePolygon(id: "region")
            }
        case .circle:
            if show {
                ctrl.addCircle(LeaflektCircle(
                    id: "zone", center: kolkata,
                    radiusMeters: circleRadius,
                    strokeColor: .systemGreen,
                    fillColor: UIColor.systemGreen.withAlphaComponent(0.2)
                ))
            } else {
                ctrl.removeCircle(id: "zone")
            }
        }
    }

    private func updateCircle(radius: Double) {
        guard showCircle else { return }
        controller?.updateCircle(LeaflektCircle(
            id: "zone", center: kolkata,
            radiusMeters: radius,
            strokeColor: .systemGreen,
            fillColor: UIColor.systemGreen.withAlphaComponent(0.2)
        ))
    }

    private func fmt(_ value: Double) -> String {
        String(format: "%.4f", value)
    }
}
