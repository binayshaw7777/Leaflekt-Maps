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
import platform.Foundation.NSURL
import platform.UIKit.UIColor
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
    contentDescription: String?,
    isFirstRenderDone: Boolean
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

            @OptIn(ExperimentalResourceApi::class)
            val mapHtmlUri = Res.getUri("files/map.html")
            val htmlUrl = NSURL(string = mapHtmlUri)
                ?: error("map.html not found in compose resources")
            val baseUrl = htmlUrl.URLByDeletingLastPathComponent
                ?: error("Could not resolve base URL for map resources")

            @OptIn(ExperimentalForeignApi::class)
            WKWebView(frame = cValue<CGRect>(), configuration = config).also { webView ->
                webView.opaque = false
                webView.backgroundColor = UIColor.clearColor()
                controller.setWebView(webView)
                webView.loadFileURL(htmlUrl, allowingReadAccessToURL = baseUrl)
            }
        },
        update = { webView ->
            controller.setWebView(webView)
            webView.alpha = if (isFirstRenderDone) 1.0 else 0.0
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
