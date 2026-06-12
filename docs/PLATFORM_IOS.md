# iOS Native (Swift Package)

LeafleKT provides a native Swift Package `LeaflektMap` for iOS apps.

## Installation

In Xcode: **File → Add Package Dependencies**, paste:

```
https://github.com/binayshaw7777/LeafleKT
```

Select version `v1.0.0` → add `LeaflektMap` target to your app.

## Requirements

| Platform | Minimum |
|----------|---------|
| iOS | iOS 15+ |

## Basic Map

```swift
import SwiftUI
import LeaflektMap

struct ContentView: View {
    @State private var position = LeaflektCameraPosition(
        target: LeaflektLatLng(latitude: 22.5726, longitude: 88.3639),
        zoom: 12
    )

    var body: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(
                mapStyle: .cartoLight,
                geoJsonOverlay: .india
            ))
            .uiSettings(LeaflektMapUiSettings(
                zoomControlsEnabled: true,
                scrollGesturesEnabled: true,
                zoomGesturesEnabled: true
            ))
            .onMapClick { latLng in print("Tapped: \(latLng)") }
            .onCameraIdle { pos in print("Idle at zoom \(pos.zoom)") }
    }
}
```

## Imperative Controller

```swift
LeaflektMapView(position: $position)
    .onReady { controller in
        controller.moveCamera(lat: 28.6139, lng: 77.2090, zoom: 14)
        controller.setZoomBounds(min: 5, max: 18)

        controller.addMarker(LeaflektMarker(
            id: "delhi",
            position: LeaflektLatLng(latitude: 28.6139, longitude: 77.2090),
            title: "New Delhi"
        ))

        controller.addPolyline(LeaflektPolyline(
            id: "route", points: points,
            color: .blue, width: 6
        ))

        controller.addCircle(LeaflektCircle(
            id: "zone",
            center: LeaflektLatLng(latitude: 28.6139, longitude: 77.2090),
            radiusMeters: 500,
            strokeColor: .red,
            fillColor: UIColor.red.withAlphaComponent(0.2),
            strokeWidth: 3
        ))

        controller.createClusterGroup(groupId: "pins", maxClusterRadius: 80)
        controller.addMarkersToCluster(groupId: "pins", markers: myMarkers)
    }
```

## Map Styles

```swift
LeaflektMapStyle.openStreetMap
LeaflektMapStyle.cartoLight
LeaflektMapStyle.cartoDark
LeaflektMapStyle.openTopoMap
LeaflektMapStyle.esriWorldImagery
LeaflektMapStyle.openFreeMapLiberty
LeaflektMapStyle.openFreeMapFiord
LeaflektMapStyle.openFreeMapBright
```

## Custom Marker Icon

```swift
let icon = LeaflektMarkerIcon(
    dataUrl: "https://example.com/pin.png",
    widthPx: 48,
    heightPx: 48,
    anchorFractionX: 0.5,
    anchorFractionY: 1.0
)

controller.addMarker(LeaflektMarker(
    id: "custom",
    position: LeaflektLatLng(latitude: 22.5726, longitude: 88.3639),
    icon: icon
))
```

## Sample App (iOS)

The iOS sample app (`app-ios-native/`) mirrors the Android sample with three tabs:

| Tab | Features |
|-----|----------|
| **Explore** | Ola Maps autocomplete search, pin selected place, map style + location FABs |
| **Directions** | Origin/destination picker, route polyline, swap places, get route, distance/duration |
| **Clustering** | 100 random points clustered via Leaflet.markercluster |

Architecture:
- `SampleView.swift` — Tab UI with `ExploreMapScreen`, `DirectionsMapScreen`, `ClusteringMapScreen`
- `SampleViewModel.swift` — `@MainActor ObservableObject` with explore + directions state
- `OlaMapsService.swift` — Ola Maps API client (autocomplete, place details, directions)
- `SampleModels.swift` — Models + polyline decoder
- `LauncherView.swift` — Entry screen routing to Demo or Sample app
