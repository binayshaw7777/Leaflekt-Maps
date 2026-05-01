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
- [ ] Directions journey playback sample with moving bike marker
- [ ] Route playback controls: start, pause, resume, stop
- [ ] Route playback speed controls using Compose segmented buttons
- [ ] Marker visibility toggle for journey playback
- [ ] Advanced Clustering plugin integration

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

## Success Criteria
- [x] Map loads in Compose
- [x] 1:1 Google-style Marker API implemented
- [x] JitPack dependency verified (v0.1.0)
- [x] Dev integration path documented (<5 min local module integration)

## Tracking Notes
- Keep `:leaflekt` reusable and independent from `:app`
- `:app` remains a demo/sample surface with runtime tuning controls
- Last updated: 2026-04-19

