# LeafleKT — Test Strategy

Platforms: Android native, iOS native, KMP core, CMP shared UI
UI tech: Jetpack Compose, SwiftUI, Compose Multiplatform
Rendering: WebView/WKWebView → Leaflet.js

---

## Test Layers

### Layer 1 — Unit Tests

| Module | What | Priority | Existing |
|--------|------|----------|----------|
| **leaflekt-core** | `LeaflektMapJson` encode (5 methods) | P0 | 2 tests |
| | `LeaflektScriptBuilder` JS output (28 scripts) | P0 | 0 |
| | `LeaflektColor` → CSS rgba conversion | P1 | 0 |
| | `LeaflektMapProperties`/`UiSettings` equality/copy | P1 | 0 |
| | `DeviceMemory` RAM calc | P1 | 0 |
| | `LeaflektControllerBase` script queue (pending→ready→flush) | P2 | 0 |
| **leaflekt-compose** | State Saver round-trips (camera, marker, polyline, polygon, circle) | P0 | 6 tests |
| | `GenerateId` uniqueness | P1 | 0 |
| | Shape state select/deselect/toggle | P0 | partial |
| | `LeaflektColorBridge` compose↔core | P1 | 0 |
| **LeaflektMap** (Swift) | All 8 model Equatable/Hashable/Codable | P0 | 3 tests |
| | `LeafktetMapStyle` computed props (tileUrl, attribution, maxZoom) | P1 | 0 |
| | Polyline decode algorithm | P1 | 0 |
| | `LeafktetMapController` script queue | P2 | 0 |
| **app-cmp** | `CmpOlaMapsViewModel` — debounce, state transitions | P0 | 0 |
| | `CmpOlaMapsRepository` — Ktor mock engine, all JSON shapes | P0 | 0 |
| | Polyline decoder (3 formats) | P0 | 0 |
| | `CmpDirectionsRoute` helpers (distanceLabel, durationLabel, cameraTarget) | P1 | 0 |
| **app-ios-native** | `SampleViewModel` — search debounce, place select, route fetch | P0 | 0 |
| | `OlaMapsService` — URL construction, response parsing | P0 | 0 |
| | Polyline decoder (all input formats) | P1 | 0 |
| | `DirectionsRoute` formatting | P1 | 0 |
| **leaflektsampleapp** | `OlaMapsViewModel` — debounce, distinctUntilChanged | P0 | 0 |
| | `OlaMapsRepository` — Ktor mock, all JSON response shapes | P0 | 0 |
| | Polyline decoder (5+ JSON schemas) | P0 | 0 |
| | Place model headline()/supportingLine() | P1 | 0 |

### Layer 2 — Screenshot / Snapshot Tests

| Module | Tool | What | Priority |
|--------|------|------|----------|
| **leaflekt-compose** | Roborazzi | 11 demo screens (mock WebView) | P1 |
| | | 8 style variants × key screens | P2 |
| | | Loading/empty/error state | P2 |
| **app** | Roborazzi | Launcher screen + demo screen scaffold | P1 |
| **app-cmp** | Roborazzi | 3 tab screens (Explore, Directions, Clustering) | P1 |
| **leaflektsampleapp** | Roborazzi | 3 tabs + 11 demo screens | P1 |
| **LeaflektMap** (iOS) | iOSSnapshotTestCase | Sample app screens | P2 |
| **app-ios-native** | iOSSnapshotTestCase | ContentView + SampleView | P2 |

**Challenge:** WebView renders tiles async. Screenshots capture container, not real tiles.
**Mitigation:** Mock `PlatformWebView` with placeholder, or inject JS to freeze tiles at capture.

### Layer 3 — Instrumented / UI Tests

| Platform | Tool | What | Priority |
|----------|------|------|----------|
| **Android** | Compose UI Test | Launcher→Demo/Sample nav | P0 |
| | | Bottom sheet: zoom, style, layer toggles | P0 |
| | | Marker tap→callback fires | P1 |
| | | Location permission grant/deny | P1 |
| | | Config change: rotation preserves state | P1 |
| | | TalkBack content descriptions | P2 |
| **iOS** | XCUITest | Tab nav (Explore↔Directions↔Clustering) | P0 |
| | | Search bar expand/collapse | P1 |
| | | Map style picker select | P1 |
| | | Location permission dialog | P1 |
| **CMP** | Compose UI Test (Android host) | Shared composable basic rendering | P1 |

### Layer 4 — Benchmarking

| Type | Tool | What | Priority |
|------|------|------|----------|
| **Android macro** | Macrobenchmark | Cold start → onMapReady (pool vs no pool) | P1 |
| | | Style switch: 8 styles, jank frames | P1 |
| | | Marker add: 10 / 100 / 500 / 1000 | P1 |
| | | Cluster: 100 / 500 / 1000 points calc time | P1 |
| | | Map scroll FPS (synthetic swipe) | P1 |
| **Android micro** | Microbenchmark | `LeaflektMapJson` encode throughput | P2 |
| | | `LeaflektScriptBuilder` string building | P2 |
| **iOS perf** | XCTest metrics | WKWebView load→onMapReady | P2 |
| | | Memory: 100 / 500 markers | P2 |

### Layer 5 — Manual / Exploratory

| Scenario | What | Priority |
|----------|------|----------|
| Device matrix | API 24–36, iOS 15–18, tablets, foldables | P1 |
| Network | Offline, slow 3G, airplane → tile fallback | P1 |
| Location | GPS off → permission → first fix | P1 |
| Tile providers | Blocked URLs in certain regions | P2 |
| Long session | 30+ min open: memory leaks | P2 |

---

## Implementation Phases

### Phase 1 (now)
- Fill unit test gaps across all modules (Layer 1)
- Fix broken `LeaflektCameraPositionStateTest` (out-of-sync API)
- Seed mock-based repo/decoder tests for app-cmp + leaflektsampleapp
- All module build passes with `test` task

### Phase 2 (short-term)
- Roborazzi for compose modules (mock WebView → placeholder)
- Compose UI Test for nav flows (launcher→screen)
- XCUITest for iOS tab nav
- Ktor MockEngine for all API tests
- Swift mock `URLProtocol` for OlaMapsService tests

### Phase 3 (medium-term)
- Android Macrobenchmark (cold start, style switch, markers)
- iOS snapshot tests
- CMP UI test common pattern
- CI pipeline: unit → screenshot → instrumentation → benchmark
- Long pole: WebView UI test (flaky, low ROI — defer)

---

## Tooling

| Need | Android/KMP | iOS |
|------|-------------|-----|
| Test runner | JUnit 5 + kotlin.test | XCTest |
| Mock HTTP | Ktor MockEngine | URLProtocol subclass |
| Screenshot | Roborazzi | iOSSnapshotTestCase |
| UI test | Compose UI Test + Espresso | XCUITest |
| Benchmark | Macrobenchmark + Microbenchmark | XCTest metrics (os_signpost) |
| Code coverage | Kover | xccov |
| CI | GitHub Actions (macOS runner for iOS) | — |

Key constraint: all Android screenshot/instrumented/benchmark tests need an emulator or device. KMP common tests run on JVM with no Android dependency.

---

## Existing Assets

- `docs/testcases/LEAFLEKT_TEST_PLAN.md` — 412-line functional test cases (14 categories)
- `docs/SCREENSHOT_TASKS.md` — ADB-based screenshot/GIF capture (uses demo Activity)
- `scripts/capture_screenshots.sh` — ADB automation script
- `leaflekt-core/src/commonTest/` — 2 unit tests
- `leaflekt-compose/src/commonTest/` — 6 unit tests
- `LeaflektMap/Tests/` — 3 Swift unit tests
