# LeafleKT (Leaflet + Compose) - Execution Checklist

## Architecture (Locked)
- [x] Jetpack Compose UI + Android WebView + JavaScript Bridge + Leaflet.js stack implemented
- [x] Data flow wired: Compose -> Controller -> WebView -> JS Bridge -> Leaflet
- [x] Reverse callbacks wired: Leaflet -> JS Bridge -> Kotlin callbacks -> Compose state

## Refinement & Optimization (Session 0.3.0)
- [x] **Asset Optimization:** India boundary GeoJSON reduced from 6.6 MB to 313 KB (5% simplification).
- [x] **Branding Refactor:** All internal symbols migrated from `Leaflet*` to `Leaflekt*`.
- [x] **State Management:** 1:1 Google Maps Compose pattern implemented (`LeaflektCameraPositionState`).
- [x] **Declarative Markers:** Implemented child-based `LeaflektMarker` API with hoisted state support.
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
- [x] Refactor: Rename `LeafletMap` and internal wrapper symbols to `LeaflektMap` for brand consistency.
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
- [ ] Advanced Clustering plugin integration

### Phase 15 - Core Infrastructure
- [ ] **JNI Layer Migration:** Research migrating the bridge from JS-based to a native JNI layer for improved performance and direct engine access.
- [ ] Offline Tile caching system
- [ ] Custom Icon support (Bitmaps/Drawables)

## Success Criteria
- [x] Map loads in Compose
- [x] 1:1 Google-style Marker API implemented
- [x] JitPack dependency verified (v0.1.0)
- [x] Dev integration path documented (<5 min local module integration)

## Tracking Notes
- Keep `:leaflekt` reusable and independent from `:app`
- `:app` remains a demo/sample surface with runtime tuning controls
- Last updated: 2026-04-19
