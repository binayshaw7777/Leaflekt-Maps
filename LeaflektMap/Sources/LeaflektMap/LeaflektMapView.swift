import SwiftUI

public struct LeaflektMapView: View {
    @Binding public var position: LeaflektCameraPosition
    private var properties: LeaflektMapProperties = LeaflektMapProperties()
    private var uiSettings: LeaflektMapUiSettings = LeaflektMapUiSettings()
    private var onMapReady: (() -> Void)?
    private var onMapClick: ((LeaflektLatLng) -> Void)?
    private var onCameraIdle: ((LeaflektCameraPosition) -> Void)?
    private var onMarkerClick: ((String) -> Void)?
    private var onReady: ((LeaflektMapController) -> Void)?

    @StateObject private var controller = LeaflektMapControllerObject()

    public init(position: Binding<LeaflektCameraPosition>) {
        self._position = position
    }

    public var body: some View {
        LeaflektMapRepresentable(
            position: position,
            properties: properties,
            uiSettings: uiSettings,
            controller: controller.controller,
            onMapReady: {
                onMapReady?()
                onReady?(controller.controller)
            },
            onMapClick: onMapClick,
            onCameraIdle: { newPosition in
                position = newPosition
                onCameraIdle?(newPosition)
            },
            onMarkerClick: onMarkerClick
        )
        .ignoresSafeArea()
        .onAppear {
            // Re-invalidate after layout settles — handles TabView pre-created WKWebViews
            // that were sized to zero before the tab was first selected.
            let mapController = controller.controller
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) {
                mapController.invalidateSize()
            }
        }
    }

    public func mapProperties(_ properties: LeaflektMapProperties) -> Self {
        var copy = self; copy.properties = properties; return copy
    }

    public func uiSettings(_ settings: LeaflektMapUiSettings) -> Self {
        var copy = self; copy.uiSettings = settings; return copy
    }

    public func onMapClick(_ handler: @escaping (LeaflektLatLng) -> Void) -> Self {
        var copy = self; copy.onMapClick = handler; return copy
    }

    public func onMapReady(_ handler: @escaping () -> Void) -> Self {
        var copy = self; copy.onMapReady = handler; return copy
    }

    public func onCameraIdle(_ handler: @escaping (LeaflektCameraPosition) -> Void) -> Self {
        var copy = self; copy.onCameraIdle = handler; return copy
    }

    public func onMarkerClick(_ handler: @escaping (String) -> Void) -> Self {
        var copy = self; copy.onMarkerClick = handler; return copy
    }

    public func onReady(_ handler: @escaping (LeaflektMapController) -> Void) -> Self {
        var copy = self; copy.onReady = handler; return copy
    }
}

final class LeaflektMapControllerObject: ObservableObject {
    let controller = LeaflektMapController()
}
