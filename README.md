# LeafleKT

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.

<img src="https://github.com/user-attachments/assets/d9544533-7c4a-4653-9364-cfd631314368"
     style="max-width:100%; height:auto;" />

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
- Camera state
- Map style switching
- India boundary overlay
- Declarative markers
- Declarative polylines
- Declarative polygons
- Declarative circles
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
    val cameraPositionState = rememberLeaflektCameraPositionState(
        initialPosition = LeaflektCameraPosition(
            target = LeaflektLatLng(22.5726, 88.3639),
            zoom = 11.0
        )
    )

    LeaflektMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = LeaflektMapProperties(
            mapStyle = LeaflektMapStyle.OpenStreetMap
        ),
        uiSettings = LeaflektMapUiSettings(
            zoomControlsEnabled = true
        ),
        onMapClick = { latLng ->
            Log.d("LeafleKT", "Map click: $latLng")
        }
    ) {
        LeaflektMarker(
            position = LeaflektLatLng(22.5726, 88.3639),
            title = "Kolkata"
        )

        LeaflektPolyline(
            points = listOf(
                LeaflektLatLng(22.5726, 88.3639),
                LeaflektLatLng(22.5826, 88.3939)
            ),
            color = Color(0xFF0B6E4F),
            width = 6f
        )

        LeaflektPolygon(
            points = listOf(
                LeaflektLatLng(22.5600, 88.3400),
                LeaflektLatLng(22.5900, 88.3500),
                LeaflektLatLng(22.5800, 88.3900)
            ),
            fillColor = Color(0x332F7D32),
            strokeColor = Color(0xFF2F7D32),
            strokeWidth = 4f
        )

        LeaflektCircle(
            center = LeaflektLatLng(22.5726, 88.3639),
            radiusMeters = 1200.0,
            fillColor = Color(0x33438A5E),
            strokeColor = Color(0xFF1E5F3A),
            strokeWidth = 3f
        )
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

Supported child composables inside `LeaflektMap`:

- `LeaflektMarker`
- `LeaflektPolyline`
- `LeaflektPolygon`
- `LeaflektCircle`
- `MapEffect`

The `content` slot can also render normal Compose UI above the map.

Extensibility example:

```kotlin
LeaflektMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = cameraPositionState
) {
    MapEffect(selectedStyle) { controller ->
        controller.executeJavaScript(
            """
            window.LeaflektBridge.setMapStyle({
              id: "custom",
              tileUrlTemplate: "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
              attributionHtml: "&copy; OpenStreetMap contributors",
              maxZoom: 19,
              subdomains: null
            });
            """.trimIndent()
        )
    }
}
```

Current location example:

```kotlin
LeaflektMap(
    modifier = Modifier.fillMaxSize(),
    uiSettings = LeaflektMapUiSettings(
        showCurrentLocation = true
    )
)
```

Custom current location icon:

```kotlin
LeaflektMap(
    uiSettings = LeaflektMapUiSettings(
        showCurrentLocation = true,
        currentLocationIcon = LeaflektCurrentLocationIcon(
            bitmap = myLocationBitmap,
            widthPx = 36,
            heightPx = 36,
            anchorFractionX = 0.5f,
            anchorFractionY = 0.5f
        )
    )
)
```

Camera lifecycle example:

```kotlin
@Composable
fun CameraAwareMap() {
    val cameraPositionState = rememberLeaflektCameraPositionState()
    var cameraStatus by rememberSaveable { mutableStateOf("idle") }

    LeaflektMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onCameraMoveStarted = {
            cameraStatus = "moving"
        },
        onCameraMove = {
            Log.d("LeafleKT", "Camera: ${cameraPositionState.position}")
        },
        onCameraIdle = {
            cameraStatus = "idle"
            Log.d(
                "LeafleKT",
                "Settled at ${cameraPositionState.position.target} z=${cameraPositionState.position.zoom}"
            )
        }
    )
}
```

## Built-In Map Styles

- `LeaflektMapStyle.OpenStreetMap`
- `LeaflektMapStyle.CartoLight`
- `LeaflektMapStyle.CartoDark`
- `LeaflektMapStyle.OpenTopoMap`
- `LeaflektMapStyle.EsriWorldImagery`

## Notes On Behavior

- The India boundary overlay is always enabled and reacts to the active map style.
- Camera lifecycle callbacks are bridged from Leaflet `movestart` / `move` / `moveend` and `zoomstart` / `zoom` / `zoomend`, and user gestures sync back into `LeaflektCameraPositionState`.
- `showCurrentLocation = true` requests Android location permission when needed and renders a default blue dot with pulse and accuracy ring, or a custom bitmap marker when `currentLocationIcon` is provided.
- `geodesic` is accepted for API familiarity on polylines and polygons, but Leaflet core does not provide Google Maps style geodesic rendering. It is retained as a compatibility field and currently renders as a normal projected path.
- Stroke patterns are mapped to Leaflet `dashArray`, so they are approximate rather than 1:1 with Google Maps SDK pattern items.
- Vector layer draw order is best-effort. LeafleKT reapplies polyline, polygon, and circle order by `zIndex`, then insertion order, but Leaflet does not expose a Google Maps style path `zIndex` contract.
- Polyline `alpha` is supported and is applied by emitting an RGBA stroke color to the Leaflet runtime.

## Shape Capabilities

- `LeaflektPolyline`: points, color, width, alpha, stroke pattern, visibility, click callbacks, best-effort `zIndex`, compatibility-only `geodesic`
- `LeaflektPolygon`: points, holes, fill/stroke color, fill/stroke opacity, stroke pattern, visibility, click callbacks, best-effort `zIndex`, compatibility-only `geodesic`
- `LeaflektCircle`: center, radius, fill/stroke color, fill/stroke opacity, stroke pattern, visibility, click callbacks, best-effort `zIndex`

## Release

Version is controlled by the root [`VERSION`](VERSION) file.

Useful commands:

```bash
./gradlew :leaflekt:testDebugUnitTest
./gradlew :leaflekt:assembleRelease
./gradlew :leaflekt:publishReleasePublicationToMavenLocal
./gradlew :app:assembleDebug
```

Master branch release automation:

- reads `VERSION`
- updates the dependency version in this README
- builds and verifies the release artifacts
- publishes GitHub tag and GitHub release
- warms the JitPack build

See:

- [docs/RELEASE_CHECKLIST.md](docs/RELEASE_CHECKLIST.md)
- [docs/GITHUB_RELEASE_SETUP.md](docs/GITHUB_RELEASE_SETUP.md)

## Feature Checklist

Implemented:

- [x] Compose map container
- [x] Camera position state
- [x] Map style switching
- [x] India boundary overlay
- [x] Declarative markers
- [x] Declarative polylines
- [x] Declarative polygons
- [x] Declarative circles
- [x] Click callbacks for map, markers, polylines, polygons, and circles
- [x] Shape selection state for polyline, polygon, and circle wrappers
- [x] `MapEffect` for imperative map extensions via `LeaflektController`
- [x] Camera move started / moving / idle callbacks
- [x] Camera position sync from user gestures back into `LeaflektCameraPositionState`
- [x] SDK current location overlay with boolean toggle and optional custom icon

Planned:

- [ ] GeoJSON layer API
- [ ] General custom marker bitmaps and drawables
- [ ] HTML/divIcon markers
- [ ] Custom info windows
- [ ] Tile source customization API
- [ ] Clustering
- [ ] Offline tile caching
- [ ] Tooltips and popups
- [ ] Shape drag interactions
- [ ] Shape edit handles

## Legal

- LeafleKT is not affiliated with Google, Leaflet, or OpenStreetMap.
- The API shape is intentionally familiar to Google Maps Compose, but the implementation in this project is original.
- Leaflet.js is licensed separately under its own terms.
- Map tile usage and attribution remain the responsibility of the consuming app.

## License

Apache License 2.0. See [LICENSE](LICENSE).
