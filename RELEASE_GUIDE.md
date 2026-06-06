# LeafleKT — Release Guide

Quick reference for releasing all three SDK channels. Full rationale lives in `PLAN_PUBLISH.md`.

---

## Three channels per release

| Module | Audience | Channel | Coordinates |
|--------|----------|---------|-------------|
| `:leaflekt` | Android-only Compose | JitPack (auto) | `com.github.binayshaw7777.LeafleKT:leaflekt:VERSION` |
| `:leaflekt-compose` | Compose Multiplatform (Android + iOS) | Maven Central (auto) | `io.github.binayshaw7777:leaflekt-compose:VERSION` |
| `LeaflektMap` | Native iOS / SwiftUI | SPM via GitHub tag (auto) | `https://github.com/binayshaw7777/LeafleKT` tag `vVERSION` |

All three publish automatically when a push lands on `master` with a new `VERSION` value.

---

## One-time manual setup (do once, never again)

### 1. Sonatype Central account

1. Register at https://central.sonatype.com (GitHub login works)
2. Add namespace `io.github.binayshaw7777` — auto-verified via GitHub
3. Generate a user token → note **username** and **password**

### 2. GPG signing key

```bash
# Generate — choose RSA 4096, any email, set a passphrase
gpg --full-gen-key

# Get KEY_ID (last 8 hex chars of fingerprint)
gpg --list-keys

# Upload public key to keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys KEY_ID

# Export private key as base64 for CI
gpg --export-secret-keys --armor KEY_ID | base64
```

### 3. GitHub repository secrets

Go to repo Settings → Secrets and variables → Actions → New repository secret:

| Secret name | Value |
|-------------|-------|
| `SONATYPE_USERNAME` | Sonatype token username |
| `SONATYPE_PASSWORD` | Sonatype token password |
| `SIGNING_KEY_ID` | Last 8 chars of GPG fingerprint |
| `SIGNING_PASSWORD` | GPG key passphrase |
| `SIGNING_SECRET_KEY` | base64 output from export step above |

### 4. Swift Package Index (after first release)

Submit at https://swiftpackageindex.com/add-a-package with the repo URL.
SPI auto-discovers `LeaflektMap/Package.swift`.

---

## Local verification before release

Run these in order. Each gates the next.

```bash
# 1. Gradle config + vanniktech plugin resolves
./gradlew :leaflekt-compose:tasks --group publishing

# 2. Android target compiles
./gradlew :leaflekt-compose:assembleRelease

# 3. iOS KMP targets compile (needs Xcode)
./gradlew :leaflekt-compose:compileKotlinIosArm64 \
          :leaflekt-compose:compileKotlinIosSimulatorArm64 \
          :leaflekt-compose:compileKotlinIosX64

# 4. Local publish dry run — no GPG/Sonatype needed
#    Check output at ~/.m2/repository/io/github/binayshaw7777/leaflekt-compose/
./gradlew :leaflekt-compose:publishToMavenLocal -PsigningSkip=true

# 5. Swift Package builds (must specify iOS target — package doesn't support macOS)
cd LeaflektMap && swift package resolve && cd ..
xcodebuild build \
  -scheme LeaflektMap \
  -destination 'generic/platform=iOS Simulator' \
  CODE_SIGNING_REQUIRED=NO CODE_SIGNING_ALLOWED=NO \
  -quiet 2>&1 | tail -10

# 6. Common tests
./gradlew :leaflekt-compose:iosSimulatorArm64Test
```

---

## Release steps (every release)

1. Update `VERSION` file — single line, e.g. `0.6.0`
2. Run local verification above (steps 1–6)
3. Commit and merge to `master`
4. CI runs two jobs:
   - `release` (ubuntu): builds Android, publishes docs, creates GitHub release, warms JitPack for `:leaflekt`
   - `release-ios` (macos): compiles iOS KMP targets, runs tests, validates Swift package, **publishes `:leaflekt-compose` to Maven Central**, creates `vVERSION` SPM tag
5. Verify:
   - JitPack: `https://jitpack.io/#binayshaw7777/LeafleKT/VERSION`
   - Maven Central: search `io.github.binayshaw7777:leaflekt-compose` (may take ~10 min to index)
   - SPM: tag `vVERSION` visible in GitHub releases

---

## Gradle publishing commands (manual if needed)

```bash
# Publish :leaflekt-compose to Maven Central manually (needs env vars set)
./gradlew :leaflekt-compose:publishAndReleaseToMavenCentral --no-configuration-cache

# Publish to local Maven repo (no credentials needed)
./gradlew :leaflekt-compose:publishToMavenLocal -PsigningSkip=true

# Android-only build check
./gradlew :leaflekt:assembleRelease :leaflekt:testDebugUnitTest
```

---

## Architecture notes

- `:leaflekt` stays on JitPack — existing Android users unaffected, JitPack Linux runners handle Android-only fine
- `:leaflekt-compose` must publish from macOS CI runner — Linux cannot compile `iosX64`/`iosArm64`/`iosSimulatorArm64` KMP targets
- SPM tag format is `vVERSION` (e.g. `v0.6.0`); Gradle tag format is `VERSION` (e.g. `0.6.0`) — both created by CI
- vanniktech plugin v0.30+ required for `SonatypeHost.CENTRAL_PORTAL`; `--no-configuration-cache` required (plugin not config-cache compatible)
- Signing uses in-memory strategy via `ORG_GRADLE_PROJECT_signingInMemoryKey*` env vars — no `.gpg` file on disk

---

## Files changed for publishing setup

| File | What changed |
|------|-------------|
| `gradle/libs.versions.toml` | Added `vanniktech-maven-publish = "0.30.0"` + plugin alias |
| `build.gradle.kts` (root) | Added vanniktech plugin `apply false` |
| `leaflekt-compose/build.gradle.kts` | Replaced `maven-publish` with `mavenPublishing { publishToMavenCentral(CENTRAL_PORTAL) }`; group → `io.github.binayshaw7777` |
| `.github/workflows/release-master.yml` | Removed broken JitPack warm for leaflekt-compose; added Maven Central publish step in `release-ios`; fixed README sed patterns |
| `README.md` | Added CMP (Maven Central) + SPM install sections |
