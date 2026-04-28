# LeafleKT

LeafleKT is a Compose-first Android wrapper around Leaflet.js. It uses `WebView` plus a JavaScript bridge, but the public API is Kotlin-first and state-driven.

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

## Marker Icons

Leaflekt supports both synchronous bitmap icons and asynchronous image loading via Coil.

### Synchronous (Bitmap) API

Provide a pre-loaded `Bitmap` via `LeaflektMarkerIcon`:

```kotlin
val bikeBitmap = BitmapFactory.decodeResource(
    LocalContext.current.resources,
    R.drawable.ic_bike_marker
)

LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    icon = LeaflektMarkerIcon(
        bitmap = bikeBitmap,
        widthPx = 72,
        heightPx = 72,
        anchorFractionX = 0.5f,
        anchorFractionY = 1f
    )
)
```

### Asynchronous (Coil-powered) API

Use `rememberLeaflektAsyncMarkerIcon` to load images from URLs, drawable resources, files, or any Coil-supported model. The returned `State<LeaflektMarkerIcon?>` is `null` while loading and automatically updates when the image is ready.

> **Note:** Async icon loading requires adding the Coil Compose dependency:
> ```kotlin
> implementation("io.coil-kt:coil-compose:2.7.0")
> ```

```kotlin
// From URL
val remoteIcon = rememberLeaflektAsyncMarkerIcon(
    model = "https://example.com/marker.png",
    widthPx = 64,
    heightPx = 64
)

// From drawable resource
val localIcon = rememberLeaflektAsyncMarkerIcon(
    model = R.drawable.ic_custom_marker,
    anchorFractionX = 0.5f,
    anchorFractionY = 0.5f
)

LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    icon = remoteIcon.value  // null during load, non-null on success
)
```

Supported model types include:
- `String` — HTTP/HTTPS URL or absolute file path
- `@DrawableRes Int` — Android drawable resource ID
- `Bitmap` — in-memory bitmap
- `File` — local file
- `Uri` — content URI
- Any custom Coil `Model` (requires providing a custom [ImageLoader])

### Composable Marker Icons

Pass a composable lambda directly as the marker icon using `iconContent`. This renders any Compose UI at the marker's position, anchored flexibly.

```kotlin
LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    iconContent = {
        // Custom animated vector, text badge, or any composable
        Box(
            modifier = Modifier
                .background(Color.Blue, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Custom",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }
    },
    iconAnchorX = 0.5f,  // center horizontally (default)
    iconAnchorY = 1f     // bottom aligned to marker tip (default)
)
```

`iconContent` is rendered using `LeaflektOverlay`, so it's resolution-independent and fully interactive (clicks, animations, etc.). You can combine `icon` (bitmap) and `iconContent` (overlay) simultaneously for layered effects.

### Anchor Control

Both APIs support `anchorFractionX` and `anchorFractionY` (0–1) to control which point on the icon is anchored to the marker's lat/lng:
- `(0f, 0f)` — top-left corner
- `(0.5f, 0.5f)` — center (default for `LeaflektMarkerIcon`)
- `(1f, 1f)` — bottom-right corner

Default for `LeaflektMarkerIcon`: `anchorFractionY = 1f` (bottom edge), matching Google Maps behavior.

### Map Zoom Bounds

Control the allowed zoom range via `LeaflektMapProperties` to prevent overscrolling into blank space:

```kotlin
LeaflektMap(
    properties = LeaflektMapProperties(
        minZoom = 2.0,   // Don't zoom out beyond world view
        maxZoom = 18.0   // Don't over-zoom beyond tile detail
    )
)
```

Defaults: `minZoom = 2.0`, `maxZoom = 19.0`. These bounds apply to all user gestures (pinch, scroll, double-tap) and programmatic camera moves.

### Info Window Control

Control info window visibility and anchor position:

```kotlin
LeaflektMarker(
    position = LeaflektLatLng(22.5726, 88.3639),
    title = "Victoria Memorial",
    infoWindow = {
        MarkerInfoWindowCard(
            label = "Landmark",
            headline = "Victoria Memorial",
            supportingLine = "Built 1906–1921"
        )
    },
    isInfoWindowVisible = true,  // Show immediately
    infoWindowAnchorX = 0.5f,    // Center horizontally (default)
    infoWindowAnchorY = 1f       // Bottom of info window at marker tip (default)
)
```

**Parameters:**
- `isInfoWindowVisible` — Shows the info window on first composition when `true`. Default: `false`. Ignored when `infoWindow` is `null`.
- `infoWindowAnchorX` / `infoWindowAnchorY` — Fraction (0–1) controlling where the info window attaches relative to the marker position. Defaults: `0.5f, 1f` (bottom-center, standard info window placement). Uses the same system as `LeaflektOverlay`.

**Default Leaflet Popup:** If `infoWindow` is not provided, the standard Leaflet popup shows `title` and `snippet` with default styling.

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
- [x] General custom marker bitmaps
- [x] Async image loading for marker icons (Coil-powered)
- [x] Composable marker icons (pass @Composable as iconContent)
- [ ] Drawable convenience API for markers
- [ ] HTML/divIcon markers
- [x] Custom info windows
- [x] Info window anchor customization
- [x] Info window initial visibility control
- [x] OLA Maps Places search and autocomplete sample in `leaflektsampleapp`
- [x] Map Rotation support (using Leaflet.Rotate)
- [x] Marker rotation
- [x] Automatic Theme Sync (Sync map tiles with system Dark/Light mode)
- [x] Native Compose Overlays (Pin any @Composable to map coordinates)
- [x] Zoom bounds (min/max to prevent overscroll to white space)
- [x] Smooth continuous pinch zoom (no snapping to integer zoom levels)
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
