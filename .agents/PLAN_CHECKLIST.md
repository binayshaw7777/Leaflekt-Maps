# LeafleKT (Leaflet + Compose) - Execution Checklist

## Architecture (Locked)
- [x] Jetpack Compose UI + Android WebView + JavaScript Bridge + Leaflet.js stack implemented
- [x] Data flow wired: Compose -> Controller -> WebView -> JS Bridge -> Leaflet
- [x] Reverse callbacks wired: Leaflet -> JS Bridge -> Kotlin callbacks -> Compose state

## Refinement & Optimization (Session 0.3.0)
- [x] **Asset Optimization:** India boundary GeoJSON reduced from 6.6 MB to 313 KB (5% simplification).
- [x] **Branding Refactor:** All internal symbols migrated from `Leaflet*` to `Leaflekt*`.
- [x] **State Management:** 1:1 Google Maps Compose pattern implemented (`CameraPositionState`).
- [x] **Declarative Markers:** Implemented child-based `Marker` API with hoisted state support.
- [x] **Security:** Safe Browsing enabled; attribution links intercepted and redirected to system browser.
- [x] **Documentation:** Dokka integrated; all public functions include code snippets in KDocs.
- [x] **Automation:** CI/CD pipeline wired with README auto-versioning and clean semantic tagging.

## Day 1 MVP Phases (Updated)

### Phase 12 - Deployment
- [x] Push code to master branch
- [x] Create automated tag `0.1.0` (v-prefix removed)
- [x] Verify JitPack build
- [x] Restore WebViewAssetLoader functionality

### Phase 13 - Launch
- [x] Refactor: Rename `LeafletMap` and internal wrapper symbols to `MapView` for brand consistency.
- [x] Implement binary-compatible UI Settings.
- [ ] Record demo
- [ ] LinkedIn post

## Future Scope 🚀

### Phase 14 - Advanced Overlays
- [x] Polylines implementation
- [x] Polygons implementation
- [x] Circles implementation
- [x] OLA Maps Places search and autocomplete sample in `leaflektsampleapp`
- [x] Map Rotation support (using Leaflet.Rotate)
- [x] `MarkerCluster` / `MarkerClusterOptions` in `:leaflekt` (Leaflet.markercluster plugin)
- [x] `MapOverlay` — pin any @Composable to a `LatLng` coordinate
- [x] `MapController` refactor — new `controller/MapController.kt` with richer API
- [x] `MarkerIcon` — public custom bitmap icon API (`marker/MarkerIcon.kt`)
- [x] Marker rotation + alpha + zIndex public API
- [x] Custom info window support (`infoWindow: @Composable () -> Unit`)
- [ ] Directions journey playback sample with moving bike marker
- [ ] Route playback controls: start, pause, resume, stop
- [ ] Route playback speed controls using Compose segmented buttons
- [ ] Marker visibility toggle for journey playback

### Phase 15 - Core Infrastructure
- [ ] **JNI Layer Migration:** Research migrating the bridge from JS-based to a native JNI layer for improved performance and direct engine access.
- [ ] Offline Tile caching system
- [x] Custom Icon support (Bitmap-backed marker icons)
- [x] Public custom marker icon API for SDK consumers
- [x] Public marker rotation API for SDK consumers
- [x] Public custom info window API for SDK consumers
- [x] **Async Icon Loading:** Coil-powered async marker icon loading with `rememberLeaflektAsyncMarkerIcon(model)` supporting URLs, resources, files, and URIs.
- [x] **Composable Marker Icons:** Pass @Composable lambdas directly as marker icons via `iconContent` with configurable anchors.
- [x] **Zoom Bounds:** Enforce min/max zoom to prevent overscrolling to white space.
- [x] Info window anchor customization (`infoWindowAnchorX/Y`) and initial visibility (`isInfoWindowVisible`)
- [ ] Public moving-marker icon API for route playback and vehicle simulation

### Phase 16 - mapcn Inspiration (Rich UI & Viz)
- [x] **Automatic Theme Sync:** Sync map tiles with system Light/Dark mode.
- [x] **Native Compose Overlays:** Pin any @Composable to a `LatLng`.
- [x] **Smooth Pinch Zoom:** Fix pinch zoom snappiness by disabling zoomSnap for continuous zoom levels (leaflekt/src/main/assets/map.html:194).
- [ ] **Curved Lines (`LeaflektArc`):** Bezier curve support for visualizations.
- [ ] **MapBlocks Registry:** Common pre-wired UI patterns (e.g., Location Picker).

### Phase 17 - CMP + iOS Native Parity (feature/cmp-migration)

> Features shipped in `:leaflekt` (Android) or `leaflektsampleapp` that are missing in `:leaflekt-compose` (CMP) and `LeaflektMap` (iOS SPM).

#### 17A — `:leaflekt-compose` CMP Module

- [ ] **MarkerCluster CMP:** Add `LeaflektMarkerCluster` composable + `MarkerClusterOptions` to `commonMain`. Wire JS bridge calls for `addCluster`/`removeCluster`. Add Leaflet.markercluster plugin to `androidMain/assets/` and `iosMain/resources/`.
- [ ] **MapOverlay CMP:** Port `MapOverlay` composable to `commonMain`. `registerOverlayPoint`/`unregisterOverlayPoint` JS calls already in CMP `map.html` — wire `LeaflektController` `expect/actual` to expose them.
- [ ] **MapController richer API:** Sync `LeaflektControllerInterface` in `commonMain` with new `:leaflekt` `MapController` API (recenter, setStyle, zoom bounds, etc.).
- [ ] **MarkerIcon CMP:** Ensure `LeaflektMarkerIcon` (ByteArray-backed, CMP-compatible) covers all cases from Android `MarkerIcon`. Add `iconContent: @Composable () -> Unit` path.
- [ ] **Marker rotation + zIndex + alpha CMP:** Verify `LeaflektMarkerInfo` in `commonMain` includes `rotationDegrees`, `alpha`, `zIndex` fields.
- [ ] **Custom info windows CMP:** Verify `LeaflektMarker` in `commonMain` supports `infoWindow: @Composable () -> Unit`.
- [ ] **GeoJSON overlay CMP:** Decide whether `LeaflektGeoJsonOverlay` (already in `commonMain`) should be exposed in `LeaflektMapProperties` or kept as a standalone call. Align with Android behavior.
- [ ] **app-cmp demo parity:** Update `app-cmp/App.kt` to demo: clustering, map overlay, directions route polyline, custom marker icons — matching `leaflektsampleapp` feature set.

#### 17B — `LeaflektMap` iOS SPM

- [ ] **MarkerCluster Swift:** Add `LeaflektMarkerCluster` struct + `MarkerClusterOptions` to `Models/`. Extend `LeaflektMapController` with `addCluster(_:)/removeCluster(_:)`. Bundle Leaflet.markercluster JS/CSS in `Resources/leaflet/`.
- [ ] **MapOverlay Swift:** `LeaflektMapView` modifier `.overlay(position:content:)` — pins SwiftUI `View` to a map coordinate. Requires `WKWebView` projection callbacks (`registerOverlayPoint` already in map.html).
- [ ] **MapController richer API Swift:** Add `recenter()`, `setStyle(_:)`, `setZoomBounds(min:max:)` to `LeaflektMapController.swift`.
- [ ] **MarkerIcon Swift:** Add `LeaflektMarkerIcon` struct (base64 PNG, width/height/anchor). Extend `LeaflektMarker` with `icon: LeaflektMarkerIcon?`.
- [ ] **Marker rotation + alpha + zIndex Swift:** Add `rotationDegrees`, `alpha`, `zIndex` to `LeaflektMarker.swift`.
- [ ] **Custom info windows Swift:** Add `infoWindowContent: AnyView?` to `LeaflektMarker.swift`. Inject Compose-rendered HTML or native SwiftUI overlay.
- [ ] **GeoJSON overlay Swift:** Add `LeaflektGeoJsonOverlay` enum to `Models/`. Expose via `LeaflektMapProperties` or direct controller call. `setGeoJsonOverlay` already in `map.html`.
- [ ] **app-ios-native demo parity:** Update `ContentView.swift` to demo: clustering, directions route polyline, custom marker icons — matching `leaflektsampleapp` screens.

#### 17C — OlaMaps / Directions in CMP + iOS
- [ ] **Directions API client CMP:** Port `OlaMapsRepository.kt` to `commonMain` using Ktor (already in `libs.versions.toml`). Android already uses Ktor; iOS `actual` is a no-op passthrough.
- [ ] **DirectionsScreen CMP:** Add directions tab to `app-cmp/App.kt` — search origin/destination, fetch route, render `LeaflektPolyline`.
- [ ] **ClusteringScreen CMP:** Add clustering demo tab to `app-cmp/App.kt` using `LeaflektMarkerCluster`.
- [ ] **Directions iOS native demo:** Add `DirectionsView.swift` to `app-ios-native` — same flow as CMP version using `LeaflektMapController` for polyline.
- [ ] **Clustering iOS native demo:** Add clustering screen to `app-ios-native` using `LeaflektMarkerCluster`.
- [ ] **OLA Maps API keys:** Manage keys via `local.properties` / `BuildConfig` on Android; `Info.plist` on iOS. Document in README.

## Success Criteria
- [x] Map loads in Compose
- [x] 1:1 Google-style Marker API implemented
- [x] JitPack dependency verified (v0.1.0)
- [x] Dev integration path documented (<5 min local module integration)

## Tracking Notes
- Keep `:leaflekt` reusable and independent from `:app`
- `:app` remains a demo/sample surface with runtime tuning controls
- `:leaflektsampleapp` is a richer demo: Explore (places search), Directions (OlaMaps routing), Clustering tabs
- Phase 17 tracks CMP + iOS parity with the features shipped in `:leaflekt` v0.5.0 (`leaflektsampleapp`)
- Last updated: 2026-06-06

