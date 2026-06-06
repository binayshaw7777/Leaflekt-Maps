# Plan: LeafleKT SDK Restructure — leaflekt-core extraction

## Execution status

- [x] Step 1: Create `leaflekt-core` module
- [x] Step 2: Add `LeaflektColor` and Compose bridge
- [x] Steps 3-5: Move core common/platform sources and `map.html`
- [x] Step 6: Refactor `leaflekt-compose` to depend on core
- [x] Steps 7-8: Update settings and remove legacy `leaflekt`
- [x] Step 9: Migrate demo apps
- [x] Step 10: Document canonical `map.html` and migration
- [x] Step 11.1: Verify `:leaflekt-core:build`
- [x] Step 11.2: Verify `:leaflekt-compose:build`
- [x] Step 11.3: Verify `:app:build`
- [x] Step 11.4: Verify `:leaflektsampleapp:build`
- [x] Step 11.5: Verify `:app-cmp:build`
- [x] Step 11.6: Verify native Swift package

Implementation note: the complete Android/iOS `leaflet/` resource trees moved to
`leaflekt-core` with `map.html`. Keeping them in Compose would make direct core consumers
load missing CSS, JavaScript, and image assets.

Verification: all five Gradle module builds passed together, the core AAR contains
`map.html` plus Leaflet CSS/JS assets, and the `app-ios-native` Xcode simulator build passed.

## Goal

Restructure the SDK from 3 overlapping modules into a clean 3-target architecture:

| Target | Module | Consumers |
|--------|--------|-----------|
| KMP (no shared UI) | `leaflekt-core` | KMP apps using own platform UI |
| CMP / Android Compose / iOS via Compose | `leaflekt-compose` | CMP and single-platform Compose apps |
| iOS native Swift | `LeaflektMap` (Swift SPM) | Swift/SwiftUI apps — **unchanged** |

**Delete** `leaflekt` (Android View module) — replaced by `leaflekt-compose` for Android Compose users.

---

## Current module inventory

```
leaflekt/            Android-only, View/XML + Compose, published to Maven
leaflekt-compose/    KMP (Android + iOS), Compose-only, published to Maven
LeaflektMap/         Swift Package, iOS native, published via SPM
app/                 Android demo (uses :leaflekt)
app-cmp/             CMP demo (uses :leaflekt-compose)
app-ios-native/      iOS native demo (uses LeaflektMap SPM)
leaflektsampleapp/   Android sample app (uses :leaflekt)
```

---

## Target module layout

```
leaflekt-core/       NEW — KMP, zero Compose dependency
  src/commonMain/    models, controller interface+base, script builder, bridge
  src/androidMain/   Android WebView controller, JS bridge (no Compose)
  src/iosMain/       WKWebView controller, WeakScriptMessageHandler (no Compose)

leaflekt-compose/    REFACTORED — CMP, depends on leaflekt-core
  src/commonMain/    Composables, Compose state, CompositionLocal
  src/androidMain/   PlatformWebView (AndroidView), BitmapCompat, location
  src/iosMain/       PlatformWebView (UIKitView), location

LeaflektMap/         UNCHANGED — Swift SPM

app/                 Android demo — update dep :leaflekt → :leaflekt-compose
leaflektsampleapp/   Android sample — update dep :leaflekt → :leaflekt-compose
app-cmp/             CMP demo — stays on :leaflekt-compose (no change needed)
app-ios-native/      iOS native demo — unchanged
```

---

## Step 1 — Create `leaflekt-core` module

### 1a. Create directory structure

```
leaflekt-core/
  build.gradle.kts
  src/
    commonMain/kotlin/com/binayshaw7777/leaflekt/
    androidMain/kotlin/com/binayshaw7777/leaflekt/
    iosMain/kotlin/com/binayshaw7777/leaflekt/
```

### 1b. `leaflekt-core/build.gradle.kts`

```kotlin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
}

val releaseVersion = rootProject.file("VERSION").readText().trim()

group = "io.github.binayshaw7777"
version = releaseVersion

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.webkit)
            implementation(libs.google.play.services.location)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "com.binayshaw7777.leaflekt"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets")
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("CI") != null) {
        signAllPublications()
    }

    coordinates("io.github.binayshaw7777", "leaflekt-core", releaseVersion)

    pom {
        name.set("LeafleKT Core")
        description.set("KMP core for LeafleKT — Leaflet.js WebView controller, models, script builder.")
        url.set("https://github.com/binayshaw7777/LeafleKT")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }

        developers {
            developer {
                id.set("binayshaw7777")
                name.set("Binay Shaw")
                url.set("https://github.com/binayshaw7777")
            }
        }

        scm {
            url.set("https://github.com/binayshaw7777/LeafleKT")
            connection.set("scm:git:https://github.com/binayshaw7777/LeafleKT.git")
            developerConnection.set("scm:git:ssh://git@github.com/binayshaw7777/LeafleKT.git")
        }
    }
}
```

### 1c. `leaflekt-core/src/androidMain/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest />
```

---

## Step 2 — Add `LeaflektColor` to core (replaces `androidx.compose.ui.graphics.Color`)

Shape info models and `LeaflektScriptBuilder` currently use `androidx.compose.ui.graphics.Color`.
To remove the Compose dependency from core, introduce `LeaflektColor` in core and bridge it in `leaflekt-compose`.

### 2a. Create `leaflekt-core/src/commonMain/kotlin/com/binayshaw7777/leaflekt/LeaflektColor.kt`

```kotlin
package com.binayshaw7777.leaflekt

data class LeaflektColor(
    val red: Float,
    val green: Float,
    val blue: Float,
    val alpha: Float = 1f
) {
    companion object {
        val Black = LeaflektColor(0f, 0f, 0f)
        val White = LeaflektColor(1f, 1f, 1f)
        val Transparent = LeaflektColor(0f, 0f, 0f, 0f)
        val Red = LeaflektColor(1f, 0f, 0f)
        val Blue = LeaflektColor(0f, 0f, 1f)
        val Green = LeaflektColor(0f, 0.502f, 0f)

        fun fromArgb(argb: Long): LeaflektColor {
            val a = ((argb shr 24) and 0xFF) / 255f
            val r = ((argb shr 16) and 0xFF) / 255f
            val g = ((argb shr 8) and 0xFF) / 255f
            val b = (argb and 0xFF) / 255f
            return LeaflektColor(r, g, b, a)
        }

        fun fromRgb(r: Int, g: Int, b: Int, a: Int = 255): LeaflektColor =
            LeaflektColor(r / 255f, g / 255f, b / 255f, a / 255f)
    }

    internal fun toCssRgba(overrideAlpha: Float = this.alpha): String {
        val r = (red * 255).toInt().coerceIn(0, 255)
        val g = (green * 255).toInt().coerceIn(0, 255)
        val b = (blue * 255).toInt().coerceIn(0, 255)
        return "rgba($r,$g,$b,$overrideAlpha)"
    }
}
```

### 2b. Add to `leaflekt-compose` a bridge extension (new file or in existing `ColorExtensions`)

Create `leaflekt-compose/src/commonMain/kotlin/com/binayshaw7777/leaflekt/compose/LeaflektColorBridge.kt`:

```kotlin
package com.binayshaw7777.leaflekt.compose

import androidx.compose.ui.graphics.Color
import com.binayshaw7777.leaflekt.LeaflektColor

fun Color.toLeaflektColor(): LeaflektColor =
    LeaflektColor(red, green, blue, alpha)

fun LeaflektColor.toComposeColor(): Color =
    Color(red, green, blue, alpha)
```

This file has **no** expect/actual — pure commonMain conversion. After this, remove the old `ColorExtensions.kt` expect and its android/ios actuals from `leaflekt-compose` (they were `internal expect fun Color.toCssRgba()`). That function moves into core's `LeaflektColor.toCssRgba()` above.

---

## Step 3 — Files to create in `leaflekt-core/src/commonMain/`

Move (and re-package to `com.binayshaw7777.leaflekt`) these files from `leaflekt-compose/src/commonMain/`:

| File | Notes |
|------|-------|
| `LeaflektLatLng.kt` | No changes |
| `LeaflektCameraPosition.kt` | No changes |
| `LeaflektGeoJsonOverlay.kt` | No changes |
| `LeaflektMapStyle.kt` | No changes |
| `LeaflektStrokePattern.kt` | No changes |
| `LeaflektCurrentLocationIcon.kt` | No changes |
| `LeaflektMapProperties.kt` | No changes |
| `LeaflektMapUiSettings.kt` | No changes |
| `LeaflektBridgeCallbacks.kt` | Change from `internal interface` to `internal interface` — keep internal |
| `LeaflektWebConstants.kt` | Keep internal |
| `LeaflektMapJson.kt` | Keep internal |
| `LeaflektMarkerInfo.kt` | No Color usage — no changes |
| `LeaflektPolylineInfo.kt` | `color: Color` → `color: LeaflektColor = LeaflektColor.Black` |
| `LeaflektPolygonInfo.kt` | `fillColor: Color` → `fillColor: LeaflektColor = LeaflektColor.Transparent`, `strokeColor: Color` → `strokeColor: LeaflektColor = LeaflektColor.Black` |
| `LeaflektCircleInfo.kt` | same Color → LeaflektColor replacements as Polygon |
| `LeaflektScriptBuilder.kt` | Replace `import androidx.compose.ui.graphics.Color` with none; use `LeaflektColor.toCssRgba()` directly |
| `LeaflektControllerInterface.kt` | No changes (references moved models) |
| `LeaflektControllerBase.kt` | No changes (references moved models) |
| `GenerateId.kt` | `expect fun generateId()` — keep internal |

All files: change `package com.binayshaw7777.leaflekt.compose` → `package com.binayshaw7777.leaflekt`.

---

## Step 4 — Files to create in `leaflekt-core/src/androidMain/`

Move from `leaflekt-compose/src/androidMain/`:

| File | Notes |
|------|-------|
| `LeaflektController.android.kt` | Change package to `com.binayshaw7777.leaflekt`. Imports `android.webkit.WebView`. No Compose. |
| `LeaflektJsBridge.android.kt` | Change package. `@JavascriptInterface` bridge — no Compose. |
| `GenerateId.android.kt` | Change package. |

Also copy `leaflekt-compose/src/androidMain/assets/map.html` → `leaflekt-core/src/androidMain/assets/map.html`.

---

## Step 5 — Files to create in `leaflekt-core/src/iosMain/`

Move from `leaflekt-compose/src/iosMain/`:

| File | Notes |
|------|-------|
| `LeaflektController.ios.kt` | Change package to `com.binayshaw7777.leaflekt`. |
| `WeakScriptMessageHandler.kt` | Change package. |
| `GenerateId.ios.kt` | Change package. |

Also copy `leaflekt-compose/src/iosMain/resources/map.html` → `leaflekt-core/src/iosMain/resources/map.html`.

---

## Step 6 — Refactor `leaflekt-compose` after extraction

### 6a. Update `leaflekt-compose/build.gradle.kts`

Add to `commonMain.dependencies`:
```kotlin
api(project(":leaflekt-core"))
```

Use `api` (not `implementation`) so consumers of `leaflekt-compose` also get `leaflekt-core` types without needing to add it separately.

### 6b. Delete from `leaflekt-compose/src/commonMain/`

Remove every file listed in Step 3 (they now live in core). Keep:
- `LeaflektMap.kt`
- `LeaflektMapComposable.kt`
- `LeaflektMarker.kt`
- `LeaflektMarkerCluster.kt`
- `LeaflektMarkerState.kt`
- `LeaflektPolyline.kt`
- `LeaflektPolygon.kt`
- `LeaflektCircle.kt`
- `LeaflektPolylineState.kt`
- `LeaflektPolygonState.kt`
- `LeaflektCircleState.kt`
- `LeaflektCameraPositionState.kt`
- `LeaflektCurrentLocationOverlayExpect.kt`
- `PlatformWebView.kt`
- `LeaflektController.kt` (the `expect class LeaflektController` declaration)
- `LocalLeaflektController.kt` (if exists, or inline in LeaflektMap.kt)
- `LeaflektShapeSelectionDefaults.kt` (uses `Color`, stays in compose)
- NEW: `LeaflektColorBridge.kt` (from Step 2b)

### 6c. Delete from `leaflekt-compose/src/androidMain/`

Remove:
- `LeaflektController.android.kt` (moved to core)
- `LeaflektJsBridge.android.kt` (moved to core)
- `GenerateId.android.kt` (moved to core)
- `ColorExtensions.android.kt` (old `toCssRgba` — no longer needed)

Keep:
- `BitmapCompat.kt`
- `LeaflektCurrentLocationOverlay.android.kt`
- `PlatformLocationProvider.android.kt`
- `PlatformWebView.android.kt`

### 6d. Delete from `leaflekt-compose/src/iosMain/`

Remove:
- `LeaflektController.ios.kt` (moved to core)
- `WeakScriptMessageHandler.kt` (moved to core)
- `GenerateId.ios.kt` (moved to core)
- `ColorExtensions.ios.kt` (old `toCssRgba` — no longer needed)

Keep:
- `LeaflektCurrentLocationOverlay.ios.kt`
- `PlatformLocationProvider.ios.kt`
- `PlatformWebView.ios.kt`

### 6e. Update all imports in remaining `leaflekt-compose` files

Every remaining file that imports from `com.binayshaw7777.leaflekt.compose.Leaflekt*` models must update to `com.binayshaw7777.leaflekt.Leaflekt*` for types now living in core. Compose-specific types (state classes, composables) keep the `.compose` package.

Specifically, in composables like `LeaflektMarker.kt`, `LeaflektPolyline.kt`, etc., add:
```kotlin
import com.binayshaw7777.leaflekt.LeaflektLatLng
import com.binayshaw7777.leaflekt.LeaflektMarkerInfo
// etc.
```

In `PlatformWebView.android.kt` and `.ios.kt`, add:
```kotlin
import com.binayshaw7777.leaflekt.LeaflektBridgeCallbacks
import com.binayshaw7777.leaflekt.LeaflektWebConstants // JS_BRIDGE_ANDROID etc.
```

---

## Step 7 — Update `settings.gradle.kts`

Add `leaflekt-core`, remove `leaflekt`:

```kotlin
rootProject.name = "LeafleKT"
include(":leaflekt-core")    // NEW
include(":leaflekt-compose")
include(":app")
include(":app-cmp")
include(":leaflektsampleapp")
// ":leaflekt" removed
```

---

## Step 8 — Delete `leaflekt` module

Delete the entire `leaflekt/` directory. This module is fully superseded.

---

## Step 9 — Update demo apps

### `:app` (`app/src/main/java/…/MainActivity.kt` and `app/build.gradle.kts`)

Change dependency: `:leaflekt` → `:leaflekt-compose`

In `build.gradle.kts`:
```kotlin
// before
implementation(project(":leaflekt"))
// after
implementation(project(":leaflekt-compose"))
```

Update imports in `MainActivity.kt`:
- Old: `import com.binayshaw7777.leaflekt.library.*`
- New: `import com.binayshaw7777.leaflekt.compose.*` and `import com.binayshaw7777.leaflekt.*`

If `MainActivity.kt` uses `MapView` (Android View composable from old leaflekt), replace with `LeaflektMap` composable from `leaflekt-compose`.

### `:leaflektsampleapp`

Same as `:app` — swap dependency and update imports.

### `:app-cmp`

Already uses `:leaflekt-compose`. Update imports for any types that moved to core package. No dependency change needed (core is re-exported via `api`).

---

## Step 10 — `map.html` canonical source

**Problem:** `map.html` currently duplicated in 3 places:
- `leaflekt-compose/src/androidMain/assets/map.html`
- `leaflekt-compose/src/iosMain/resources/map.html`
- `LeaflektMap/Sources/LeaflektMap/Resources/map.html` (SPM)

**After restructure:**
- Move canonical `map.html` to `leaflekt-core/src/androidMain/assets/map.html`
- Copy to `leaflekt-core/src/iosMain/resources/map.html`
- Delete `leaflekt-compose/src/androidMain/assets/map.html`
- Delete `leaflekt-compose/src/iosMain/resources/map.html`
- `leaflekt-compose` gets `map.html` transitively from `leaflekt-core` via the KMP artifact
- SPM `LeaflektMap/Sources/LeaflektMap/Resources/map.html` stays separate — must be manually kept in sync when `map.html` changes (document this in CONTRIBUTING.md)

---

## Step 11 — Verify build targets

After all changes, verify:
1. `./gradlew :leaflekt-core:build` — compiles clean
2. `./gradlew :leaflekt-compose:build` — compiles clean
3. `./gradlew :app:build` — Android demo builds
4. `./gradlew :leaflektsampleapp:build` — sample app builds
5. `./gradlew :app-cmp:build` — CMP demo builds
6. Open `app-ios-native.xcodeproj` in Xcode → build succeeds (SPM unchanged)

---

## File move summary

### leaflekt-core/src/commonMain/ (all NEW, moved from leaflekt-compose commonMain)
```
LeaflektColor.kt                  NEW (created in Step 2a)
LeaflektLatLng.kt
LeaflektCameraPosition.kt
LeaflektGeoJsonOverlay.kt
LeaflektMapStyle.kt
LeaflektStrokePattern.kt
LeaflektCurrentLocationIcon.kt
LeaflektMapProperties.kt
LeaflektMapUiSettings.kt
LeaflektBridgeCallbacks.kt
LeaflektWebConstants.kt
LeaflektMapJson.kt
LeaflektMarkerInfo.kt
LeaflektPolylineInfo.kt           Color → LeaflektColor
LeaflektPolygonInfo.kt            Color → LeaflektColor
LeaflektCircleInfo.kt             Color → LeaflektColor
LeaflektScriptBuilder.kt          Color → LeaflektColor
LeaflektControllerInterface.kt
LeaflektControllerBase.kt
GenerateId.kt
```

### leaflekt-core/src/androidMain/ (all NEW, moved from leaflekt-compose androidMain)
```
LeaflektController.android.kt
LeaflektJsBridge.android.kt
GenerateId.android.kt
AndroidManifest.xml               NEW (empty)
assets/map.html                   moved from leaflekt-compose/src/androidMain/assets/
```

### leaflekt-core/src/iosMain/ (all NEW, moved from leaflekt-compose iosMain)
```
LeaflektController.ios.kt
WeakScriptMessageHandler.kt
GenerateId.ios.kt
resources/map.html                moved from leaflekt-compose/src/iosMain/resources/
```

### leaflekt-compose/src/commonMain/ (REMOVED files)
```
DELETE: LeaflektLatLng.kt
DELETE: LeaflektCameraPosition.kt
DELETE: LeaflektGeoJsonOverlay.kt
DELETE: LeaflektMapStyle.kt
DELETE: LeaflektStrokePattern.kt
DELETE: LeaflektCurrentLocationIcon.kt
DELETE: LeaflektMapProperties.kt
DELETE: LeaflektMapUiSettings.kt
DELETE: LeaflektBridgeCallbacks.kt
DELETE: LeaflektWebConstants.kt
DELETE: LeaflektMapJson.kt
DELETE: LeaflektMarkerInfo.kt
DELETE: LeaflektPolylineInfo.kt
DELETE: LeaflektPolygonInfo.kt
DELETE: LeaflektCircleInfo.kt
DELETE: LeaflektScriptBuilder.kt
DELETE: LeaflektControllerInterface.kt
DELETE: LeaflektControllerBase.kt
DELETE: GenerateId.kt
DELETE: ColorExtensions.kt        replaced by LeaflektColorBridge.kt
ADD:    LeaflektColorBridge.kt    NEW (Step 2b)
```

### leaflekt-compose/src/androidMain/ (REMOVED files)
```
DELETE: LeaflektController.android.kt
DELETE: LeaflektJsBridge.android.kt
DELETE: GenerateId.android.kt
DELETE: ColorExtensions.android.kt
DELETE: assets/map.html           now lives in leaflekt-core
```

### leaflekt-compose/src/iosMain/ (REMOVED files)
```
DELETE: LeaflektController.ios.kt
DELETE: WeakScriptMessageHandler.kt
DELETE: GenerateId.ios.kt
DELETE: ColorExtensions.ios.kt
DELETE: resources/map.html        now lives in leaflekt-core
```

---

## `LeaflektController` expect/actual split

`LeaflektController` is currently an `expect class` in `leaflekt-compose/src/commonMain/` with actuals in androidMain and iosMain. After the move, the `expect class` declaration moves to **core's commonMain** and actuals move to core's platform source sets. The `leaflekt-compose` module no longer needs its own controller expect/actual — it just imports `LeaflektController` from core.

Check: `leaflekt-compose/src/commonMain/LeaflektController.kt` — if it contains the `expect class` declaration, delete it and let core own it.

---

## Public API surface after restructure

### `leaflekt-core` — KMP users add `io.github.binayshaw7777:leaflekt-core`
```kotlin
// Models
LeaflektLatLng, LeaflektCameraPosition, LeaflektColor
LeaflektMarkerInfo, LeaflektMarkerIconInfo
LeaflektPolylineInfo, LeaflektPolygonInfo, LeaflektCircleInfo
LeaflektStrokePattern, LeaflektCurrentLocationIcon
// Map config
LeaflektMapStyle, LeaflektMapProperties, LeaflektMapUiSettings, LeaflektGeoJsonOverlay
// Controller
LeaflektController (expect/actual — holds WebView/WKWebView)
LeaflektControllerInterface, LeaflektControllerBase
```

### `leaflekt-compose` — CMP/Compose users add `io.github.binayshaw7777:leaflekt-compose`
All core types re-exported via `api(project(":leaflekt-core"))` plus:
```kotlin
// Composables
LeaflektMap, LeaflektMarker, LeaflektPolyline, LeaflektPolygon, LeaflektCircle
LeaflektMarkerCluster, LeaflektCurrentLocationOverlay
// Compose state
LeaflektMarkerState, LeaflektPolylineState, LeaflektPolygonState, LeaflektCircleState
LeaflektCameraPositionState
// Bridge util
Color.toLeaflektColor(), LeaflektColor.toComposeColor()
```

---

## Notes / gotchas

1. **BREAKING — Package rename**: All types moving to core change package from
   `com.binayshaw7777.leaflekt.compose` to `com.binayshaw7777.leaflekt`.
   Any existing consumer of `leaflekt-compose` who directly references model types
   (e.g. `LeaflektLatLng`, `LeaflektMarkerInfo`, shape info classes) must update imports.
   **Bump version to `1.0.0` when this ships. Add MIGRATION.md.**

2. **BREAKING — `LeaflektColor` in shape constructors**: Existing Compose users who wrote
   `color = Color.Red` in `LeaflektPolylineInfo(...)` must now write
   `color = Color.Red.toLeaflektColor()` or `color = LeaflektColor.Red`.
   Document in MIGRATION.md.

3. **BREAKING — JitPack fully dropped**: `leaflekt` (old module) was published via
   `maven-publish` plugin and consumed via JitPack (`https://jitpack.io`).
   After this restructure, JitPack is no longer used. Both `leaflekt-core` and
   `leaflekt-compose` publish exclusively to **Maven Central** via
   `vanniktech.maven.publish` + `SonatypeHost.CENTRAL_PORTAL`.
   - Remove `maven { url = uri("https://jitpack.io") }` from `settings.gradle.kts`
   - Old JitPack artifact (`com.github.binayshaw7777:LeafleKT`) becomes dead — document this.

3. **`LeaflektController` expect/actual**: Confirm the exact file location of the `expect class LeaflektController` declaration before deleting. Search for `expect class LeaflektController` across the repo.

4. **`leaflektsampleapp` uses `:leaflekt`**: If it uses old Android View APIs (non-Compose `MapView`), those APIs no longer exist. Migrate to `LeaflektMap` composable from `leaflekt-compose`.

5. **`app/` uses `:leaflekt`**: Same as above — check `MainActivity.kt` for View-based map usage and migrate to Compose.

6. **map.html sync (SPM)**: After any `map.html` change, manually copy to `LeaflektMap/Sources/LeaflektMap/Resources/map.html`. Add a comment in `map.html` reminding devs to sync SPM copy.

7. **`LeaflektShapeSelectionDefaults.kt`** stays in `leaflekt-compose` (uses `Color`). Update it to use `LeaflektColor` if you want it usable from core users too — but it is currently `internal`, so not public API.
