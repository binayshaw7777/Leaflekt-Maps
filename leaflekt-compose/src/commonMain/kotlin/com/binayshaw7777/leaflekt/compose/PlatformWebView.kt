package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?,
    isFirstRenderDone: Boolean
)

/**
 * Pre-warms the map WebView so the first [LeaflektMap] composable appears near-instantly.
 * Call this once at app startup, before the user navigates to any screen containing a map.
 * No-op on Android (WebView pool handles warm-up automatically).
 */
expect fun prewarmLeaflektWebView()
