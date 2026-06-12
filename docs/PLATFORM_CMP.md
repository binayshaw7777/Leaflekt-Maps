# CMP / Compose Multiplatform

LeafleKT provides a CMP module `leaflekt-compose` for Android Compose and Compose Multiplatform (Android + iOS via CMP).

## Compose Multiplatform or Android Compose

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-compose:1.0.5")
}
```

### KMP without shared UI (core only)

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-core:1.0.5")
}
```

## Requirements

| Platform | Minimum |
|----------|---------|
| Android | API 21+ |
| Kotlin | 2.x |
| Compose | 1.7+ |

## CMP Sample App (`app-cmp/`)

The CMP sample app has three screens matching Android native and iOS:

| Screen | Features |
|--------|----------|
| **Explore** | Ola Maps autocomplete search, pin selected place, map style / location FABs |
| **Directions** | Origin/destination cards, place picker sheet, route polyline, swap/get route |
| **Clustering** | 100 markers clustered via `LeaflektMarkerCluster` |

Architecture:
- `CmpSampleApp.kt` — All screen composables + search bar + place picker + map style sheet
- `CmpOlaMapsViewModel.kt` — Reactive ViewModel with debounced search, directions state, route fetching
- `CmpOlaMapsRepository.kt` — Ola Maps API via Ktor
- `CmpOlaMapsModels.kt` — Data models

All files in `app-cmp/src/androidMain/kotlin/com/binayshaw7777/leaflekt/cmp/`.
