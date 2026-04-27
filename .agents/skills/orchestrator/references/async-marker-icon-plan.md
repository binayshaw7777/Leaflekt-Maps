# Async Marker Icon — Implementation Plan

## Overview
Add Coil-inspired async image loading for marker icons with minimal boilerplate.

## API Design

```kotlin
// State holder (similar to AsyncImage's State)
class LeaflektAsyncMarkerIconState(
    model: Any?,                    // URL, Int(@DrawableRes), Bitmap, File, Uri, etc.
    imageLoader: ImageLoader = ImageLoaderFactory.Default,
    widthPx: Int? = null,           // Auto-detect if null
    heightPx: Int? = null,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 1f
)

// Factory — preferred entry point
@Composable
fun rememberLeaflektAsyncMarkerIcon(
    model: Any?,
    imageLoader: ImageLoader = LocalLeaflektImageLoader.current,
    widthPx: Int? = null,
    heightPx: Int? = null,
    anchorFractionX: Float = 0.5f,
    anchorFractionY: Float = 1f
): LeaflektAsyncMarkerIconState

// Direct icon access
val LeaflektAsyncMarkerIconState.icon: LeaflektMarkerIcon?

// Extension for direct usage in LeaflektMarker
@Composable
fun modelToIcon(
    model: Any?,
    ... // same params
): LeaflektMarkerIcon? = rememberLeaflektAsyncMarkerIcon(model, ...).icon
```

## Usage Example

```kotlin
// URL
val bikeIcon = rememberLeaflektAsyncMarkerIcon(
    model = "https://example.com/bike.png"
)

// Resource ID
val defaultIcon = rememberLeaflektAsyncMarkerIcon(
    model = R.drawable.ic_marker
)

// In marker
LeaflektMarker(
    position = LeaflektLatLng(22.57, 88.36),
    icon = bikeIcon.icon  // null while loading, non-null when ready
)
```

## Implementation Tasks

1. **Add Coil dependency** to `leaflekt/build.gradle.kts`
   - Create a separate `leaflekt-image` optional module OR add as implementation with proguard exclusion
   - Prefer: add `coil-compose` as implementation (users already have it if using Compose)

2. **Create `LeaflektAsyncMarkerIconState.kt`**
   - Accepts model + imageLoader
   - Internally uses `AsyncImagePainter` or `ImageLoader.execute()` 
   - Converts loaded `Bitmap` → `LeaflektMarkerIcon` with dimensions & anchor
   - Exposes `val icon: LeaflektMarkerIcon?` as derived State
   - Handles errors → returns `null`
   - Auto-detects width/height from bitmap when null

3. **Create `LocalLeaflektImageLoader.kt`**
   - `CompositionLocal` providing default `ImageLoader` with sensible defaults
   - Allows override via `CompositionLocalProvider`

4. **Update documentation** in `LeaflektMarker.kt` and `LeaflektMarkerIcon.kt`
   - Show both Bitmap and async patterns
   - Keep KDocs with examples

5. **Add sample** in `leaflektsampleapp`
   - Demo async icon loading from URL and resources

## Technical Notes

- **Dependency**: Coil Compose (`io.coil-kt:coil-compose`) ~500 KB AAR, negligible for most apps
- **Threading**: Coil handles background decode, main-thread dispatch
- **Caching**: Reuses Coil's cache; identical models across markers share bitmap
- **State preservation**: `rememberSaveable` integration viapainter state restoration
- **Error handling**: Returns `null` icon; marker uses default if `icon = null`
- **No breaking changes**: Existing `LeaflektMarkerIcon(Bitmap)` continues unchanged

## Files to Create/Modify

- `leaflekt/src/main/java/com/binayshaw7777/leaflekt/library/LeaflektAsyncMarkerIconState.kt` (NEW)
- `leaflekt/src/main/java/com/binayshaw7777/leaflekt/library/LocalLeaflektImageLoader.kt` (NEW)
- `leaflekt/build.gradle.kts` (+ coil-compose dependency)
- `README.md` (add async icon section)
- `leaflektsampleapp/src/.../SomeScreen.kt` (demo usage)

## Benefits

- **One-liner loading**: `val icon = rememberLeaflektAsyncMarkerIcon(url)`
- **Zero boilerplate**: No `ImageRequest`, `Target`, or callbacks
- **Coil-compatible**: Any model Coil supports works here
- **Seamless**: Just pass `.icon` to marker's `icon` parameter
- **Composable-friendly**: Follows Compose idioms

## Trade-offs

- Adds Coil dependency (optional — can be shaded or moved to separate artifact)
- Increases library size slightly (~500 KB)
- Introduces async state (null → loading → success/error)
