#!/usr/bin/env bash
# LeafleKT screenshot + GIF capture script
# Run from repo root: bash scripts/capture_screenshots.sh
#
# Prerequisites:
#   - Android emulator running with internet access
#   - App installed: ./gradlew :leaflektsampleapp:installDebug
#   - ffmpeg installed (for GIF conversion)
#   - (optional) gifsicle for GIF optimization

set -euo pipefail

PACKAGE="com.binayshaw7777.leaflektsampleapp"
ACTIVITY=".demo.DemoActivity"
SCREENSHOTS_DIR="docs/assets/screenshots"
GIFS_DIR="docs/assets/gifs"
TILE_WAIT=5  # seconds for map tiles to load

mkdir -p "$SCREENSHOTS_DIR" "$GIFS_DIR"

launch() {
  local screen="$1"
  shift
  adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "$screen" "$@" > /dev/null
  sleep "$TILE_WAIT"
}

screenshot() {
  local out="$1"
  adb exec-out screencap -p > "$out"
  echo "  -> $out"
}

echo "=== LeafleKT Screenshot Capture ==="
echo ""

# S3 — Markers
echo "[S3] Markers..."
launch "markers"
screenshot "$SCREENSHOTS_DIR/markers.png"

# S4 — Polylines
echo "[S4] Polylines..."
launch "polylines"
screenshot "$SCREENSHOTS_DIR/polylines.png"

# S5 — Polygons
echo "[S5] Polygons..."
launch "polygons"
screenshot "$SCREENSHOTS_DIR/polygons.png"

# S6 — Circles
echo "[S6] Circles..."
launch "circles"
screenshot "$SCREENSHOTS_DIR/circles.png"

# S7 — GeoJSON India
echo "[S7] GeoJSON India overlay..."
launch "geojson"
sleep 2  # India GeoJSON takes a bit extra
screenshot "$SCREENSHOTS_DIR/geojson_india.png"

# S8 — UI Settings
echo "[S8] UI Settings..."
launch "ui_settings"
screenshot "$SCREENSHOTS_DIR/ui_settings.png"

# S9 — Custom Icons
echo "[S9] Custom Icons..."
launch "custom_icons"
screenshot "$SCREENSHOTS_DIR/custom_icons.png"

# S2 — Map Styles (8 screenshots)
STYLE_NAMES=("osm" "carto_light" "carto_dark" "topo" "esri" "ofm_liberty" "ofm_fiord" "ofm_bright")
echo "[S2] Map styles (8 screenshots)..."
for i in "${!STYLE_NAMES[@]}"; do
  echo "  Style ${i}: ${STYLE_NAMES[$i]}"
  launch "map_styles" --ei STYLE_INDEX "$i"
  screenshot "$SCREENSHOTS_DIR/style_${STYLE_NAMES[$i]}.png"
done

# P1 — Map styles montage (requires ImageMagick)
if command -v montage &> /dev/null; then
  echo "[P1] Building map styles grid..."
  cd "$SCREENSHOTS_DIR"
  montage style_osm.png style_carto_light.png style_carto_dark.png style_topo.png \
          style_esri.png style_ofm_liberty.png style_ofm_fiord.png style_ofm_bright.png \
          -geometry 360x640+4+4 -tile 4x2 map_styles_grid.png
  cd - > /dev/null
  echo "  -> $SCREENSHOTS_DIR/map_styles_grid.png"
else
  echo "[P1] montage not found — skipping grid (install ImageMagick)"
fi

# S1 — Basic map (reuse markers shot, same screen)
echo "[S1] Basic map (copy of markers)..."
cp "$SCREENSHOTS_DIR/markers.png" "$SCREENSHOTS_DIR/basic_map.png"

# G1 — Clustering GIF
echo "[G1] Clustering GIF (8s recording)..."
adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "clustering" > /dev/null
sleep 4
adb shell screenrecord --bit-rate 8000000 --time-limit 8 /sdcard/clustering_demo.mp4
sleep 9
adb pull /sdcard/clustering_demo.mp4 "$GIFS_DIR/clustering.mp4"
if command -v ffmpeg &> /dev/null; then
  ffmpeg -y -i "$GIFS_DIR/clustering.mp4" \
    -vf "fps=12,scale=400:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
    "$GIFS_DIR/clustering.gif"
  echo "  -> $GIFS_DIR/clustering.gif"
else
  echo "  ffmpeg not found — raw mp4 at $GIFS_DIR/clustering.mp4"
fi

# G2 — Selection GIF note
echo ""
echo "[G2] Selection GIF requires manual interaction (tap on polyline to toggle)."
echo "     Launch manually: adb shell am start -n ${PACKAGE}/${ACTIVITY} --es SCREEN selection"
echo "     Then record:     adb shell screenrecord --time-limit 6 /sdcard/selection.mp4"
echo "     Pull + convert:  adb pull /sdcard/selection.mp4 && ffmpeg -i selection.mp4 -vf 'fps=12,scale=400:-1' docs/assets/gifs/selection_state.gif"

# G3 — Camera GIF
echo "[G3] Camera Animation GIF (7s recording)..."
adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "camera" > /dev/null
# LaunchedEffect handles the 3s delay before animation
adb shell screenrecord --bit-rate 8000000 --time-limit 7 /sdcard/camera_demo.mp4
sleep 8
adb pull /sdcard/camera_demo.mp4 "$GIFS_DIR/camera_animation.mp4"
if command -v ffmpeg &> /dev/null; then
  ffmpeg -y -i "$GIFS_DIR/camera_animation.mp4" \
    -vf "fps=12,scale=400:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
    "$GIFS_DIR/camera_animation.gif"
  echo "  -> $GIFS_DIR/camera_animation.gif"
else
  echo "  ffmpeg not found — raw mp4 at $GIFS_DIR/camera_animation.mp4"
fi

# P2 — GIF optimization
if command -v gifsicle &> /dev/null; then
  echo ""
  echo "[P2] Optimizing GIFs..."
  gifsicle --optimize=3 --batch "$GIFS_DIR"/*.gif 2>/dev/null || true
fi

echo ""
echo "=== Done ==="
echo "Screenshots: $SCREENSHOTS_DIR"
echo "GIFs:        $GIFS_DIR"
echo ""
echo "Next: update docs/SCREENSHOT_TASKS.md checkboxes, then insert image links in docs/README.md"
