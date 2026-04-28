# LeafleKT

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.

<img src="https://github.com/user-attachments/assets/ac67880c-9258-4b8b-be38-10ffd0a3788c"
     style="max-width:100%; height:auto;" />

[![JitPack](https://jitpack.io/v/binayshaw7777/LeafleKT-Maps.svg)](https://jitpack.io/#binayshaw7777/LeafleKT-Maps)
[![Android API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
![Build Status](https://github.com/binayshaw7777/LeafleKT-Maps/actions/workflows/release-master.yml/badge.svg)

**LeafleKT** is a high-performance, Compose-first Android wrapper around [Leaflet.js](https://leafletjs.com/). It brings the power of the web's most popular mapping engine into native Jetpack Compose, offering a stable state-driven Kotlin API while keeping the runtime entirely self-contained.

---

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.
## Status

- Map rendering in Compose
- Camera state with Rotation & Tilt support
- Dynamic Map style switching & Theme Sync
- India boundary overlay (Optimized GeoJSON)
- Declarative markers, polylines, polygons, and circles
- Advanced Marker Clustering
- Ola Maps integration (Tiles & Places)
- JitPack publication setup
## Status
- Map rendering in Compose
- Camera state
- Map style switching
- India boundary overlay
- Declarative markers
- Declarative polylines
- Declarative polygons
- Declarative circles
- JitPack publication setup

## Install
- Map rendering in Compose
- Camera state with Rotation & Tilt support
- Dynamic Map style switching & Theme Sync
- India boundary overlay (Optimized GeoJSON)
- Declarative markers, polylines, polygons, and circles
- Advanced Marker Clustering
- Ola Maps integration (Tiles & Places)
- JitPack publication setup
## Install

Add JitPack:
Add JitPack:
## Install

Add JitPack:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

Add the library:

```kotlin
dependencies {
    implementation("com.github.binayshaw7777.LeafleKT:leaflekt:0.5.0")
}
```

## Requirements

- Android API 21+
- Kotlin
- Jetpack Compose

## Quick Start

```kotlin
@Composable
fun SampleMap() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            target = LatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }

    MapView(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyle = MapStyle.OpenStreetMap,
            automaticThemeSync = true // Sync with System Light/Dark mode
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            rotateGesturesEnabled = true // Enable map rotation
        ),
        onMapClick = { latLng ->
            Log.d("LeafleKT", "Map click: $latLng")
        }
    ) {
        Marker(
            position = LatLng(22.5726, 88.3639),
            title = "Kolkata",
            rotationDegrees = 45f // Rotate marker clockwise
        )

        Polyline(
            points = listOf(
                LatLng(22.5726, 88.3639),
                LatLng(22.5826, 88.3939)
            ),
            color = Color(0xFF0B6E4F),
            width = 6f
        )
        
        // ... Polygons, Circles, and more
    }
}
```

## API Reference

### MapView

The primary container for the map.

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `modifier` | `Modifier` | Layout modifier for the map. |
| `cameraPositionState` | `CameraPositionState` | Hoisted state for camera control. |
| `properties` | `MapProperties` | Map configuration (styles, zoom limits). |
| `uiSettings` | `MapUiSettings` | UI and gesture toggles. |
| `onMapLoaded` | `(() -> Unit)?` | Called when Leaflet is initialized. |
| `onMapClick` | `((LatLng) -> Unit)?` | Called when the map is tapped. |
| `onMarkerClick` | `((String) -> Unit)?` | Called when any marker is clicked. |
| `content` | `@Composable () -> Unit` | Map children (Markers, Shapes, etc). |

### Marker

Standard marker with bitmap icons or default Leaflet pins.

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `state` | `MarkerState` | `remember...` | Hoisted state for position. |
| `title` | `String?` | `null` | Title for default popup. |
| `snippet` | `String?` | `null` | Secondary text for default popup. |
| `icon` | `MarkerIcon?` | `null` | Custom bitmap icon. |
| `rotationDegrees`| `Float` | `0f` | Visual rotation of the marker. |
| `visible` | `Boolean` | `true` | Visibility toggle. |
| `alpha` | `Float` | `1.0f` | Opacity (0.0 to 1.0). |
| `zIndex` | `Float` | `0f` | Drawing order (higher is on top). |
| `infoWindow` | `@Composable () -> Unit`| `null` | Custom Compose UI popup. |
| `onClick` | `() -> Boolean` | `{ false }` | Click handler (return true to consume). |

### Marker (Compose Icon)

Marker where the icon itself is a Compose UI.

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `state` | `MarkerState` | `remember...` | Hoisted state for position. |
| `iconContent` | `@Composable () -> Unit`| - | Compose UI for the marker icon. |
| `iconAnchorX` | `Float` | `0.5f` | Horizontal anchor (0 to 1). |
| `iconAnchorY` | `Float` | `0.5f` | Vertical anchor (0 to 1). |
| `zIndex` | `Float` | `0f` | Drawing order. |
| `infoWindow` | `@Composable () -> Unit`| `null` | Custom Compose UI popup. |
| `onClick` | `() -> Boolean` | `{ false }` | Click handler. |

### Shapes (Polyline, Polygon, Circle)

All shapes support `onClick: () -> Boolean` and `zIndex`.

| Component | Key Properties |
| :--- | :--- |
| `Polyline`| `points`, `color`, `width`, `pattern`, `geodesic`, `zIndex`, `onClick` |
| `Polygon` | `points`, `holes`, `fillColor`, `strokeColor`, `strokeWidth`, `zIndex`, `onClick` |
| `Circle`  | `center`, `radiusMeters`, `fillColor`, `strokeColor`, `strokeWidth`, `zIndex`, `onClick` |

### Advanced Components

#### MarkerCluster
Groups markers into clusters.
- `options`: `MarkerClusterOptions` (Radius, coverage, etc)
- `onClusterClick`: `(lat, lng, count) -> Unit`
- `content`: `@Composable () -> Unit` (The markers to cluster)

#### MapOverlay
Pins any Composable to map coordinates.
- `position`: `LatLng`
- `anchorFractionX` / `anchorFractionY`: `Float` (Alignment)
- `content`: `@Composable () -> Unit`

### Configuration

#### MapProperties
- `mapStyle`: `MapStyle` (Default: `OpenStreetMap`)
- `automaticThemeSync`: `Boolean` (Sync with System Dark/Light mode)
- `minZoom` / `maxZoom`: `Double` (Zoom constraints)

#### MapUiSettings
- `zoomControlsEnabled`: `Boolean` (Visible +/- buttons)
- `scrollGesturesEnabled`: `Boolean` (Pan support)
- `zoomGesturesEnabled`: `Boolean` (Pinch/Double-tap zoom)
- `rotateGesturesEnabled`: `Boolean` (Two-finger rotation)
- `showCurrentLocation`: `Boolean` (Enable GPS blue dot)
- `currentLocationIcon`: `LeaflektCurrentLocationIcon?` (Custom GPS icon)

### Marker Icons

Group large numbers of markers into clusters automatically:

<!-- MEDIA SUGGESTION: CLUSTERING COMPARISON
     Suggestion: A side-by-side comparison image:
     - Left: 100+ individual markers overlapping (cluttered).
     - Right: Clean clusters with count badges (e.g., "50", "20").
-->

```kotlin
MapView {
    MarkerCluster(
        options = MarkerClusterOptions(
            maxClusterRadius = 80,
            showCoverageOnHover = true
        )
    ) {
        markers.forEach { m ->
            Marker(position = m.position)
        }
    }
}
```

### Map Zoom & Interaction

Control the allowed zoom range and gesture support:

```kotlin
MapView(
    properties = MapProperties(
        minZoom = 2.0,   // Prevent zooming out to blank space
        maxZoom = 18.0   // Limit detail zoom
    ),
    uiSettings = MapUiSettings(
        zoomGesturesEnabled = true,
        scrollGesturesEnabled = true,
        rotateGesturesEnabled = true // Continuous map rotation
    )
)
```

## Marker Icons

Leaflekt supports synchronous bitmaps and native Compose UI overlays. If your app wants remote
images, load them with your preferred image library and convert the decoded result into a
`MarkerIcon`.

<!-- MEDIA SUGGESTION: ICON TYPES SHOWCASE
     Suggestion: A horizontal row showing 3 markers:
     - 1. A standard bitmap icon (e.g., a pin).
     - 2. An async-loaded image (e.g., a profile picture from a URL).
     - 3. A native Compose UI marker (e.g., a pill with "99+" badge).
-->

### Synchronous (Bitmap) API

```kotlin
Marker(
    position = LatLng(22.5726, 88.3639),
    icon = MarkerIcon(
        bitmap = bikeBitmap,
        widthPx = 72,
        heightPx = 72,
        anchorFractionX = 0.5f,
        anchorFractionY = 1f
    ),
    rotationDegrees = 90f // Visual rotation for vehicles/navigation
)
```

<!-- MEDIA SUGGESTION: VEHICLE ROTATION GIF
     Suggestion: A short GIF showing a car or bike marker rotating 360 degrees smoothly while staying centered on a coordinate.
-->

### App-Level Remote Images

The sample app demonstrates a Coil-based helper for remote marker icons, but the SDK itself does
not depend on Coil, Glide, or any specific image pipeline.

### Composable Marker Icons

Render any Compose UI as a marker:

```kotlin
Marker(
    position = LatLng(22.5726, 88.3639),
    iconContent = {
        TextBadge(text = "99+", color = Color.Red)
    }
)
```

## Built-In Map Styles

- `MapStyle.OpenStreetMap`
- `MapStyle.CartoLight`
- `MapStyle.CartoDark`
- `MapStyle.OpenTopoMap`
- `MapStyle.EsriWorldImagery`

<!-- MEDIA SUGGESTION: MAP STYLE GRID
     Suggestion: A 2x3 or 3x2 grid of 1:1 square screenshots showing:
     - The same area (e.g., Central Park or India Gate) in each style.
     - Captions below each square with the style name.
-->

## Ola Maps Support

LeafleKT provides first-class support for **Ola Maps**. Use the OLA tile provider and integrate with OLA's Places & Directions API (see `leaflektsampleapp`).

<!-- MEDIA SUGGESTION: OLA MAPS SHOWCASE
     Suggestion: A screenshot of the Sample App using OLA Maps style, showing a destination route (Polyline) and an OLA Places search bar at the top.
-->

```kotlin
MapView(
    properties = MapProperties(
        mapStyle = MapStyle.OlaMaps(apiKey = "YOUR_API_KEY")
    )
)
```

## Notes On Behavior

- **India Boundary:** Optimized GeoJSON overlay is reactive to map styles and keeps the boundary visible even on satellite layers.
- **Theme Sync:** `automaticThemeSync = true` automatically swaps between Light/Dark variants of Carto or OpenStreetMap based on the Android system theme.

<!-- MEDIA SUGGESTION: THEME SYNC SPLIT-VIEW
     Suggestion: A split-screen image:
     - Left: System in Light Mode (Map shows CartoLight).
     - Right: System in Dark Mode (Map shows CartoDark).
-->

- **Smooth Zoom:** `zoomSnap` is disabled by default, providing a smooth, continuous pinch-to-zoom experience similar to native maps.
- **Map Rotation:** Full 360-degree rotation is supported via the `rotateGesturesEnabled` UI setting.

## Feature Checklist

Implemented:

- [x] Compose map container
- [x] Camera position state (LatLng, Zoom, Bearing)
- [x] Map style switching (OSM, Carto, Esri, OLA)
- [x] Automatic Theme Sync (Dark/Light mode)
- [x] India boundary overlay (313 KB optimized)
- [x] Declarative markers, polylines, polygons, and circles
- [x] Advanced Marker Clustering
- [x] Marker rotation support
- [x] Click callbacks for all map entities
- [x] `MapEffect` for imperative map extensions
- [x] SDK current location overlay (with pulse and custom icons)
- [x] Bitmap marker icons
- [x] Native Compose Overlays (`iconContent`)
- [x] Info window customization (initial visibility, anchor, and custom UI)
- [x] Zoom bounds (min/max constraints)
- [x] Continuous pinch zoom (smooth experience)

Planned:

- [ ] GeoJSON layer API
- [ ] Directions journey playback sample
- [ ] Route playback controls (Start/Pause/Stop)
- [ ] Curved Lines (`LeaflektArc`)
- [ ] Offline tile caching
- [ ] Shape drag/edit interactions

## Legal

- LeafleKT is not affiliated with Google, Leaflet, or OpenStreetMap.
- Leaflet.js is licensed separately under its own terms.
- Map tile usage and attribution remain the responsibility of the consuming app.

## License

Apache License 2.0. See [LICENSE](LICENSE).

