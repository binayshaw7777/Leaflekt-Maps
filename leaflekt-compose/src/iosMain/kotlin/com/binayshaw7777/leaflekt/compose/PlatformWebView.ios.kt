package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.CoreGraphics.CGRect
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.WebKit.WKUserContentController
import platform.WebKit.WKUserScript
import platform.WebKit.WKUserScriptInjectionTime
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration

@Composable
internal actual fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?
) {
    val messageHandler = remember { WeakScriptMessageHandler(bridge) }

    UIKitView(
        modifier = modifier,
        factory = {
            val userContentController = WKUserContentController()
            userContentController.addScriptMessageHandler(messageHandler, name = JS_BRIDGE_IOS)

            val bridgeNameScript = WKUserScript(
                source = "window.bridgeName = '$JS_BRIDGE_IOS';",
                injectionTime = WKUserScriptInjectionTime.WKUserScriptInjectionTimeAtDocumentStart,
                forMainFrameOnly = true
            )
            userContentController.addUserScript(bridgeNameScript)

            val config = WKWebViewConfiguration()
            config.userContentController = userContentController

            val bundle = NSBundle.mainBundle
            val htmlPath = bundle.pathForResource("map", ofType = "html")
                ?: error("map.html not found in bundle")
            val htmlUrl = NSURL.fileURLWithPath(htmlPath)
            val bundleUrl = bundle.bundleURL

            @OptIn(ExperimentalForeignApi::class)
            WKWebView(frame = cValue<CGRect>(), configuration = config).also { webView ->
                controller.setWebView(webView)
                webView.loadFileURL(htmlUrl, allowingReadAccessToURL = bundleUrl)
            }
        },
        update = { webView ->
            controller.setWebView(webView)
        },
        onRelease = { webView ->
            controller.setWebView(null)
            webView.configuration.userContentController.removeScriptMessageHandlerForName(JS_BRIDGE_IOS)
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            controller.setWebView(null)
        }
    }
}
