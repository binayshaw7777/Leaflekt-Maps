# Contributing

## Map Runtime Resources

Android core owns the canonical runtime logic in
`leaflekt-core/src/androidMain/assets/map.html`.

Keep these platform variants synchronized when JavaScript or markup changes:

- `leaflekt-core/src/iosMain/resources/map.html`
- `LeaflektMap/Sources/LeaflektMap/Resources/map.html`

The iOS copies use relative `leaflet/...` resource URLs. Android uses
`/assets/leaflet/...` URLs for `WebViewAssetLoader`; preserve that intentional difference.
