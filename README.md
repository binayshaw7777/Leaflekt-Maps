# LeafleKT

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.

<!-- MEDIA SUGGESTION: HERO POSTER/VIDEO
     Suggestion: A high-quality wide poster (16:9) or a short looping video showing:
     - Map style switching
     - A car marker moving with rotation
     - Smooth pinch-to-zoom
     - A dark/light mode transition
-->
<img src="https://github.com/user-attachments/assets/ac67880c-9258-4b8b-be38-10ffd0a3788c" style="max-width:100%; height:auto;" />

[![JitPack](https://jitpack.io/v/binayshaw7777/LeafleKT-Maps.svg)](https://jitpack.io/#binayshaw7777/LeafleKT-Maps)
[![Android API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
![Build Status](https://github.com/binayshaw7777/LeafleKT-Maps/actions/workflows/release-master.yml/badge.svg)

**LeafleKT** is a high-performance, Compose-first Android wrapper around [Leaflet.js](https://leafletjs.com/). It brings the power of the web's most popular mapping engine into native Jetpack Compose, offering a stable state-driven Kotlin API while keeping the runtime entirely self-contained.

---

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.
## Status

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

    // Optional: Required only for async marker icon loading
    // rememberLeaflektAsyncMarkerIcon uses Coil under the hood
    implementation("io.coil-kt:coil-compose:2.7.0")
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
    val cameraPositionState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(
            target = LeaflektLatLng(latitude = 22.5726, longitude = 88.3639),
            zoom = 12.0
        )
    }

    LeaflektMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = LeaflektMapProperties(
            mapStyle = LeaflektMapStyle.OpenStreetMap,
            automaticThemeSync = true // Sync with System Light/Dark mode
        ),
        uiSettings = LeaflektMapUiSettings(
            zoomControlsEnabled = true,
            rotateGesturesEnabled = true // Enable map rotation
        ),
        onMapClick = { latLng ->
            Log.d("LeafleKT", "Map click: $latLng")
        }
    ) {
        LeaflektMarker(
            position = LeaflektLatLng(22.5726, 88.3639),
            title = "Kolkata",
            rotationDegrees = 45f // Rotate marker clockwise
        )

        LeaflektPolyline(
            points = listOf(
                LeaflektLatLng(22.5726, 88.3639),
                LeaflektLatLng(22.5826, 88.3939)
            ),
            color = Color(0xFF0B6E4F),
            width = 6f
        )
        
        // ... Polygons, Circles, and more
    }
}
```

## API Surface

Main entry point:

```kotlin
LeaflektMap(
    modifier = Modifier,
    cameraPositionState = rememberLeaflektCameraPositionState(),
    contentDescription = null,
    properties = DefaultLeaflektMapProperties,
    uiSettings = DefaultLeaflektMapUiSettings,
    onMapLoaded = null,
    onReady = null,
    onMapClick = null,
    onCameraMoveStarted = null,
    onCameraMove = null,
    onCameraIdle = null,
    onMarkerClick = null,
    content = {}
)
```

### Marker Clustering

Group large numbers of markers into clusters automatically:

<!-- MEDIA SUGGESTION: CLUSTERING COMPARISON
     Suggestion: A side-by-side comparison image:
     - Left: 100+ individual markers overlapping (cluttered).
     - Right: Clean clusters with count badges (e.g., "50", "20").
-->

```kotlin
LeaflektMap {
    LeaflektMarkerCluster(
        options = MarkerClusterOptions(
            maxClusterRadius = 80,
            showCoverageOnHover = true
        )
    ) {
        markers.forEach { m ->
            LeaflektMarker(position = m.position)
        }
    }
}
```

### Map Zoom & Interaction

Control the allowed zoom range and gesture support:

```kotlin
LeaflektMap(
    properties = LeaflektMapProperties(
        minZoom = 2.0,   // Prevent zooming out to blank space
        maxZoom = 18.0   // Limit detail zoom
    ),
    uiSettings = LeaflektMapUiSettings(
        zoomGesturesEnabled = true,
        scrollGesturesEnabled = true,
        rotateGesturesEnabled = true // Continuous map rotation
    )
)
```

## Marker Icons

Leaflekt supports synchronous bitmaps, asynchronous image loading via Coil, and native Compose UI overlays.

<!-- MEDIA SUGGESTION: ICON TYPES SHOWCASE
     Suggestion: A horizontal row showing 3 markers:
     - 1. A standard bitmap icon (e.g., a pin).
     - 2. An async-loaded image (e.g., a profile picture from a URL).
     - 3. A native Compose UI marker (e.g., a pill with "99+" badge).
-->

### Synchronous (Bitmap) API

```kotlin
LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    icon = LeaflektMarkerIcon(
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

### Asynchronous (Coil-powered) API

```kotlin
val remoteIcon = rememberLeaflektAsyncMarkerIcon(
    model = "https://example.com/marker.png",
    widthPx = 64,
    heightPx = 64
)

LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    icon = remoteIcon.value
)
```

### Composable Marker Icons

Render any Compose UI as a marker:

```kotlin
LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    iconContent = {
        TextBadge(text = "99+", color = Color.Red)
    }
)
```

## Built-In Map Styles

- `LeaflektMapStyle.OpenStreetMap`
- `LeaflektMapStyle.CartoLight`
- `LeaflektMapStyle.CartoDark`
- `LeaflektMapStyle.OpenTopoMap`
- `LeaflektMapStyle.EsriWorldImagery`

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
LeaflektMap(
    properties = LeaflektMapProperties(
        mapStyle = LeaflektMapStyle.OlaMaps(apiKey = "YOUR_API_KEY")
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
- [x] Async image loading for markers (Coil-powered)
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
