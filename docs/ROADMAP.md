# LeafleKT Project Roadmap 🛣️

This roadmap outlines planned features, platform improvements, and performance enhancements for LeafleKT.

---

## 🎯 Near-Term (v1.2)

- 🌐 **Custom XYZ & WMTS Tile Providers:** First-class support for plugging in custom tile URL templates (`https://tile.example.com/{z}/{x}/{y}.png`) with custom headers & subdomains.
- 💾 **Offline Tile Caching (L1/L2):** Disk and memory caching layer for loaded map tiles to improve load speeds and offline operation.
- 🔄 **Automatic Provider Fallback Chains:** Automatic fallback tile source when primary tile provider endpoints experience timeouts or failures.
- 📱 **Showcase Apps on Stores:** Publish standalone demonstration apps on Google Play Store and Apple App Store.

---

## 🚀 Medium-Term (v1.3 - v2.0)

- 📐 **Advanced GeoJSON Styling:** Feature-level styling properties, interactive click events per feature, and simplified GeoJSON filtering.
- 🚗 **Vehicle & Location Trackers:** Smooth marker animation helper utilities (interpolating lat/lng along polyline paths with visual bearing rotation).
- 📍 **Custom Overlay Callouts:** Native Compose / SwiftUI interactive popup views anchored to map coordinates instead of web popups.

---

## 🔬 Future Research

- ⚡ **Native Vector Engine Evaluation:** Exploring MapLibre Native integration bindings for zero-WebView fully native offline rendering capabilities.

---

## 🤝 Contributing

We welcome community feedback and contributions! Please open an issue on GitHub to discuss proposed feature requests before submitting a pull request.

