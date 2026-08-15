

# LeafleKT 🗺️

**The Zero-Cost, Open-Source Map SDK for Kotlin Multiplatform & Native iOS**

LeafleKT brings the flexibility of Leaflet.js to **Compose Multiplatform (Android & iOS)** and **Native iOS (SwiftUI)** with zero external map SDK overhead, no required API keys, and no credit card lock-in.

<p align="center">
  <img src="https://github.com/user-attachments/assets/0d3d2a45-724d-4581-a4fd-5b53735b9f2f" alt="LeafleKT Hero Banner" width="100%" />
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.binayshaw7777/leaflekt-compose"><img src="https://img.shields.io/maven-central/v/io.github.binayshaw7777/leaflekt-compose?style=for-the-badge&color=2E7D32" alt="Maven Central" /></a>
  <a href="https://github.com/binayshaw7777/LeafleKT-Maps"><img src="https://img.shields.io/badge/Swift_Package-v1.1.0-F05138?style=for-the-badge&logo=swift&logoColor=white" alt="Swift Package" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache_2.0-007ACC?style=for-the-badge" alt="License" /></a>
  <a href="https://android-arsenal.com/api?level=21"><img src="https://img.shields.io/badge/Android-API_21%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android API" /></a>
  <a href="https://developer.apple.com/ios/"><img src="https://img.shields.io/badge/iOS-15.0%2B-000000?style=for-the-badge&logo=apple&logoColor=white" alt="iOS Version" /></a>
</p>

---

## ⚡ Why LeafleKT?

Traditional mobile map SDKs (Google Maps, Mapbox) charge per map load, require complex billing setups, and add megabytes of binary bloat to your mobile application. LeafleKT bridges Leaflet.js via high-performance native web runtimes (`WebView` / `WKWebView`) into declarative Jetpack Compose and SwiftUI APIs.

### 📊 Comparison at a Glance

| Feature | Google Maps SDK | Mapbox SDK | LeafleKT SDK |
| :--- | :---: | :---: | :---: |
| **SDK Usage Cost** | Pay per load ($7+/1k) | Pay per load ($5+/1k) | **100% Free & Open Source** |
| **API Key Needed** | Mandatory | Mandatory | **Optional (No key by default)** |
| **Credit Card Required** | Mandatory | Mandatory | **No Credit Card Required** |
| **Kotlin Multiplatform** | ❌ Android Only | ❌ Complex Bindings | **✅ Native KMP & Compose UI** |
| **iOS SwiftUI Support** | ⚠️ UIKit Wrapper | ⚠️ UIKit Wrapper | **✅ Native Swift Package & SwiftUI** |
| **Bundle Size Overhead** | ~15 MB | ~20 MB | **~100 KB** |
| **Tile Provider Choice** | Locked | Locked | **8 Built-in + Custom XYZ** |
| **License** | Proprietary | Proprietary | **Apache 2.0** |

---

## 🚀 Quick Start

### 1️⃣ Compose Multiplatform / Android Compose

Add dependency in your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-compose:1.1.1")
}
```

Display a declarative map:

```kotlin
@Composable
fun MapScreen() {
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(22.5726, 88.3639),
            zoom = 12.0
        )
    }

    LeaflektMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        properties = LeaflektMapProperties(mapStyle = LeaflektMapStyle.OpenFreeMapBright),
        uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = true)
    ) {
        LeaflektMarker(
            position = LeaflektLatLng(22.5726, 88.3639),
            title = "Kolkata",
            snippet = "City of Joy"
        )
    }
}
```

### 2️⃣ iOS Native (SwiftUI via Swift Package Manager)

In Xcode: **File → Add Package Dependencies**, enter repository URL:
`https://github.com/binayshaw7777/LeafleKT-Maps` (Version: `v1.1.0`)

```swift
import SwiftUI
import LeaflektMap

struct NativeMapScreen: View {
    @State private var position = LeaflektCameraPosition(
        target: LeaflektLatLng(latitude: 22.5726, longitude: 88.3639),
        zoom: 12
    )

    var body: some View {
        LeaflektMapView(position: $position)
            .mapProperties(LeaflektMapProperties(mapStyle: .openFreeMapBright))
            .uiSettings(LeaflektMapUiSettings(zoomControlsEnabled: true))
    }
}
```

---

## 📦 Modules

| Module | Coordinates / Artifact | Description | Supported Platforms |
| :--- | :--- | :--- | :--- |
| **Compose UI** | `io.github.binayshaw7777:leaflekt-compose:1.1.1` | Declarative Compose map UI components | Android, Compose Multiplatform (iOS) |
| **Core** | `io.github.binayshaw7777:leaflekt-core:1.1.1` | Headless engine, camera state, color system | Kotlin Multiplatform (Common, Android, iOS) |
| **iOS Swift Package** | `LeaflektMap` SPM Target | Native SwiftUI wrappers and controller API | iOS 15.0+ |

---

## ✨ Features & Capabilities

<p align="center">
  <img src="https://github.com/user-attachments/assets/4b0e8f82-1180-4bd0-b03c-a707ff990194" alt="Features Overview" width="100%" />
</p>

* **🎨 8 Built-in Map Styles:** Raster (OpenStreetMap, Carto Light/Dark, Topo, Esri Imagery) + Vector (OpenFreeMap Liberty, Fiord, Bright).
* **📍 Custom Markers & Icons:** Base64 encoded PNGs or remote HTTPS URLs with full anchor point, rotation, alpha, and click control.
* **⚡ Marker Clustering:** Fast client-side clustering for hundreds/thousands of markers powered by `Leaflet.markercluster`.
* **🛣️ Rich Vector Overlays:** Polylines with dash/dot patterns, Polygons with hole support, and Circles with meter-based radii.
* **🗺️ GeoJSON Support:** Render regional boundaries (built-in high-precision India boundary + custom GeoJSON strings).
* **🎯 Selection & Interactivity:** Built-in shape selection state highlights with customizable selection colors/widths.
* **🎮 Imperative Controller:** Direct control via `LeaflektController` to move camera, clear markers, switch styles, and execute JS at runtime.
* **🔋 RAM-Adaptive Buffering:** Auto-calculates off-screen tile buffer (`keepBuffer`) according to device RAM capacity.

---

## 🖼️ Visual Showcase

[![LeafleKT Video Demo](https://img.youtube.com/vi/Stokqv3e77w/maxresdefault.jpg)](https://youtu.be/Stokqv3e77w)

<table>
  <tr>
    <td align="center"><b>Map Styles Grid</b></td>
    <td align="center"><b>Marker Clustering</b></td>
  </tr>
  <tr>
    <td><img src="docs/assets/screenshots/map_styles_grid.png" width="380" /></td>
    <td><img src="docs/assets/gifs/clustering.png" width="380" /></td>
  </tr>
  <tr>
    <td align="center"><b>Camera Animations</b></td>
    <td align="center"><b>GeoJSON Boundaries</b></td>
  </tr>
  <tr>
    <td><img src="docs/assets/gifs/camera_animation.png" width="380" /></td>
    <td><img src="docs/assets/screenshots/geojson_india.png" width="380" /></td>
  </tr>
</table>

---

## ⚡ Performance & Benchmarks

Micro-benchmark results comparing `LeaflektMapJson` and `LeaflektScriptBuilder` optimizations against previous baseline:

| Benchmark Category | Operation | Optimized | Baseline | Improvement |
| :--- | :--- | :---: | :---: | :---: |
| **Markers** | `addMarkersScript` (10 markers) | **1,973 ns** | 5,670 ns | **65% faster** 🚀 |
| **Markers** | `addMarkersScript` (100 markers) | **8,973 ns** | 14,374 ns | **37% faster** 🚀 |
| **Markers** | `removeMarkersScript` (10 IDs) | **220 ns** | 473 ns | **53% faster** 🚀 |
| **Encoding** | `encodeString` (special characters) | **77 ns** | 333 ns | **4.3x faster** 🚀 |
| **Encoding** | `encodeLatLngList` (100 points) | **2,561 ns** | 4,701 ns | **45% faster** 🚀 |
| **Encoding** | `encodeLatLngList` (1000 points) | **26,081 ns** | 45,839 ns | **43% faster** 🚀 |
| **Clustering** | `addMarkersToClusterScript` (50 markers) | **4,043 ns** | 5,488 ns | **26% faster** 🚀 |
| **Vectors** | `addPolylineScript` (500 points) | **14,409 ns** | 24,053 ns | **40% faster** 🚀 |
| **Vectors** | `fitBoundsScript` | **221 ns** | 341 ns | **35% faster** 🚀 |

Key runtime optimizations include zero-allocation `StringBuilder` pre-allocation, single-pass character scanning for JS escaping, pre-warmed WKWebView instances on iOS, and lazy loading of MapLibre GL CSS/JS.

---

## 🚦 Platform Stability & Support Matrix

| Platform | Target Runtime | Status | Notes |
| :--- | :--- | :---: | :--- |
| **Android** | Jetpack Compose / WebView | ✅ **Stable** | Production ready (API 21+) |
| **iOS (Swift Package)** | SwiftUI / WKWebView | ✅ **Stable** | Production ready (iOS 15+) |
| **Compose Multiplatform (iOS)** | CMP / WKWebView | 🚧 **In Development** | Functional — known layout refinements in progress |

---

## 📄 Documentation & Links

* 📖 **[Full Online Documentation Site](https://binayshaw7777.github.io/Leaflekt-Maps/#/)**
* 📱 **[iOS SPM Setup Guide](docs/PLATFORM_IOS.md)**
* 🤖 **[CMP & Android Guide](docs/PLATFORM_CMP.md)**
* 📱 **[Sample Application Code](leaflektsampleapp/)**
* 🛣️ **[Project Roadmap](docs/ROADMAP.md)**
* ⚖️ **[Terms & Provider Attribution](docs/TERMS.md)**

---

## 📜 License & Attribution

Distributed under the **Apache 2.0 License**. See [`LICENSE`](LICENSE) for details.

> **Note on Map Tiles:** LeafleKT does not operate tile server infrastructure. Map tile data, rate limits, and attribution rules are set by tile providers (OpenStreetMap, Carto, OpenFreeMap, Esri). Ensure compliance with provider terms when building production apps. See [Terms & Attribution](docs/TERMS.md).
