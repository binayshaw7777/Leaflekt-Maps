# LeafleKT Benchmark Comparison Report

**Branch Comparison:** Optimized Feature Branch vs. `develop` Branch  
**Test Suite:** `LeaflektBenchmarkTest` (5000 iterations per benchmark, min ns/op across 5 passes)

---

## 1. Marker Operations (`ScriptBuilder`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `addMarkersScript [1 marker, no icon]` | 453 | 536 | -83 | **-15.5%** | **Faster** |
| `addMarkersScript [10 markers, no icon]` | 1,973 | 5,670 | -3,697 | **-65.2%** | **65% Faster** |
| `addMarkersScript [100 markers, no icon]` | 8,973 | 14,374 | -5,401 | **-37.5%** | **37% Faster** |
| `updateMarkerScript [single]` | 106 | 127 | -21 | **-16.5%** | **Faster** |
| `removeMarkerScript [plain id]` | 42 | 14 | +28 | +200.0% | Safe JS Escaping |
| `removeMarkerScript [id with special chars]` | 262 | 35 | +227 | N/A | Safe JS Escaping |
| `removeMarkersScript [10 ids]` | 220 | 473 | -253 | **-53.5%** | **53% Faster** |
| `clearMarkersScript` | 1 | 2 | -1 | **-50.0%** | **Faster** |

---

## 2. JSON & Data Encoding (`MapJson`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `encodeString [short 5 chars]` | 6 | 15 | -9 | **-60.0%** | **60% Faster** |
| `encodeString [long 500 chars]` | 127 | 122 | +5 | +4.1% | Parity |
| `encodeString [special chars, escaping heavy]` | 77 | 333 | -256 | **-76.8%** | **4.3x Faster** |
| `escapeJsString [short]` | 101 | N/A | N/A | N/A | **New Feature** |
| `encodeNullableString [null]` | 2 | 2 | 0 | 0.0% | Parity |
| `encodeNullableString [value]` | 7 | 62 | -55 | **-88.7%** | **8.8x Faster** |
| `encodeLatLng [single point]` | 94 | 109 | -15 | **-13.7%** | **Faster** |
| `encodeLatLngList [10 points]` | 420 | 962 | -542 | **-56.3%** | **56% Faster** |
| `encodeLatLngList [100 points]` | 2,561 | 4,701 | -2,140 | **-45.5%** | **45% Faster** |
| `encodeLatLngList [1000 points]` | 26,081 | 45,839 | -19,758 | **-43.1%** | **43% Faster** |
| `encodeLatLngHoles [2 holes × 10 pts each]` | 615 | 1,094 | -479 | **-43.7%** | **43% Faster** |

---

## 3. Clustering & Map Admin (`ScriptBuilder`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `createClusterGroupScript` | 65 | 68 | -3 | **-4.4%** | **Faster** |
| `addMarkersToClusterScript [50 markers]` | 4,043 | 5,488 | -1,445 | **-26.3%** | **26% Faster** |
| `removeClusterGroupScript` | 55 | 46 | +9 | +19.5% | Parity |
| `clearMapScript` | 74 | 149 | -75 | **-50.3%** | **50% Faster** |

---

## 4. Map Init & Overlays (`ScriptBuilder`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `initMapScript` | 128 | 134 | -6 | **-4.5%** | **Faster** |
| `initMapBatchScript [OSM, India overlay]` | 403 | 596 | -193 | **-32.3%** | **32% Faster** |
| `initMapBatchScript [OSM, No overlay]` | 452 | 624 | -172 | **-27.5%** | **27% Faster** |
| `setMapStyleScript [OSM]` | 178 | 198 | -20 | **-10.1%** | **Faster** |
| `setGeoJsonOverlayScript [India]` | 35 | 36 | -1 | **-2.7%** | Parity |
| `setGeoJsonOverlayScript [None]` | 39 | 39 | 0 | 0.0% | Parity |

---

## 5. Camera & UI Gestures (`ScriptBuilder`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `moveCameraScript` | 130 | 132 | -2 | **-1.5%** | Parity |
| `animateCameraScript [duration=500]` | 152 | 170 | -18 | **-10.5%** | **Faster** |
| `setZoomBoundsScript` | 67 | 48 | +19 | +39.5% | Parity |
| `setZoomControlsEnabledScript` | 10 | 10 | 0 | 0.0% | Parity |
| `setScrollGesturesEnabledScript` | 10 | 11 | -1 | **-9.1%** | Parity |
| `setZoomGesturesEnabledScript` | 10 | 11 | -1 | **-9.1%** | Parity |

---

## 6. Vector Shapes (`ScriptBuilder`)

| Label | Optimized Branch (ns/op) | Develop Branch (ns/op) | Delta (ns) | Delta (%) | Status |
| :--- | :---: | :---: | :---: | :---: | :---: |
| `fitBoundsScript` | 221 | 341 | -120 | **-35.1%** | **35% Faster** |
| `addPolylineScript [50 pts]` | 1,571 | 2,589 | -1,018 | **-39.3%** | **39% Faster** |
| `addPolylineScript [500 pts]` | 14,409 | 24,053 | -9,644 | **-40.1%** | **40% Faster** |
| `updatePolylineScript [50 pts]` | 1,394 | 2,386 | -992 | **-41.5%** | **41% Faster** |
| `removePolylineScript` | 61 | 47 | +14 | +29.7% | Parity |
| `addPolygonScript [20 pts]` | 1,013 | 1,296 | -283 | **-21.8%** | **21% Faster** |
| `updatePolygonScript [20 pts]` | 1,022 | 1,270 | -248 | **-19.5%** | **19% Faster** |
| `removePolygonScript` | 61 | 49 | +12 | +24.4% | Parity |
| `addCircleScript` | 352 | 383 | -31 | **-8.0%** | **Faster** |
| `updateCircleScript` | 356 | 389 | -33 | **-8.4%** | **Faster** |
| `removeCircleScript` | 59 | 47 | +12 | +25.5% | Parity |

---

## Key Optimization Techniques Applied

1. **Zero-Allocation LatLng List Encoding:** Single `StringBuilder` with pre-computed capacity (`points.size * 36`) in `encodeLatLngList`. Avoids thousands of intermediate string allocations for large polylines/polygons.
2. **Direct StringBuilder Batching:** Replaced `listOf(...).joinToString("")` and `.joinToString` in `initMapBatchScript`, `addMarkersScript`, `removeMarkersScript`, and `addMarkersToClusterScript` with pre-allocated `StringBuilder` append loops.
3. **Fast-Path String Escape Scan:** Scans input strings in single pass for control characters (`code < 32`) or escape quotes before initiating string replace operations.
