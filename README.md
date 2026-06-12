# LeafleKT

LeafleKT is a Kotlin Multiplatform Leaflet.js SDK — a UI-free core, Compose Multiplatform UI layer, and a native Swift Package for iOS.

<img src="https://github.com/user-attachments/assets/ac67880c-9258-4b8b-be38-10ffd0a3788c"
     style="max-width:100%; height:auto;" />

[![Maven Central](https://img.shields.io/maven-central/v/io.github.binayshaw7777/leaflekt-compose)](https://central.sonatype.com/artifact/io.github.binayshaw7777/leaflekt-compose)
[![Android API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
![Build Status](https://github.com/binayshaw7777/LeafleKT-Maps/actions/workflows/release-master.yml/badge.svg)

---

```kotlin
// build.gradle.kts
implementation("io.github.binayshaw7777:leaflekt-compose:1.0.5")
```

```kotlin
@Composable
fun MyMap() {
    val cameraState = rememberLeaflektCameraPositionState {
        position = LeaflektCameraPosition(LeaflektLatLng(22.5726, 88.3639), 12.0)
    }
    LeaflektMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        properties = LeaflektMapProperties(mapStyle = LeaflektMapStyle.OpenFreeMapBright),
    ) {
        LeaflektMarker(position = LeaflektLatLng(22.5726, 88.3639), title = "Kolkata")
    }
}
```

## Modules

| Module | Artifact | Platform |
|--------|----------|----------|
| Compose UI | `leaflekt-compose` | Android + CMP (iOS via CMP) |
| Core (no UI) | `leaflekt-core` | KMP (any platform) |
| iOS Native | `LeaflektMap` Swift Package | iOS 15+ |

## Features

- 8 tile styles (OSM, Carto, OpenFreeMap, Esri, Topo)
- Markers, polylines, polygons, circles with hoisted state
- Marker clustering + custom icons (base64/URL)
- GeoJSON overlays (India built-in, custom)
- Stroke patterns, polygon holes, selection state
- Imperative controller API
- Ola Maps search & directions (sample app)

## Quick Start

[Full docs →](https://leaflekt.dev) | [iOS SPM →](PLATFORM_IOS.md) | [Sample app →](leaflektsampleapp/)

## License

Apache 2.0
