package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*
import com.binayshaw7777.leaflekt.compose.generated.resources.Res

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.binayshaw7777.leaflekt.NavigationDelegate
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.CoreGraphics.CGRect
import platform.Foundation.NSBundle
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

private object IosWebViewGlobals {
    val processPool = WKProcessPool()
    private var isCacheConfigured = false

    fun setupCacheOnce() {
        if (!isCacheConfigured) {
            isCacheConfigured = true
            NSURLCache.setSharedURLCache(
                NSURLCache(
                    memoryCapacity = 50UL * 1024UL * 1024UL,
                    diskCapacity = 200UL * 1024UL * 1024UL,
                    diskPath = "leaflekt_url_cache"
                )
            )
        }
    }
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
    val alpha by animateFloatAsState(
        targetValue = if (isFirstRenderDone) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "mapAlpha"
    )

    UIKitView(
        modifier = modifier.alpha(alpha),
        factory = {
            IosWebViewGlobals.setupCacheOnce()

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
            }.getOrNull() ?: NSBundle.mainBundle.URLForResource("map", withExtension = "html")
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
