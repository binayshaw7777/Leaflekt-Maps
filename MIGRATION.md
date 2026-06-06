# Migrating to LeafleKT 1.0

Version 1.0 replaces the legacy Android `leaflekt` module with separate core and Compose artifacts.

## Dependencies

KMP projects without Compose:

```kotlin
implementation("io.github.binayshaw7777:leaflekt-core:1.0.0")
```

Compose Multiplatform or Android Compose projects:

```kotlin
implementation("io.github.binayshaw7777:leaflekt-compose:1.0.0")
```

Remove the JitPack repository and the old
`com.github.binayshaw7777.LeafleKT:leaflekt` dependency. Both new artifacts publish to Maven Central.

## Package Changes

Core models and controllers moved from `com.binayshaw7777.leaflekt.compose` to
`com.binayshaw7777.leaflekt`. Composables and Compose state remain in
`com.binayshaw7777.leaflekt.compose`.

Common renames from the removed Android module:

| Before | After |
|---|---|
| `MapView` | `LeaflektMap` |
| `MapController` | `LeaflektController` |
| `LatLng` | `LeaflektLatLng` |
| `CameraPosition` | `LeaflektCameraPosition` |
| `MapProperties` | `LeaflektMapProperties` |
| `MapUiSettings` | `LeaflektMapUiSettings` |
| `MapStyle` | `LeaflektMapStyle` |
| `Marker`, `Polyline`, `Polygon`, `Circle` | `LeaflektMarker`, `LeaflektPolyline`, `LeaflektPolygon`, `LeaflektCircle` |

## Colors

Core shape models no longer depend on Compose `Color`. Use `LeaflektColor`:

```kotlin
LeaflektPolylineInfo(
    id = "route",
    points = points,
    color = LeaflektColor.Blue,
)
```

Compose callers can convert colors at the boundary:

```kotlin
Color.Red.toLeaflektColor()
```

Compose shape composables still accept Compose `Color` directly.
