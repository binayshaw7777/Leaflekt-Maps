# Leaflekt Sample App Plan

## Current Change

- [x] Define bottom navigation sample flow for Explore and Directions
- [x] Extract reusable map and search UI into internal composables
- [x] Add directions search state for origin and destination place lookup
- [x] Add Ola Maps directions request and route decoding
- [x] Plot route polyline and endpoints on the directions map
- [x] Verify `:leaflektsampleapp:compileDebugKotlin`

## Next Change

- [x] Expose public custom marker icon API in `:leaflekt`
- [x] Expose public marker rotation support in `:leaflekt`
- [x] Expose public custom info window support in `:leaflekt`
- [x] Implement sample-only async marker icon loading with Coil
- [x] Add sample demonstrating app-level remote icon loading
- [x] Add info window anchor customization and initial visibility control
- [x] Add composable marker icon API (`iconContent`) with anchor control
- [x] Add sample demonstrating composable marker icon in sample app
- [x] Fix overscrolling by enforcing min/max zoom bounds (default 2–19)
- [x] Confirm pinch zoom is smooth and continuous (zoomSnap: false)
- [ ] Add journey playback controls on Directions screen
- [ ] Add bike marker visibility toggle and playback state
- [ ] Animate bike movement from route source to destination
- [ ] Add segmented speed selection for slow, normal, and fast playback
- [ ] Support pause, resume, and stop actions during route playback
- [ ] Confirm marker icon strategy for bike visual, including bitmap support or fallback
