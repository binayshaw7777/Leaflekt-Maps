# LeafleKT SDK v1.2 — June 2026 Plan

> **Goal**: Make LeafleKT the best native mobile map SDK for Compose & KMP.
> Theme: **Own the native mobile map space** — beat Leaflet on mobile, offer what Google Maps SDK should have been.

---

## Pillar 1 — Web Target (Close the Platform Gap)

Current gap: no web/JS target. mapcn owns React web. Leaflet owns vanilla JS web.

| Action | Why |
|--------|-----|
| **CMP → WASM target** | Compose Multiplatform already supports wasm-js. Port `leaflekt-compose` to web target. Same API, runs in browser via MapLibre GL JS (swap Leaflet.js for MapLibre GL JS on web) |
| **Adaptive tile renderer** | `map.html` → switch between Leaflet.js (mobile) and MapLibre GL JS (web) based on target |
| **Kotlin/Wasm demo** | New `app-web` module — show LeafleKT working in browser |

**Result**: One API — Android, iOS, Web. mapcn stays React-only.

---

## Pillar 2 — First-Party Feature Modules (SDK, Not Just Sample)

Current: search, directions, clusters live in sample app, not SDK.

### MV-1: `leaflekt-search` module
- CMP search bar composable with built-in Ola Maps / Mapbox / Google Places provider abstraction
- `expect/actual` for platform location
- Drop-in `< 10 line` setup

### MV-2: `leaflekt-directions` module
- `DirectionsRenderer` composable — origin/destination input, route polyline, eta card
- Provider abstraction (Ola, Mapbox, Google)
- Route playback engine (animated marker along polyline with speed controls)
- Journey recording (GPS -> polyline)

### MV-3: `leaflekt-clustering` module (already in core)
- Expose `MarkerCluster` config options publicly
- Cluster click expansion animation

### MV-4: `leaflekt-location` module
- `rememberCurrentLocation()` composable — unified location permission + updates
- Works on Android (FusedLocation) + iOS (CLLocationManager) + Web (Geolocation API)

**Result**: `implementation("io.github.binayshaw7777:leaflekt-search")` — not copy-paste from sample app.

---

## Pillar 3 — Tile & Style Improvements

| Action | Why |
|--------|-----|
| **Default to OpenFreeMap** | Switch default map style from CartoDark → OpenFreeMapBright. Zero license friction out of the box. |
| **Offline tile caching** | `MapProperties.offlineCache(region: LatLngBounds, zoom: IntRange)` — persistent tile cache for offline use |
| **Custom tile URL API** | `MapProperties.tileUrlTemplate("https://tile.example.com/{z}/{x}/{y}.png")` — for any custom tile server |
| **MapTiler / Stadia quick presets** | `MapStyle.MapTiler(id: String, key: String)`, `MapStyle.Stadia(style: StadiaStyle)` — built-in presets with API key param |

---

## Pillar 4 — Animation & UX APIs

Current state: maps are static. Need smooth transitions.

| API | Description |
|-----|-------------|
| `CameraPositionState.animateTo(target, duration)` | Smooth flyTo animation with duration |
| `MarkerState.animatePosition(from, to, duration)` | Marker movement animation (route playback core) |
| `MapProperties.gestureThreshold` | Configurable gesture sensitivity |
| `MapUiSettings.compassGravity` | Compass position customization |
| `MapUiSettings.scaleBarEnabled` | Show/hide scale bar |
| `Marker.bounceOnTap` | Subtle bounce animation on marker tap |
| `Polyline.animateAdd(duration)` | Draw polyline progressively |

---

## Pillar 5 — Performance & Reliability

| Action | Why |
|--------|-----|
| **JNI bridge research** (from PLAN_CHECKLIST Phase 15) | Replace JS bridge with native JNI/NavigationIntercept for lower latency |
| **Cluster performance benchmark** | 10k markers benchmark — publish results |
| **WebView pool** | Pre-warm WebView on app start for instant map load |
| **Map snapshot API** | `MapController.takeSnapshot(): Bitmap` — for sharing/thumbnail generation |
| **Tile loading indicator** | Built-in progress indicator when tiles are loading |
| **Error boundary** | Graceful degradation when WebView crashes (show fallback UI) |

---

## Pillar 6 — Developer Experience

| Action | Why |
|--------|-----|
| **Public roadmap** | GitHub Project board — "In Progress / Soon / Future" |
| **Migration guide: Google Maps → LeafleKT** | Side-by-side API comparison with code snippets. Single biggest adoption driver. |
| **Migration guide: Leaflet.js → LeafleKT** | For React Native / Web devs moving to native |
| **Playground app** (Android + iOS) | Interactive API explorer — toggle features live, see generated code |
| **Video demos** | 30-60s screen recordings per feature (twitter/LinkedIn fodder) |
| **Comparisons page** | LeafleKT vs MapLibre Native vs Google Maps vs mapcn |
| **API reference with runnable snippets** | Dokka + embedded code blocks |
| **Quickstart templates** | `git clone` a working project with search + map + cluster in 5 min |

---

## Pillar 7 — Ecosystem & Community

| Action | Why |
|--------|-----|
| **Compose Multiplatform iOS stability** | Can iOS run LeafleKT reliably via CMP today? If not, fix it. Huge unlock. |
| **Kotlin Multiplatform Wizard entry** | Get LeafleKT listed at https://kmp.jetbrains.com/ |
| **Awesome LeafleKT** curated list | Community plugins, examples, articles |
| **Sample apps** | Delivery tracking, Store locator, Ride hailing, Real-time fleet tracking |
| **GitHub Discussions** | Q&A, Show and Tell, Feature Requests |
| **X / Mastodon presence** | Regular feature showcases, tips, release announcements |

---

## Pillar 8 — Monetization (Optional)

If this becomes a business:

| Model | How |
|-------|-----|
| **Open Core** | Core SDK MIT (free). Paid modules: offline tiles, advanced clustering, heatmaps |
| **Pro support** | Enterprise Slack/Discord support for teams |
| **Tile proxy service** | Self-hosted tile proxy with caching, analytics (like Mapbox but self-sovereign) |
| **Sponsorship** | GitHub Sponsors + OpenCollective |

For now: **keep MIT free**. Grow adoption first.

---

## Priority Matrix

```
                   High Impact          Low Impact
Easy               ┌─────────────────┬─────────────────┐
                   │  Pillar 3 (tile  │  Docs / guides  │
                   │  defaults + API) │  (Pillar 6)     │
                   │  Pillar 6 (dx)   │                  │
                   ├─────────────────┼─────────────────┤
Hard               │  Pillar 1 (Web)  │  Pillar 7        │
                   │  Pillar 2 (mods) │  (community)     │
                   │  Pillar 5 (JNI)  │  Pillar 8        │
                   └─────────────────┴─────────────────┘
```

**Immediate (June 2026):** Pillars 3, 4, 6
**Near-term (Q3 2026):** Pillar 2 (leaflekt-search, leaflekt-directions)
**Long-term (Q4 2026+):** Pillar 1 (Web target), Pillar 5 (JNI)

---

## Current Status vs v1.2 Target

| Feature | Now | v1.2 Target |
|---------|-----|-------------|
| Map render | Leaflet.js via WebView | Same (+ MapLibre GL JS for Web) |
| Platform | Android + iOS (CMP) | Android + iOS + Web (WASM) |
| Search/Directions | Sample app only | `leaflekt-search`, `leaflekt-directions` modules |
| Default tiles | CartoDark (enterprise license trap) | OpenFreeMapBright (no restrictions) |
| Offline | None | `offlineCache()` API |
| Tile customization | MapStyle presets only | `tileUrlTemplate()` + presets |
| Animations | None | flyTo, marker animation, progressive draw |
| Cluster | Basic | Configurable + animated |
| Docs | Dokka reference | Migration guides, runnable snippets, playground app |
| WebView perf | Cold start | Pre-warmed pool, snapshots |

---

## How We Win vs Competitors

| vs | LeafleKT advantage | Gap to close |
|----|--------------------|--------------|
| **Google Maps SDK** | No API key needed, no billing, open source, lighter APK | Web target, richer animations |
| **MapLibre Native** | Compose-first API, simpler setup, higher-level composables | Raw MapLibre gives more control |
| **mapcn** | Native mobile + CMP, not React-only | Need Web target to compete on web |
| **Leaflet.js** | Native performance, platform APIs (GPS, permissions) | Web target lets Leaflet users migrate |
| **Mapbox** | Free, OSS, no pricing surprises | Feature depth (isochrones, traffic, 3D) |

---

## Next Actions

1. [ ] Write migration guide: Google Maps → LeafleKT (Pillar 6)
2. [ ] Switch default style to OpenFreeMapBright (Pillar 3)
3. [ ] Add `tileUrlTemplate` API to `MapProperties` (Pillar 3)
4. [ ] Extract leaflekt-search module from leaflektsampleapp (Pillar 2)
5. [ ] Extract leaflekt-directions module from leaflektsampleapp (Pillar 2)
6. [ ] Add `animateTo` on CameraPositionState (Pillar 4)
7. [ ] Create app-web (CMP → WASM) prototype (Pillar 1)
8. [ ] Add offline tile caching (Pillar 3)
9. [ ] Publish public roadmap on GitHub (Pillar 6)
10. [ ] Add scale bar + compass config to MapUiSettings (Pillar 4)
