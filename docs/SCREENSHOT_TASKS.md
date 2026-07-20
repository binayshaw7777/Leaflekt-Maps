# LeafleKT — README Screenshot Automation Tasks

> **Context for agents:** LeafleKT is a Kotlin Multiplatform Leaflet.js SDK. The map renders via
> `WebView` (Leaflet.js), so Compose Preview / Paparazzi / Roborazzi **cannot** capture tiles.
> All screenshots require a real Android emulator or device with internet access.
>
> **Package:** `com.binayshaw7777.leaflektsampleapp`
> **Demo entry point:** `com.binayshaw7777.leaflektsampleapp/.demo.DemoActivity`
> **ADB script:** `scripts/capture_screenshots.sh`
> **Output dir:** `docs/assets/screenshots/` and `docs/assets/gifs/`

---

## Setup (do once before any capture task)

- [ ] Android emulator running with internet access (API 30+)
- [ ] Build + install: `./gradlew :leaflektsampleapp:installDebug`
- [ ] Grant location permission (for location dot demo):
  `adb shell pm grant com.binayshaw7777.leaflektsampleapp android.permission.ACCESS_FINE_LOCATION`
- [ ] Create output dirs:
  `mkdir -p docs/assets/screenshots docs/assets/gifs`

---

## Screenshot Tasks

### S1 — Quick Start / Basic Map
- [ ] **Status:** TODO
- **Screen:** `SCREEN=markers`
- **Captures:** `docs/assets/screenshots/basic_map.png`
- **Shows:** Single marker on Kolkata, zoom 12, OSM tiles
- **README section:** Quick Start

### S2 — Map Styles Grid (8 screenshots)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=map_styles&STYLE_INDEX=0..7`
- **Captures:**
  - `docs/assets/screenshots/style_osm.png` (index 0)
  - `docs/assets/screenshots/style_carto_light.png` (index 1)
  - `docs/assets/screenshots/style_carto_dark.png` (index 2)
  - `docs/assets/screenshots/style_topo.png` (index 3)
  - `docs/assets/screenshots/style_esri.png` (index 4)
  - `docs/assets/screenshots/style_ofm_liberty.png` (index 5)
  - `docs/assets/screenshots/style_ofm_fiord.png` (index 6)
  - `docs/assets/screenshots/style_ofm_bright.png` (index 7)
- **README section:** Available Map Styles
- **Note:** Compose these 8 into a single 2×4 grid image using ImageMagick after capture:
  `montage style_*.png -geometry 360x640+4+4 -tile 4x2 map_styles_grid.png`

### S3 — Markers (basic + rotated)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=markers`
- **Captures:** `docs/assets/screenshots/markers.png`
- **Shows:** 3 markers — basic, titled, rotated 45°
- **README section:** Markers

### S4 — Polylines (solid + dashed + dotted)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=polylines`
- **Captures:** `docs/assets/screenshots/polylines.png`
- **Shows:** 3 polylines: solid blue, dashed red, dotted green
- **README section:** Polylines

### S5 — Polygon with hole (donut)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=polygons`
- **Captures:** `docs/assets/screenshots/polygons.png`
- **Shows:** Outer polygon + inner hole cutout
- **README section:** Polygons

### S6 — Circles
- [ ] **Status:** TODO
- **Screen:** `SCREEN=circles`
- **Captures:** `docs/assets/screenshots/circles.png`
- **Shows:** 3 concentric circles with different fill/stroke colors
- **README section:** Circles

### S7 — GeoJSON India Overlay
- [ ] **Status:** TODO
- **Screen:** `SCREEN=geojson`
- **Captures:** `docs/assets/screenshots/geojson_india.png`
- **Shows:** India boundary overlay on Carto Light, zoomed to show full country
- **README section:** GeoJSON Overlays

### S8 — UI Settings
- [ ] **Status:** TODO
- **Screen:** `SCREEN=ui_settings`
- **Captures:** `docs/assets/screenshots/ui_settings.png`
- **Shows:** Zoom controls, location button, and blue dot
- **README section:** Map UI Settings

### S9 — Custom Marker Icons
- [ ] **Status:** TODO
- **Screen:** `SCREEN=custom_icons`
- **Captures:** `docs/assets/screenshots/custom_icons.png`
- **Shows:** Multiple markers with different color icons from URLs
- **README section:** Custom Marker Icons

---

## GIF Tasks

### G1 — Marker Clustering (zoom in/out)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=clustering`
- **Captures:** `docs/assets/gifs/clustering.gif`
- **Flow:** Record 8s — pinch zoom in (expand clusters) then zoom out (collapse)
- **README section:** Marker Clustering
- **Convert:** `ffmpeg -i clustering.mp4 -vf "fps=12,scale=400:-1" docs/assets/gifs/clustering.gif`

### G2 — Selection State (polyline tap toggle)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=selection`
- **Captures:** `docs/assets/gifs/selection_state.gif`
- **Flow:** Record 5s — tap polyline (highlights), tap again (clears)
- **README section:** Selection State
- **Note:** ADB tap coordinates depend on emulator resolution. Default tap at center of polyline: `adb shell input tap 540 960`

### G3 — Camera Animation (Kolkata → Delhi)
- [ ] **Status:** TODO
- **Screen:** `SCREEN=camera`
- **Captures:** `docs/assets/gifs/camera_animation.gif`
- **Flow:** Record 7s — wait 3s for tiles, then smooth animation to Delhi with zoom
- **README section:** Camera State

---

## Post-processing Tasks

### P1 — Map styles montage
- [ ] **Status:** TODO (depends on S2)
- `cd docs/assets/screenshots && montage style_*.png -geometry 360x640+4+4 -tile 4x2 map_styles_grid.png`

### P2 — GIF optimization
- [ ] **Status:** TODO (depends on G1, G2)
- `gifsicle --optimize=3 docs/assets/gifs/*.gif`

### P3 — README image links
- [ ] **Status:** TODO (depends on all captures)
- Insert `![alt](docs/assets/screenshots/name.png)` at correct README sections

---

## ADB Command Reference

```bash
# Install
./gradlew :leaflektsampleapp:installDebug

# Launch a demo screen
adb shell am start -n com.binayshaw7777.leaflektsampleapp/.demo.DemoActivity \
  --es SCREEN "markers"

# Launch map style by index (0=OSM, 1=CartoLight, 2=CartoDark, 3=Topo, 4=Esri, 5=OFM_Liberty, 6=OFM_Fiord, 7=OFM_Bright)
adb shell am start -n com.binayshaw7777.leaflektsampleapp/.demo.DemoActivity \
  --es SCREEN "map_styles" --ei STYLE_INDEX 2

# Screenshot
adb exec-out screencap -p > docs/assets/screenshots/output.png

# Screen record (stop with Ctrl+C or after --time-limit)
adb shell screenrecord --bit-rate 8000000 --time-limit 8 /sdcard/demo.mp4
adb pull /sdcard/demo.mp4 docs/assets/gifs/

# Convert to GIF
ffmpeg -i docs/assets/gifs/demo.mp4 -vf "fps=12,scale=400:-1" docs/assets/gifs/demo.gif
```

---

## Notes for agents

- WebView tile load takes **2–4 seconds** after activity start — always `sleep 4` before screenshot
- Emulator needs **internet** — tiles won't load on offline/restricted emulators
- Tiles load asynchronously — a slight blur/loading artifact is acceptable for docs
- All demo screens use **Kolkata (22.5726, 88.3639)** as base location for consistency
- Demo screens are in `leaflektsampleapp` only — **zero SDK changes**
- `DemoActivity` is the sole entry point; routes by `SCREEN` intent extra
- Run `scripts/capture_screenshots.sh` to automate all S1–S7 + G1 in sequence
