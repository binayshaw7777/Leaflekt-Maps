# Terms & Attribution

## Attribution

LeafleKT renders tiles from third-party providers. Each provider has its own attribution requirement. You must display the provider's attribution string on the map when using their tiles.

| Style | Attribution Required |
|-------|---------------------|
| OpenStreetMap | © OpenStreetMap contributors |
| CartoDB | © OpenStreetMap contributors, © CARTO |
| OpenFreeMap | © OpenFreeMap contributors, © OpenStreetMap contributors |
| Esri | Powered by Esri |
| OpenTopoMap | © OpenTopoMap (CC-BY-SA) |

LeafleKT's built-in `LeaflektMap` composable displays attribution by default. Do not suppress it.

## Tile Provider Disclaimer

LeafleKT does not operate tile servers. Tile availability, uptime, and rate limits are controlled by the respective providers. Provider terms of service apply to your usage. Check each provider's policy before deploying to production at scale.

## Commercial Usage

LeafleKT itself is Apache 2.0 — free for commercial use. However, some tile providers restrict commercial or high-volume usage. Verify the tile provider's license before shipping a commercial product.

## No Uptime Guarantee

LeafleKT provides no uptime guarantees for any third-party tile provider. Build fallback behavior if your use case requires high availability.
