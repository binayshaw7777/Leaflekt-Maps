# Changelog 📋

All notable changes to LeafleKT will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.1.1] - 2026-08-08

### 🚀 Performance & Script Generation
- **Zero-Allocation LatLng Encoding:** Pre-allocated `StringBuilder` capacity calculations in `LeaflektMapJson.encodeLatLngList` avoiding thousands of intermediate object allocations for large polylines and polygons.
- **Direct Batch StringBuilder Loops:** Replaced `joinToString` allocations with pre-allocated buffer appends in `initMapBatchScript`, `addMarkersScript`, `removeMarkersScript`, and `addMarkersToClusterScript`.
- **Fast-Path String Escaping:** Implemented single-pass ASCII scanning for control characters and quotation marks before performing string replacement passes in `LeaflektMapJson.escapeJsString`.
- **Micro-Benchmark Suite:** Added `LeaflektBenchmarkTest` benchmark suite for `LeaflektMapJson` and `LeaflektScriptBuilder` hot-paths with verifiable sub-microsecond throughput across all map primitives.

### 🛡️ CMP & iOS Stability & Crash Fixes
- **Pre-warmed WKWebView Pooling:** Introduced `IosWebViewGlobals.claimPrewarmed` on iOS CMP to eliminate cold-start initialization latency and render maps instantaneously.
- **Lazy MapLibre GL Script Loading:** MapLibre GL JS engine and styles are now loaded lazily only on-demand when a vector style is requested, significantly cutting down initial map bundle parse time and memory overhead.
- **Weak Script Message Handler:** Stabilized `WeakScriptMessageHandler` callbacks on iOS to prevent retain cycles and memory leaks during Compose view lifecycle recompositions.
- **Synchronous Initial Camera Positioning:** Added explicit `moveCamera` dispatch during `initMap` to prevent initial viewport jump glitches on CMP iOS.
- **Modern iOS Architecture Targets:** Standardized KMP iOS targets to `iosArm64` and `iosSimulatorArm64` across `leaflekt-core`, `leaflekt-compose`, and `app-cmp`.

### 🎨 Map Styles & Features
- **OpenFreeMap Styles:** Built-in vector map style definitions for `OpenFreeMapBright`, `OpenFreeMapFiord`, and `OpenFreeMapLiberty`.
- **India GeoJSON Boundary Overlay:** Theme-aware boundary rendering with dynamic color adjustments for dark and light tile layers.
- **Shape Selections & Updates:** Dynamic polyline, polygon, and circle update APIs with preserved selection styling.

### 📦 Multiplatform Artifacts
- **Android & CMP:** `io.github.binayshaw7777:leaflekt-core:1.1.0` and `io.github.binayshaw7777:leaflekt-compose:1.1.0`
- **iOS SPM:** Swift Package Manager target `LeaflektMap` (`v1.1.0`)
