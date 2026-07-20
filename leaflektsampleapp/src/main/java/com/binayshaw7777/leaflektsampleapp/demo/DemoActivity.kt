package com.binayshaw7777.leaflektsampleapp.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.binayshaw7777.leaflektsampleapp.ui.theme.LeafleKTTheme

/**
 * Standalone activity for automated screenshot/GIF capture.
 * Launch via ADB:
 *   adb shell am start -n com.binayshaw7777.leaflektsampleapp/.demo.DemoActivity \
 *     --es SCREEN "markers"
 *
 * SCREEN values: map_styles, markers, polylines, polygons, circles, selection, geojson, clustering
 * STYLE_INDEX: 0-7 (only for map_styles screen)
 */
class DemoActivity : ComponentActivity() {
    private var currentScreen = mutableStateOf("markers")
    private var currentStyleIndex = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateStateFromIntent(intent)
        setContent {
            LeafleKTTheme {
                val screen = currentScreen.value
                val styleIndex = currentStyleIndex.value
                when (screen) {
                    "map_styles" -> MapStylesDemoScreen(styleIndex)
                    "markers" -> MarkersDemoScreen()
                    "polylines" -> PolylinesDemoScreen()
                    "polygons" -> PolygonsDemoScreen()
                    "circles" -> CirclesDemoScreen()
                    "selection" -> SelectionDemoScreen()
                    "geojson" -> GeoJsonDemoScreen()
                    "clustering" -> ClusteringDemoScreen()
                    "ui_settings" -> UiSettingsDemoScreen()
                    "custom_icons" -> CustomIconsDemoScreen()
                    "camera" -> CameraDemoScreen()
                    else -> MarkersDemoScreen()
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        updateStateFromIntent(intent)
    }

    private fun updateStateFromIntent(intent: android.content.Intent) {
        currentScreen.value = intent.getStringExtra("SCREEN") ?: "markers"
        currentStyleIndex.value = intent.getIntExtra("STYLE_INDEX", 0)
    }
}
