# Global Refactor: Remove "Leaflekt" Prefix

This plan outlines the systematic removal of the `Leaflekt` prefix from all user-facing components, states, and supporting classes to simplify the SDK API.

## Core Naming Mapping

| Old Symbol | New Symbol | Status |
|------------|------------|--------|
| `LeaflektMap` | `MapView` | [x] |
| `LeaflektMarker` | `Marker` | [x] |
| `LeaflektCircle` | `Circle` | [x] |
| `LeaflektPolygon` | `Polygon` | [x] |
| `LeaflektPolyline` | `Polyline` | [x] |
| `LeaflektMarkerCluster` | `MarkerCluster` | [x] |
| `LeaflektLatLng` | `LatLng` | [x] |
| `LeaflektCameraPosition` | `CameraPosition` | [x] |
| `LeaflektController` | `MapController` | [x] |
| `LeaflektOverlay` | `MapOverlay` | [x] |
| `LeaflektMapStyle` | `MapStyle` | [x] |
| `LeaflektMapProperties` | `MapProperties` | [x] |
| `LeaflektMapUiSettings` | `MapUiSettings` | [x] |
| `LeaflektMapComposable` | `MapComposable` | [x] |
| `LeaflektMapEffect` | `MapEffect` | [x] |

## Supporting Classes & State

| Old Symbol | New Symbol | Status |
|------------|------------|--------|
| `LeaflektMarkerState` | `MarkerState` | [x] |
| `LeaflektMarkerIcon` | `MarkerIcon` | [x] |
| `LeaflektMarkerInfo` | `MarkerInfo` | [x] |
| `LeaflektCameraPositionState` | `CameraPositionState` | [x] |
| `LeaflektCircleInfo` | `CircleInfo` | [x] |
| `LeaflektCircleState` | `CircleState` | [x] |
| `LeaflektPolygonInfo` | `PolygonInfo` | [x] |
| `LeaflektPolygonState` | `PolygonState" | [x] |
| `LeaflektPolylineInfo` | `PolylineInfo` | [x] |
| `LeaflektPolylineState` | `PolylineState" | [x] |
| `LeaflektCurrentLocationIcon` | `CurrentLocationIcon` | [x] |
| `rememberLeaflektCameraPositionState` | `rememberCameraPositionState` | [x] |
| `rememberLeaflektMarkerState` | `rememberMarkerState` | [x] |
| `LocalLeaflektController` | `LocalMapController` | [x] |
| `LocalLeaflektMarkerClusterId` | `LocalMarkerClusterId` | [x] |
| `LocalLeaflektCameraPositionState` | `LocalCameraPositionState` | [x] |

## Internal & Utility Renames

| Old Symbol | New Symbol | Status |
|------------|------------|--------|
| `LeaflektJsBridge` | `MapJsBridge` | [x] |
| `LeaflektWebView` | `MapWebView` | [x] |
| `LeaflektScriptBuilder` | `MapScriptBuilder` | [x] |
| `LeaflektMapJson` | `MapJson` | [x] |
| `LeaflektCurrentLocationOverlay` | `CurrentLocationOverlay` | [x] |
| `LeaflektWebConstants` | `MapWebConstants` | [x] |

## File Migration Checklist

### `leaflekt` module
- [x] `LeaflektMap.kt` -> `MapView.kt`
- [x] `LeaflektMarker.kt` -> `Marker.kt`
- [x] `LeaflektMarkerState.kt` -> `MarkerState.kt`
- [x] `LeaflektMarkerIcon.kt` -> `MarkerIcon.kt`
- [x] `LeaflektMarkerInfo.kt` -> `MarkerInfo.kt`
- [x] `LeaflektCircle.kt` -> `Circle.kt`
- [x] `LeaflektCircleState.kt" -> `CircleState.kt`
- [x] `LeaflektCircleInfo.kt` -> `CircleInfo.kt`
- [x] `LeaflektPolygon.kt` -> `Polygon.kt`
- [x] `LeaflektPolyline.kt` -> `Polyline.kt`
- [x] `LeaflektController.kt` -> `MapController.kt`
- [x] `LeaflektCameraPosition.kt` -> `CameraPosition.kt`
- [x] `LeaflektCameraPositionState.kt` -> `CameraPositionState.kt`
- [x] `LeaflektMapProperties.kt` -> `MapProperties.kt`
- [x] `LeaflektMapUiSettings.kt` -> `MapUiSettings.kt`
- [x] `LeaflektMapStyle.kt` -> `MapStyle.kt`
- [x] `LeaflektOverlay.kt` -> `MapOverlay.kt`
- [x] `LeaflektMarkerCluster.kt` -> `MarkerCluster.kt`
- [x] `LeaflektJsBridge.kt` -> `MapJsBridge.kt`
- [x] `LeaflektWebView.kt` -> `MapWebView.kt`
- [x] `LeaflektScriptBuilder.kt` -> `MapScriptBuilder.kt`
- [x] `LeaflektMapJson.kt` -> `MapJson.kt`
- [x] `LeaflektCurrentLocationOverlay.kt` -> `CurrentLocationOverlay.kt`

## Implementation Steps

1. **Phase 1: Library Internal Renames** [DONE]
2. **Phase 2: JS Bridge & Assets** [DONE]
3. **Phase 3: Sample App Migration** [DONE]
4. **Phase 4: Documentation & Cleanup** [DONE]

## Context & Constraints
- **Naming Rule:** Removed "Leaflekt" prefix. Simplified to `MapView`, `Marker`, `Circle`, etc.
- **Convention:** Follows Jetpack Compose naming conventions (e.g. `rememberMarkerState`).
- **Backward Compatibility:** Dropped in favor of cleaner API as requested.
