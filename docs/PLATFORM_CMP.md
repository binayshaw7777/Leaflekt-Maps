# CMP / Compose Multiplatform & Android Guide

LeafleKT provides official Compose modules for Android Compose and Compose Multiplatform (CMP) targeting Android and iOS.

---

## 📦 Installation

### Gradle Setup (`build.gradle.kts`)

For Compose Multiplatform (Android & iOS) or Android Jetpack Compose:

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-compose:1.1.0")
}
```

If you only need headless state management and core LeafleKT data structures without Compose dependencies (e.g. background data syncing or non-Compose UI layers):

```kotlin
dependencies {
    implementation("io.github.binayshaw7777:leaflekt-core:1.1.0")
}
```

---

## 📋 Requirements

| Parameter | Specification |
| :--- | :--- |
| **Android Min SDK** | API 21+ (Android 5.0 Lollipop) |
| **iOS Min Version** | iOS 15.0+ (for CMP iOS target) |
| **Kotlin Version** | 2.x+ |
| **Compose Multiplatform** | 1.7+ |

---

## 🛠️ Android Configuration

### 1. Internet Permission (`AndroidManifest.xml`)

LeafleKT fetches tile images and map assets over HTTPS. Ensure internet access is enabled in your app manifest:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> <!-- Optional: For showCurrentLocation -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> <!-- Optional: For showCurrentLocation -->
```

### 2. ProGuard / R8 Rules

If your app uses code shrinking or obfuscation in production builds, add the following rules to `proguard-rules.pro`:

```proguard
# Keep LeafleKT JS bridge & interface classes
-keep class com.binayshaw7777.leaflekt.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
```

---

## 📱 CMP Sample Application (`app-cmp/`)

The CMP sample app demonstrates cross-platform map integration with state management and search routing capabilities.

### Sample App Features

| Screen | Description & Features |
| :--- | :--- |
| **Explore** | Interactive map search using Ola Maps autocomplete API, place pinning, style toggling FAB, and user location centering. |
| **Directions** | Route planning with origin/destination input sheets, polyline route rendering, distance/duration metrics, and place swapping. |
| **Clustering** | High-performance rendering of 100+ points using `LeaflektMarkerCluster` with dynamic zoom level grouping. |

### Module Architecture

- `CmpSampleApp.kt` — Master tab host UI, sheets, search bar, and FABs.
- `CmpOlaMapsViewModel.kt` — Reactive state container handling debounced search, geocoding, and directions state.
- `CmpOlaMapsRepository.kt` — Ktor-powered API service for Ola Maps search & routing endpoints.
- `CmpOlaMapsModels.kt` — Immutable data transfer objects for search results and polyline decoding.

