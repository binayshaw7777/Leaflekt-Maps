package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*
import com.binayshaw7777.leaflekt.compose.generated.resources.Res

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.CoreGraphics.CGRect
import platform.Foundation.NSURLCache
import platform.Foundation.NSURL
import platform.UIKit.UIColor
import platform.WebKit.WKProcessPool
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

internal object IosWebViewGlobals {
    val processPool = WKProcessPool()
    private var isCacheConfigured = false

    private class PrewarmedEntry(val webView: WKWebView, val handler: WeakScriptMessageHandler)
    private var prewarmedEntry: PrewarmedEntry? = null

    fun setupCacheOnce() {
        if (isCacheConfigured) return
        isCacheConfigured = true
        NSURLCache.setSharedURLCache(
            NSURLCache(
                memoryCapacity = 50UL * 1024UL * 1024UL,
                diskCapacity = 200UL * 1024UL * 1024UL,
                diskPath = "leaflekt_url_cache"
            )
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    fun prewarmOnce(htmlUrl: NSURL, baseUrl: NSURL) {
        if (prewarmedEntry != null) return
        val noopCallbacks = object : LeaflektBridgeCallbacks {
            override fun onMapReady() {}
            override fun onMapClick(lat: Double, lng: Double) {}
            override fun onCameraMoveStarted(lat: Double, lng: Double, zoom: Double) {}
            override fun onCameraMove(lat: Double, lng: Double, zoom: Double) {}
            override fun onCameraIdle(lat: Double, lng: Double, zoom: Double) {}
            override fun onMarkerClick(markerId: String) {}
            override fun onPolylineClick(polylineId: String) {}
            override fun onPolygonClick(polygonId: String) {}
            override fun onCircleClick(circleId: String) {}
        }
        val handler = WeakScriptMessageHandler(noopCallbacks)
        val ucc = WKUserContentController().also {
            it.addScriptMessageHandler(handler, name = JS_BRIDGE_IOS)
            it.addUserScript(
                WKUserScript(
                    source = "window.bridgeName = '$JS_BRIDGE_IOS';",
                    injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
                    forMainFrameOnly = true
                )
            )
        }
        val config = WKWebViewConfiguration().apply {
            this.processPool = IosWebViewGlobals.processPool
            this.websiteDataStore = WKWebsiteDataStore.defaultDataStore()
            this.userContentController = ucc
            this.suppressesIncrementalRendering = false
            this.allowsInlineMediaPlayback = true
        }
        val webView = WKWebView(frame = cValue<CGRect>(), configuration = config).also { wv ->
            wv.opaque = false
            wv.backgroundColor = UIColor.clearColor()
            wv.allowsLinkPreview = false
            wv.allowsBackForwardNavigationGestures = false
            wv.scrollView.scrollEnabled = false
            wv.loadFileURL(htmlUrl, allowingReadAccessToURL = baseUrl)
        }
        prewarmedEntry = PrewarmedEntry(webView, handler)
    }

    fun claimPrewarmed(realBridge: LeaflektBridgeCallbacks): WKWebView? {
        val entry = prewarmedEntry ?: return null
        prewarmedEntry = null
        // Swap the delegate — no handler re-registration needed since same object is retained by WKUserContentController
        entry.handler.callbacks = realBridge
        return entry.webView
    }
}

@OptIn(ExperimentalResourceApi::class)
actual fun prewarmLeaflektWebView() {
    IosWebViewGlobals.setupCacheOnce()
    val uri = runCatching { Res.getUri("files/map.html") }.getOrNull() ?: return
    val htmlUrl = NSURL.URLWithString(uri) ?: return
    val baseUrl = htmlUrl.URLByDeletingLastPathComponent ?: htmlUrl
    IosWebViewGlobals.prewarmOnce(htmlUrl, baseUrl)
}

@Composable
internal actual fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?,
    isFirstRenderDone: Boolean
) {
    val messageHandler = remember { WeakScriptMessageHandler(bridge) }
    val navigationDelegate = remember { NavigationDelegate(bridge) }

    UIKitView(
        modifier = modifier,
        factory = {
            IosWebViewGlobals.setupCacheOnce()

            // Try to claim a pre-warmed view first — if available, JS engine and map are already running
            val prewarmed = IosWebViewGlobals.claimPrewarmed(bridge)
            if (prewarmed != null) {
                messageHandler.callbacks = bridge
                prewarmed.navigationDelegate = navigationDelegate
                controller.setWebView(prewarmed)
                controller.notifyMapReady()
                bridge.onMapFirstRender()
                return@UIKitView prewarmed
            }

            // Cold path: create fresh WKWebView
            val userContentController = WKUserContentController()
            userContentController.addScriptMessageHandler(messageHandler, name = JS_BRIDGE_IOS)

            val bridgeNameScript = WKUserScript(
                source = "window.bridgeName = '$JS_BRIDGE_IOS';",
                injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
                forMainFrameOnly = true
            )
            userContentController.addUserScript(bridgeNameScript)

            val config = WKWebViewConfiguration().apply {
                this.processPool = IosWebViewGlobals.processPool
                this.websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                this.userContentController = userContentController
                this.suppressesIncrementalRendering = false
                this.allowsInlineMediaPlayback = true
            }

            @OptIn(ExperimentalResourceApi::class)
            val htmlUrl = runCatching {
                val uri = Res.getUri("files/map.html")
                NSURL.URLWithString(uri)
            }.getOrNull() ?: platform.Foundation.NSBundle.mainBundle.URLForResource("map", withExtension = "html")
            ?: error("map.html not found in resources")

            val baseUrl = htmlUrl.URLByDeletingLastPathComponent ?: htmlUrl

            @OptIn(ExperimentalForeignApi::class)
            WKWebView(frame = cValue<CGRect>(), configuration = config).also { webView ->
                webView.opaque = false
                webView.backgroundColor = UIColor.clearColor()
                webView.navigationDelegate = navigationDelegate
                webView.allowsLinkPreview = false
                webView.allowsBackForwardNavigationGestures = false
                webView.scrollView.scrollEnabled = false
                controller.setWebView(webView)
                webView.loadFileURL(htmlUrl, allowingReadAccessToURL = baseUrl)
            }
        },
        update = { webView ->
            controller.setWebView(webView)
        },
        onRelease = { webView ->
            controller.setWebView(null)
            webView.navigationDelegate = null
            runCatching {
                webView.configuration.userContentController.removeScriptMessageHandlerForName(JS_BRIDGE_IOS)
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            controller.setWebView(null)
        }
    }
}
