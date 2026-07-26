package com.binayshaw7777.leaflekt.compose

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.binayshaw7777.leaflekt.*

val LeaflektMapPropertiesSaver: Saver<LeaflektMapProperties, *> = listSaver(
    save = {
        val geoJsonEncoded = when (val g = it.geoJsonOverlay) {
            is LeaflektGeoJsonOverlay.India -> "type:india"
            is LeaflektGeoJsonOverlay.None -> "type:none"
            is LeaflektGeoJsonOverlay.Custom -> "type:custom:${g.geojson}"
        }
        val cluster = it.clusterConfig
        listOf(it.mapStyle.name, geoJsonEncoded, it.tileBufferSize, cluster?.groupId, cluster?.maxClusterRadius)
    },
    restore = {
        val styleName = it[0] as String
        val geoJsonStr = it[1] as String
        val tileBuffer = it[2] as Int
        val clusterGroupId = it[3] as? String
        val clusterRadius = it[4] as? Int
        val style = LeaflektMapStyle.entries.firstOrNull { e -> e.name == styleName }
            ?: LeaflektMapStyle.OpenStreetMap
        val geoJson = when {
            geoJsonStr == "type:india" -> LeaflektGeoJsonOverlay.India
            geoJsonStr == "type:none" -> LeaflektGeoJsonOverlay.None
            geoJsonStr.startsWith("type:custom:") -> LeaflektGeoJsonOverlay.Custom(geoJsonStr.removePrefix("type:custom:"))
            else -> LeaflektGeoJsonOverlay.India
        }
        val cluster = if (clusterGroupId != null && clusterRadius != null)
            LeaflektClusterConfig(clusterGroupId, clusterRadius) else null
        LeaflektMapProperties(mapStyle = style, geoJsonOverlay = geoJson, tileBufferSize = tileBuffer, clusterConfig = cluster)
    }
)

val LeaflektMapUiSettingsSaver: Saver<LeaflektMapUiSettings, *> = listSaver(
    save = {
        val icon = it.currentLocationIcon
        listOf(
            it.zoomControlsEnabled,
            it.scrollGesturesEnabled,
            it.zoomGesturesEnabled,
            it.showCurrentLocation,
            it.myLocationButtonEnabled,
            icon?.widthPx,
            icon?.heightPx,
            icon?.anchorFractionX,
            icon?.anchorFractionY,
            icon?.pngBytes
        )
    },
    restore = {
        val widthPx = it[5] as? Int
        val heightPx = it[6] as? Int
        val anchorX = it[7] as? Float
        val anchorY = it[8] as? Float
        val bytes = it[9] as? ByteArray
        val icon = if (widthPx != null && heightPx != null && anchorX != null && anchorY != null && bytes != null) {
            LeaflektCurrentLocationIcon(bytes, widthPx, heightPx, anchorX, anchorY)
        } else null
        LeaflektMapUiSettings(
            zoomControlsEnabled = it[0] as Boolean,
            scrollGesturesEnabled = it[1] as Boolean,
            zoomGesturesEnabled = it[2] as Boolean,
            showCurrentLocation = it[3] as Boolean,
            myLocationButtonEnabled = it[4] as Boolean,
            currentLocationIcon = icon
        )
    }
)
