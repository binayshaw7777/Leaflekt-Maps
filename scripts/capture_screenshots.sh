#!/usr/bin/env bash
# LeafleKT screenshot + GIF capture script
# Run from repo root: bash scripts/capture_screenshots.sh
#
# Prerequisites:
#   - Android emulator running with internet access
#   - App installed: ./gradlew :leaflektsampleapp:installDebug
#   - ffmpeg installed (for GIF conversion)
#   - (optional) gifsicle for GIF optimization

set -eo pipefail

PACKAGE="com.binayshaw7777.leaflektsampleapp"
ACTIVITY=".demo.DemoActivity"
SCREENSHOTS_DIR="docs/assets/screenshots"
GIFS_DIR="docs/assets/gifs"

# Force-stop app, launch screen, wait for tiles
launch() {
  local screen="$1"
  shift
  adb shell am force-stop "$PACKAGE" 2>/dev/null || true
  sleep 1
  adb logcat -c 2>/dev/null || true
  adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "$screen" "$@" > /dev/null 2>&1
  echo "  Waiting for tiles..."

  local found=0
  for attempt in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20; do
    if adb logcat -d 2>/dev/null | grep -q "MAP_READY"; then
      found=1
      break
    fi
    sleep 1
  done

  if [ "$found" -eq 0 ]; then
    echo "  Warning: MAP_READY not seen after 20s, proceeding..."
  else
    echo "  Map ready! Waiting for tiles to render..."
  fi

  # Tiles download async after MAP_READY — emulator needs extra time
  sleep 10
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
sleep 4
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
echo "[S2] Map styles (8 screenshots)..."
STYLE_NAMES="osm carto_light carto_dark topo esri ofm_liberty ofm_fiord ofm_bright"
idx=0
for name in $STYLE_NAMES; do
  echo "  Style $idx: $name"
  launch "map_styles" --ei STYLE_INDEX "$idx"
  screenshot "$SCREENSHOTS_DIR/style_${name}.png"
  idx=$((idx + 1))
done

# P1 — Map styles montage (requires ImageMagick)
if command -v montage > /dev/null 2>&1; then
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

# S1 — Basic map (reuse markers)
echo "[S1] Basic map (copy of markers)..."
cp "$SCREENSHOTS_DIR/markers.png" "$SCREENSHOTS_DIR/basic_map.png"
echo "  -> $SCREENSHOTS_DIR/basic_map.png"

# G1 — Clustering GIF
echo "[G1] Clustering GIF (8s recording)..."
adb shell am force-stop "$PACKAGE" 2>/dev/null || true
sleep 1
adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "clustering" > /dev/null 2>&1
sleep 12
adb shell screenrecord --bit-rate 8000000 --time-limit 8 /sdcard/clustering_demo.mp4
sleep 9
adb pull /sdcard/clustering_demo.mp4 "$GIFS_DIR/clustering.mp4"
if command -v ffmpeg > /dev/null 2>&1; then
  ffmpeg -y -i "$GIFS_DIR/clustering.mp4" \
    -vf "fps=12,scale=400:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
    "$GIFS_DIR/clustering.gif" 2>/dev/null
  echo "  -> $GIFS_DIR/clustering.gif"
else
  echo "  ffmpeg not found — raw mp4 at $GIFS_DIR/clustering.mp4"
fi

# G2 — Selection GIF (manual)
echo ""
echo "[G2] Selection GIF requires manual interaction:"
echo "     adb shell am start -n ${PACKAGE}/${ACTIVITY} --es SCREEN selection"
echo "     adb shell screenrecord --time-limit 6 /sdcard/selection.mp4"
echo "     adb pull /sdcard/selection.mp4 && ffmpeg -i selection.mp4 -vf 'fps=12,scale=400:-1' docs/assets/gifs/selection_state.gif"
echo ""

# G3 — Camera GIF
echo "[G3] Camera Animation GIF (7s recording)..."
adb shell am force-stop "$PACKAGE" 2>/dev/null || true
sleep 1
adb shell am start -n "${PACKAGE}/${ACTIVITY}" --es SCREEN "camera" > /dev/null 2>&1
sleep 3
adb shell screenrecord --bit-rate 8000000 --time-limit 7 /sdcard/camera_demo.mp4
sleep 8
adb pull /sdcard/camera_demo.mp4 "$GIFS_DIR/camera_animation.mp4"
if command -v ffmpeg > /dev/null 2>&1; then
  ffmpeg -y -i "$GIFS_DIR/camera_animation.mp4" \
    -vf "fps=12,scale=400:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse" \
    "$GIFS_DIR/camera_animation.gif" 2>/dev/null
  echo "  -> $GIFS_DIR/camera_animation.gif"
else
  echo "  ffmpeg not found — raw mp4 at $GIFS_DIR/camera_animation.mp4"
fi

# P2 — GIF optimization
if command -v gifsicle > /dev/null 2>&1; then
  echo ""
  echo "[P2] Optimizing GIFs..."
  gifsicle --optimize=3 --batch "$GIFS_DIR"/*.gif 2>/dev/null || true
fi

echo ""
echo "=== Done ==="
echo "Screenshots: $SCREENSHOTS_DIR"
echo "GIFs:        $GIFS_DIR"
