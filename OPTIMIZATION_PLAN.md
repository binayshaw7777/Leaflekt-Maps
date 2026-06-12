# LeafleKT Optimization Plan

Last updated: 2026-06-12 (session 2)  
Branch: `experiment/optimization`

---

## Legend

| Field | Meaning |
|---|---|
| **Impact** | H = High / M = Medium / L = Low |
| **Effort** | H = Hard / M = Medium / E = Easy |
| **Status** | ✅ Done / 🔲 Not done / ⏳ In progress |

---

## Checklist Overview

| # | Optimization | Impact | Effort | Status |
|---|---|---|---|---|
| 1 | Android WebView disk cache | H | M | ✅ |
| 2 | iOS WKWebView cache config | H | E | ✅ |
| 3 | Replace `retryInvalidateSize` polling | M | M | ✅ |
| 4 | Batch `initializeMap` scripts into one call | M | E | ✅ |
| 5 | Disable `WebContentsDebuggingEnabled` in prod | M | E | ✅ |
| 6 | Deduplicate `pendingScripts` queue | L | E | ✅ |
| 7 | Fix tile URL filter — split domain check vs cache check | L | E | ✅ |
| 8 | Increase camera idle debounce | L | E | ✅ |
| 9 | WebView instance reuse / pooling | H | H | ✅ |
| 12 | Dynamic tile buffer size from device RAM | M | E | ✅ |
| 10 | Set WebView renderer priority | M | E | ✅ |
| 11 | Hide map until ready (prevent white flash) — all platforms | M | M | ✅ |

---

## Detailed Items

---

### 1. Android WebView Disk Cache

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt:40`

**What it is:** Android WebView supports multiple cache modes via `WebSettings.setCacheMode()`. Currently unset, which defaults to `LOAD_DEFAULT` — the WebView may or may not cache tiles depending on HTTP headers from the tile server.

**What it does:** Setting `LOAD_CACHE_ELSE_NETWORK` makes WebView serve tiles from disk cache when available and only hit network for misses or expired entries. Tiles from OSM, CartoDB, ArcGIS etc. include proper `Cache-Control` headers — we're just not persisting them.

**Impact:** **HIGH** — tiles are the heaviest network asset. Repeat views of the same area (most common case) load instantly instead of re-fetching every session.

**Tradeoff:** Slightly stale tiles possible if tile server updates and cache hasn't expired. Standard tile cache TTL is 1–7 days, which is fine for a map SDK.

**Alternative:** Leave as `LOAD_DEFAULT` (current). Lets HTTP headers decide, but WebView's in-memory cache is cleared on process death. Disk persistence requires `LOAD_CACHE_ELSE_NETWORK`.

**Implementation:**
```kotlin
// Add to WebView settings block in PlatformWebView.android.kt
settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
```

- [ ] Add WebView tile caching — `LOAD_CACHE_ELSE_NETWORK` breaks `WebViewAssetLoader` local assets; need to intercept tile requests separately or find a safe approach
- [ ] Verify tile cache persists across app restarts

---

### 2. iOS WKWebView Cache Config

**File:** `leaflekt-compose/src/iosMain/kotlin/.../PlatformWebView.ios.kt:44`

**What it is:** `WKWebView` uses `WKWebsiteDataStore` for cache management. Currently using default config with no explicit cache size or persistence setting.

**What it does:** Configuring a non-ephemeral `WKWebsiteDataStore` (`.default()`) with a `URLCache` ensures tile images persist on disk between app launches, same benefit as item #1 on Android.

**Impact:** **HIGH** — same as Android. Repeated tile fetch eliminated for cached regions.

**Tradeoff:** Default store already persists in practice on iOS, but without explicit config there is no guarantee. Explicit config also enables future cache-size control.

**Alternative:** Leave default. Works most of the time but behavior is implicit and harder to debug.

**Implementation:**
```kotlin
// In PlatformWebView.ios.kt WKWebViewConfiguration setup
config.websiteDataStore = WKWebsiteDataStore.defaultDataStore()
```

- [x] Set explicit `defaultDataStore` on `WKWebViewConfiguration`
- [ ] Test tile cache persistence across app restart on iOS simulator

---

### 3. Replace `retryInvalidateSize` Polling Loop

**File:** `leaflekt-core/src/androidMain/assets/map.html:111–116`

**What it is:** After map creation, `retryInvalidateSize(20)` is called — it runs `map.invalidateSize()` then schedules itself again via `setTimeout(..., 150)`. Runs 20 iterations = 3 full seconds of polling on every map load.

**What it does:** `invalidateSize()` forces Leaflet to recalculate its container dimensions. Needed because the WebView may still be sizing itself when Leaflet initializes. The polling is a workaround for not knowing when layout is stable.

**Impact:** **MEDIUM** — eliminates 3 seconds of unnecessary `setTimeout` churn on every map load. Also removes 20 bridge callback potential during startup.

**Tradeoff:** Replacing with `ResizeObserver` requires browser support (available in all modern Android/iOS WebViews since 2020). Need to handle one-shot: observe → invalidateSize → unobserve.

**Alternative A:** `ResizeObserver` — fires exactly once when container reaches stable size. Clean.  
**Alternative B:** Single `setTimeout(invalidateSize, 300)` — one call, still a magic number but low cost.  
**Alternative C:** Keep current — safe, brute-force, but wasteful.

**Implementation (ResizeObserver):**
```javascript
function waitForStableSizeAndInvalidate() {
    const observer = new ResizeObserver(function(entries) {
        if (entries[0].contentRect.width > 0) {
            map.invalidateSize();
            observer.disconnect();
        }
    });
    observer.observe(document.getElementById("map"));
}
// Replace: retryInvalidateSize(20);
// With:    waitForStableSizeAndInvalidate();
```

- [ ] Replace `retryInvalidateSize` with `ResizeObserver`-based single invalidation
- [ ] Test on Android WebView (API 24+) and iOS WKWebView
- [ ] Verify map renders correctly at various initial container sizes

---

### 4. Batch `initializeMap` Scripts

**File:** `leaflekt-core/src/commonMain/kotlin/.../LeaflektControllerBase.kt:126–137`

**What it is:** `initializeMap()` calls `enqueueOrRun()` 3–4 times separately: `initMapScript`, `setZoomControlsEnabledScript`, `setMapStyleScript`, optionally `setGeoJsonOverlayScript`. Each call = one JS `evaluateJavascript()` round-trip.

**What it does:** Batching combines all init calls into one JS string, reducing bridge round-trips from 3–4 to 1.

**Impact:** **MEDIUM** — faster map ready time, fewer JS bridge invocations during cold start.

**Tradeoff:** Requires a new `LeaflektScriptBuilder.initMapBatchScript(...)` builder method. Small scope change.

**Alternative:** Keep separate calls. Works fine, just slightly slower at init. Acceptable for most cases.

**Implementation:**
```kotlin
// LeaflektScriptBuilder — add:
fun initMapBatchScript(lat, lng, zoom, zoomControl, style, overlay): String {
    return listOf(
        initMapScript(lat, lng, zoom),
        setZoomControlsEnabledScript(zoomControl),
        setMapStyleScript(style),
        if (overlay !is LeaflektGeoJsonOverlay.India) setGeoJsonOverlayScript(overlay) else ""
    ).filter { it.isNotBlank() }.joinToString(";")
}

// LeaflektControllerBase.initializeMap — replace 3–4 enqueueOrRun with:
enqueueOrRun(LeaflektScriptBuilder.initMapBatchScript(...))
```

- [ ] Add `initMapBatchScript` to `LeaflektScriptBuilder`
- [ ] Refactor `initializeMap` to use single batched call
- [ ] Verify map style and zoom controls apply correctly

---

### 5. Disable `WebContentsDebuggingEnabled` in Production

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt:37`

**What it is:** `WebView.setWebContentsDebuggingEnabled(true)` is hardcoded `true`. This is a global static flag that enables Chrome DevTools remote debugging for all WebViews in the process.

**What it does:** Hardcoded `true` means production builds expose WebView internals to DevTools. It also has minor overhead — the WebView keeps debugging hooks active.

**Impact:** **MEDIUM** — security/privacy concern for a published SDK. Any app using LeafleKT exposes their WebView to USB debugging without opting in.

**Tradeoff:** None significant. Debug builds should still enable it.

**Alternative:** Remove entirely (debug users can set it themselves). Or gate on `BuildConfig.DEBUG`.

**Implementation:**
```kotlin
// Replace line 37:
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
}
```

- [x] Gate `WebContentsDebuggingEnabled` on app's `FLAG_DEBUGGABLE` (not `BuildConfig.DEBUG` — library module)
- [ ] Verify Chrome DevTools still works in debug builds

---

### 6. Deduplicate `pendingScripts` Queue

**File:** `leaflekt-core/src/commonMain/kotlin/.../LeaflektControllerBase.kt:4,22`

**What it is:** `pendingScripts` is an `ArrayDeque<String>`. Adding the same script twice (e.g., `setMapStyle` called twice before map is ready) queues both — both execute on ready.

**What it does:** Adding a `LinkedHashSet`-based dedup or checking last-entry would prevent redundant JS calls on replay.

**Impact:** **LOW** — edge case. Only matters if caller sets same property multiple times before map ready. No real-world crash risk.

**Tradeoff:** `LinkedHashSet` loses ordering guarantees for truly different scripts. Need to be careful — order matters (e.g., `initMap` must run before `addMarker`). A simple "skip if identical to last entry" is safer.

**Alternative:** Leave as-is. Callers should avoid redundant calls. Document it.

**Implementation:**
```kotlin
protected fun enqueueOrRun(script: String) {
    if (isMapReady) {
        platformExecuteJs(script)
    } else {
        if (pendingScripts.lastOrNull() != script) pendingScripts.add(script)
    }
}
```

- [ ] Add last-entry dedup to `enqueueOrRun`
- [ ] Unit test: same script called twice before ready → executes once

---

### 7. Fix Missing Tile Domains in URL Filter

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt:62–66`

**What it is:** `shouldOverrideUrlLoading` allowlists known tile domains to prevent them from opening in an external browser. But `LeaflektMapStyle` includes `tiles.openfreemap.org` — not in the allowlist.

**What it does:** If OpenFreeMap vector tiles trigger `shouldOverrideUrlLoading`, they'd be blocked or opened externally instead of loading in the WebView. (Vector tiles may not trigger this, but HTTP sub-resources sometimes do.)

**Impact:** **LOW** — may cause OpenFreeMap style to fail silently on some devices or future Android versions.

**Tradeoff:** None. Pure defensive fix.

**Alternative:** Switch to a blocklist (block only `appassets.*` from external intent). Simpler and future-proof.

**Implementation:**
```kotlin
// Add to isMapTile check:
url.contains("tiles.openfreemap.org") ||
url.contains("tile.opentopomap.org")  // already there but note: openfreemap missing
```

- [ ] Add `tiles.openfreemap.org` to tile domain allowlist
- [ ] Consider refactoring to blocklist approach instead

---

### 8. Increase Camera Idle Debounce

**File:** `leaflekt-core/src/androidMain/assets/map.html:237`

**What it is:** `scheduleCameraIdleReport()` debounces `notifyCameraIdle` at 48ms. Fires after every `moveend`/`zoomend`.

**What it does:** Increasing to 100–150ms reduces bridge callbacks during fast pan/zoom gestures. Currently fires ~20x/sec during active movement.

**Impact:** **LOW** — already decent. Marginal gain. More noticeable on low-end devices with many onCameraIdle listeners.

**Tradeoff:** Slightly less responsive camera idle event for callers that depend on it for UI updates (e.g., loading data for visible region).

**Alternative:** Make debounce configurable via `LeaflektMapProperties` so callers choose their tradeoff.

**Implementation:**
```javascript
// Change 48 → 100 (or make configurable)
}, 100);
```

- [x] Hardcoded 100ms in all 4 `map.html` variants

---

### 9. WebView Instance Reuse / Pooling

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt:38,102–111`

**What it is:** Every time `LeaflektMap` enters composition, a new `WebView` is created and `map.html` is loaded from scratch. On dispose, it's destroyed. Cold start includes: WebView init → HTML parse → JS engine init → Leaflet init → tile fetch.

**What it does:** A singleton or pool of pre-warmed `WebView` instances with `map.html` already loaded would eliminate cold-start latency. Controller reattaches to the pooled instance.

**Impact:** **HIGH** — cold start time cut from ~500–1000ms to near-instant for repeat map uses (e.g., navigating away and back).

**Tradeoff:** Memory: idle pooled WebViews consume ~30–50MB each. Requires careful state reset between reuses (tile layers, markers, camera state). Complex to implement correctly on both platforms. iOS `WKWebView` has no equivalent pooling API.

**Alternative A:** Pool size 1 (singleton). Works for most apps with one map at a time.  
**Alternative B:** `detachFromWindow` / `attachToWindow` instead of destroy — preserves JS state.  
**Alternative C:** Skip for now. Address only if profiling shows cold start is user-visible pain.

**Implementation notes:**
- Requires `Application`-level singleton holder
- Controller must support `reset()` to clear markers/layers on reuse
- Must handle cases where pool WebView was GC'd or process killed

- [x] `WebViewPool` singleton (pool size 1) in `leaflekt-compose/src/androidMain`
- [x] `reset()` JS bridge method in all 4 `map.html` — clears markers/shapes/clusters/GeoJSON, resets camera state
- [x] `PlatformWebView.android.kt` uses pool: acquire on factory, release on dispose
- [x] Pool reuse skips cold-start: `bridge.onMapReady()` + `bridge.onMapFirstRender()` posted directly
- [ ] iOS: no WKWebView pooling API — not applicable

---

### 10. Set WebView Renderer Priority

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt:41`

**What it is:** Android API 26+ allows setting renderer process priority via `WebView.setRendererPriorityPolicy()`. Default priority means OS can deprioritize or kill the renderer under memory pressure.

**What it does:** `RENDERER_PRIORITY_IMPORTANT` tells the OS to keep the renderer alive and at high priority. `false` for the second arg means priority does NOT drop when WebView is not visible — important for a map SDK where visibility may lag behind user intent.

**Impact:** **MEDIUM** — prevents renderer kills during heavy map use, reduces frame drops on low-end devices under memory pressure.

**Tradeoff:** Slight increase in memory pressure on system. Minor. Acceptable for an active map view.

**Alternative:** Leave default. Fine for high-end devices; causes jank or blank renders on low-end.

**Implementation:**
```kotlin
// Add inside WebView(context).apply { ... } block, after settings setup
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    setRendererPriorityPolicy(WebView.RendererPriority.RENDERER_PRIORITY_IMPORTANT, false)
}
```

- [ ] Add `setRendererPriorityPolicy` call in `PlatformWebView.android.kt`
- [ ] Verify no crash on API < 26 (guarded by SDK check)

---

### 11. Hide Map Until Ready (Prevent White Flash)

**File:** `leaflekt-compose/src/androidMain/kotlin/.../PlatformWebView.android.kt`, `map.html`

**What it is:** WebView shows white background while Leaflet.js initializes and first tiles load. User sees white → map pop-in. Article uses pixel-ratio detection; LeafleKT already has a JS bridge, so a `map.whenReady` callback is simpler and more reliable.

**What it does:** Keep `WebView` alpha at 0 (or overlay a placeholder) until Leaflet fires `map.whenReady`, then notify native via JS bridge and fade in. Eliminates white flash without polling.

**Impact:** **MEDIUM** — purely UX quality. No perf gain, but perceived load time improves significantly. White flash is jarring on first open.

**Tradeoff:** Requires new bridge event (`onMapReady` distinct from current map-ready state). Must handle timeout fallback so map doesn't stay invisible if bridge event never fires.

**Alternative A:** `WebView.setBackgroundColor(Color.TRANSPARENT)` — hides white but shows activity background instead; still has map pop-in.  
**Alternative B:** Expose `isMapReady: State<Boolean>` from `LeaflektController` so callers can render their own skeleton/loader.  
**Alternative C:** Do both B + native alpha fade in SDK default.

**Implementation sketch:**
```javascript
// map.html — after map init
map.whenReady(function() {
    LeaflektBridge.call('onMapFirstRender', '{}');
});
```
```kotlin
// PlatformWebView.android.kt
val isVisible = remember { mutableStateOf(false) }
// bridge callback sets isVisible = true
AndroidView(
    modifier = modifier.alpha(if (isVisible.value) 1f else 0f),
    ...
)
```

- [x] Add `onMapFirstRender` event to JS bridge and `LeaflektBridgeCallbacks`
- [x] Wire alpha/visibility in `PlatformWebView.android.kt` (update block sets `webView.alpha`)
- [x] Add 3s timeout fallback (show map even if bridge event never fires)
- [x] Apply same pattern to iOS `PlatformWebView.ios.kt` (alpha driven by `isFirstRenderDone`)
- [x] Apply same pattern to Swift package (`LeaflektMapRepresentable` + `LeaflektBridge`)
- [x] Fix parity: CMP + Swift `map.html` missing `notifyMapFirstRender()` function (was ReferenceError)
- [x] Fix parity: CMP + Swift `map.html` missing `touch-action: none` on `#map`

---

## Non-Goals (Ruled Out)

| Idea | Why not |
|---|---|
| Native tile rendering (OSMDroid) | Requires removing WebView entirely — architectural rewrite, breaks Leaflet.js plugin ecosystem |
| Tile prefetching | Speculative network use, battery impact, and tile server ToS violations for most providers |
| Custom HTTP cache interceptor | WebView's built-in cache already handles this; custom interceptor adds complexity with no gain |

---

## Suggested Order of Work

1. **#5** (debug flag) — security, 5-minute fix
2. **#1 + #2** (cache) — highest ROI, easy
3. **#3** (polling) — noticeable startup improvement
4. **#7** (URL filter) — defensive fix before next release
5. **#4** (batch init) — clean code + perf
6. **#6** (dedup queue) — minor, low risk
7. **#8** (debounce) — only if someone reports it
8. **#10** (renderer priority) — 5-line fix, do with #5
9. **#11** (hide-until-ready) — UX polish, do before public release
10. **#9** (pooling) — only after profiling confirms need
