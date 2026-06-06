# LeafleKT

LeafleKT is a Kotlin Multiplatform Leaflet.js SDK with a UI-free core, Compose Multiplatform UI,
and a native Swift package.

<!-- MEDIA SUGGESTION: HERO POSTER/VIDEO
     Suggestion: A high-quality wide poster (16:9) or a short looping video showing:
     - Map style switching
     - A car marker moving with rotation
     - Smooth pinch-to-zoom
     - A dark/light mode transition
-->
<img src="https://github.com/user-attachments/assets/ac67880c-9258-4b8b-be38-10ffd0a3788c"
     style="max-width:100%; height:auto;" />

[![Maven Central](https://img.shields.io/maven-central/v/io.github.binayshaw7777/leaflekt-compose)](https://central.sonatype.com/artifact/io.github.binayshaw7777/leaflekt-compose)
[![Android API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
![Build Status](https://github.com/binayshaw7777/LeafleKT-Maps/actions/workflows/release-master.yml/badge.svg)

LeafleKT uses `WebView`/`WKWebView` plus a JavaScript bridge while exposing Kotlin-first,
state-driven APIs.

## Status

- Map rendering in Compose
- Camera state
- Map style switching
- India boundary overlay
- Declarative markers
- Declarative polylines
- Declarative polygons
- Declarative circles
- KMP core without Compose dependencies

## Install

Compose Multiplatform or Android Compose:

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-compose:1.0.0")
}
```

KMP without shared UI:

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-core:1.0.0")
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
        properties = LeaflektMapProperties(mapStyle = LeaflektMapStyle.OpenStreetMap),
        uiSettings = LeaflektMapUiSettings(zoomControlsEnabled = true),
        onMapClick = { latLng ->
            Log.d("LeafleKT", "Map click: $latLng")
        }
    ) {
        LeaflektMarker(
            position = LeaflektLatLng(22.5726, 88.3639),
            title = "Kolkata",
            rotationDegrees = 45f
        )

        LeaflektPolyline(
            points = listOf(
                LeaflektLatLng(22.5726, 88.3639),
                LeaflektLatLng(22.5826, 88.3939)
            ),
            color = Color(0xFF0B6E4F),
            width = 6f
        )
    }
}
```

## API Reference

### LeaflektMap

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `modifier` | `Modifier` | Layout modifier for the map. |
| `cameraPositionState` | `LeaflektCameraPositionState` | Hoisted camera state. |
| `properties` | `LeaflektMapProperties` | Map style and GeoJSON overlay. |
| `uiSettings` | `LeaflektMapUiSettings` | UI, gesture, and location settings. |
| `onMapLoaded` | `(() -> Unit)?` | Called when Leaflet is initialized. |
| `onReady` | `((LeaflektController) -> Unit)?` | Provides imperative core access. |
| `onMapClick` | `((LeaflektLatLng) -> Unit)?` | Called when the map is tapped. |
| `onMarkerClick` | `((String) -> Unit)?` | Called when any marker is clicked. |
| `content` | `@Composable () -> Unit` | Declarative markers and shapes. |

### LeaflektMarker

| Parameter | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `state` | `LeaflektMarkerState` | `remember...` | Hoisted marker position. |
| `title` | `String?` | `null` | Title for default popup. |
| `snippet` | `String?` | `null` | Secondary text for default popup. |
| `icon` | `LeaflektMarkerIconInfo?` | `null` | Base64 or URL-backed marker icon. |
| `rotationDegrees` | `Float` | `0f` | Visual rotation of the marker. |
| `visible` | `Boolean` | `true` | Visibility toggle. |
| `alpha` | `Float` | `1.0f` | Opacity (0.0 to 1.0). |
| `zIndex` | `Int` | `0` | Drawing order. |
| `onClick` | `() -> Boolean` | `{ false }` | Click handler (return true to consume). |

### Shapes (Polyline, Polygon, Circle)

| Component | Key Properties |
| :--- | :--- |
| `LeaflektPolyline` | `points`, `color`, `width`, `pattern`, `onClick` |
| `LeaflektPolygon` | `points`, `fillColor`, `strokeColor`, `strokeWidth`, `onClick` |
| `LeaflektCircle` | `center`, `radiusMeters`, `fillColor`, `strokeColor`, `strokeWidth`, `onClick` |

### Configuration

`LeaflektMapProperties` selects `LeaflektMapStyle` and `LeaflektGeoJsonOverlay`.
`LeaflektMapUiSettings` controls zoom buttons, pan/zoom gestures, and current-location UI.
See `leaflektsampleapp` for markers, clusters, shapes, styles, and location examples.

## Migration

Version 1.0 removes the old `leaflekt` Android module and renames the public API to the
`Leaflekt*` family. See [MIGRATION.md](MIGRATION.md) for dependency and symbol mappings.

## Legal

- LeafleKT is not affiliated with Google, Leaflet, or OpenStreetMap.
- Leaflet.js is licensed separately under its own terms.
- Map tile usage and attribution remain the responsibility of the consuming app.

## License

Apache License 2.0. See [LICENSE](LICENSE).
