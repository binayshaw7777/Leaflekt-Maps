# iOS Native (Swift Package Manager Guide)

LeafleKT provides a first-class native Swift Package `LeaflektMap` for iOS applications built with SwiftUI or UIKit.

---

## 📦 Installation via SPM

### Xcode Integration

1. In Xcode, open your project and go to **File → Add Package Dependencies...**
2. Paste the repository URL into the search bar:
   ```text
   https://github.com/binayshaw7777/LeafleKT
   ```
3. Set **Dependency Rule** to **Up to Next Major Version** starting at `1.1.0` (or exact version `1.1.0`).
4. Select **LeaflektMap** target and add it to your iOS app target.

---

## 📋 Requirements

| Requirement | Minimum Version |
| :--- | :--- |
| **iOS Version** | iOS 15.0+ |
| **Swift Tools** | Swift 5.7+ / Xcode 14+ |

---

## 🛠️ App Transport & Info.plist Setup

LeafleKT loads map tiles over HTTPS using `WKWebView`.

If your application uses user location features (`showCurrentLocation` or `myLocationButtonEnabled`), add location usage descriptions to `Info.plist`:

```xml
<key>NSLocationWhenInUseUsageDescription</key>
<string>We need your location to show your current position on the map.</string>
```

---

## 🚀 Declarative SwiftUI Integration

### Basic Map View

```swift
import SwiftUI
import LeaflektMap

struct ContentView: View {
    @State private var position = LeaflektCameraPosition(
        target: LeaflektLatLng(latitude: 22.5726, longitude: 88.3639),
        zoom: 12.0
    )

    var body: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(
                mapStyle: .openFreeMapBright,
                geoJsonOverlay: .india
            ))
            .uiSettings(LeaflektMapUiSettings(
                zoomControlsEnabled: true,
                scrollGesturesEnabled: true,
                zoomGesturesEnabled: true
            ))
            .onMapClick { latLng in
                print("Map tapped at: \(latLng.latitude), \(latLng.longitude)")
            }
            .onCameraIdle { position in
                print("Camera stopped moving at zoom level: \(position.zoom)")
            }
            .ignoresSafeArea()
    }
}
```

---

## 🎮 Imperative Controller API

Use `.onReady` to receive a `LeaflektController` instance for imperative operations:

```swift
struct ImperativeMapScreen: View {
    @State private var position = LeaflektCameraPosition(
        target: LeaflektLatLng(latitude: 28.6139, longitude: 77.2090),
        zoom: 10.0
    )
    @State private var controller: LeaflektController?

    var body: some View {
        LeaflektMapView(position: $position)
            .onReady { readyController in
                self.controller = readyController

                // Move & Animate Camera
                readyController.moveCamera(lat: 28.6139, lng: 77.2090, zoom: 14)
                readyController.setZoomBounds(min: 5, max: 18)

                // Add Marker
                readyController.addMarker(LeaflektMarker(
                    id: "delhi-pin",
                    position: LeaflektLatLng(latitude: 28.6139, longitude: 77.2090),
                    title: "New Delhi",
                    snippet = "Capital of India"
                ))

                // Add Route Polyline
                readyController.addPolyline(LeaflektPolyline(
                    id: "main-route",
                    points: routeCoordinates,
                    color: .blue,
                    width: 6
                ))

                // Add Geofence Circle
                readyController.addCircle(LeaflektCircle(
                    id: "geofence",
                    center: LeaflektLatLng(latitude: 28.6139, longitude: 77.2090),
                    radiusMeters: 1000,
                    strokeColor: .red,
                    fillColor: UIColor.red.withAlphaComponent(0.2),
                    strokeWidth: 3
                ))

                // Create Marker Cluster Group
                readyController.createClusterGroup(groupId: "delivery-pins", maxClusterRadius: 80)
                readyController.addMarkersToCluster(groupId: "delivery-pins", markers: deliveryMarkers)
            }
    }
}
```

---

## 🎨 Map Styles

Switch map visual themes instantly:

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

---

## 🖼️ Custom Marker Icons

Supply custom icons via remote HTTPS URLs or Base64 data URIs:

```swift
let customIcon = LeaflektMarkerIcon(
    dataUrl: "https://example.com/assets/pin.png",
    widthPx: 48,
    heightPx: 48,
    anchorFractionX: 0.5, // Center horizontally
    anchorFractionY: 1.0  // Bottom aligned
)

let customMarker = LeaflektMarker(
    id: "store-location",
    position: LeaflektLatLng(latitude: 22.5726, longitude: 88.3639),
    icon: customIcon
)
```

---

## 📱 iOS Sample App (`app-ios-native/`)

The native iOS sample app mirrors the Android & CMP demo experiences with full SwiftUI fidelity:

| Screen | Capabilities |
| :--- | :--- |
| **Explore** | Ola Maps autocomplete search, place pinning, dynamic tile provider picker sheet, and location button. |
| **Directions** | Origin & destination input cards, route calculation, interactive polyline overlay, swap endpoints. |
| **Clustering** | 100 random location markers grouped via `Leaflet.markercluster` with smooth zoom expansions. |

### Architecture Highlights

- `SampleView.swift` — Main TabView container hosting `ExploreMapScreen`, `DirectionsMapScreen`, `ClusteringMapScreen`.
- `SampleViewModel.swift` — `@MainActor ObservableObject` driving search suggestions, directions queries, and state.
- `OlaMapsService.swift` — Async/await API client handling HTTP calls to Ola Maps services.

