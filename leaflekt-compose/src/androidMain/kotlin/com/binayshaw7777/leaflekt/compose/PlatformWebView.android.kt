package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import android.content.pm.ApplicationInfo
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceError
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader

@Composable
internal actual fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?,
    isFirstRenderDone: Boolean
) {
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    val alpha by animateFloatAsState(
        targetValue = if (isFirstRenderDone) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "mapAlpha"
    )

    AndroidView(
        modifier = modifier.alpha(alpha),
        factory = { context ->
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()
            val tileCache = TileCache.get(context)

            val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            WebView.setWebContentsDebuggingEnabled(isDebuggable)

            val pooled = WebViewPool.acquire()
            val webView = pooled ?: WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
                }
            }

            webView.contentDescription = contentDescription
            webView.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    if (consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.ERROR ||
                        consoleMessage.messageLevel() == ConsoleMessage.MessageLevel.WARNING) {
                        Log.w("Leaflekt.WebView",
                            "JS ${consoleMessage.messageLevel()}: ${consoleMessage.message()} " +
                            "(${consoleMessage.sourceId()}:${consoleMessage.lineNumber()})")
                    }
                    return super.onConsoleMessage(consoleMessage)
                }
            }
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val url = request?.url?.toString() ?: return false
                    val isInternal = url.startsWith("https://appassets.androidplatform.net/")
                    if (isInternal || isKnownMapUrl(url)) return false
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                        view?.context?.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("Leaflekt.WebView", "Failed to open external link: $url", e)
                    }
                    return true
                }

                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    val safeRequest = request ?: return null
                    val assetResponse = assetLoader.shouldInterceptRequest(safeRequest.url)
                    if (assetResponse != null) return assetResponse

                    val url = safeRequest.url.toString()
                    if (isCacheableRasterTileUrl(url)) {
                        val data = tileCache.getOrFetch(url) ?: return null
                        return WebResourceResponse(mimeTypeForTileUrl(url), null, data.inputStream())
                    }
                    return null
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    if (request?.url?.toString()?.endsWith("/favicon.ico") == true) return
                    if (request?.isForMainFrame == true) {
                        val desc = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            error?.description?.toString() ?: "Unknown error"
                        } else "Unknown error"
                        Log.e("Leaflekt.WebView", "Main frame error: ${request.url} - $desc")
                        // Replace Android's default "Webpage not available" screen with blank page
                        view?.loadData("<html><body></body></html>", "text/html", "UTF-8")
                        bridge.onMapError(desc)
                        return
                    }
                    Log.w("Leaflekt.WebView", "Sub-resource error: ${request?.url} - $error")
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    if (request?.isForMainFrame == true) {
                        val statusCode = errorResponse?.statusCode ?: 0
                        val desc = "HTTP $statusCode: ${errorResponse?.reasonPhrase}"
                        Log.e("Leaflekt.WebView", "Main frame HTTP error: ${request.url} - $desc")
                        view?.loadData("<html><body></body></html>", "text/html", "UTF-8")
                        bridge.onMapError(desc)
                    }
                }
            }

            webView.removeJavascriptInterface(JS_BRIDGE_ANDROID)
            webView.addJavascriptInterface(LeaflektJsBridgeAndroid(bridge), JS_BRIDGE_ANDROID)
            controller.setWebView(webView)
            webViewState.value = webView

            if (pooled == null) {
                webView.loadUrl(MAP_ASSET_URL_ANDROID)
            } else {
                // Pooled reuse: reset JS state first, then signal map-ready only after reset completes
                // to avoid initMapBatchScript executing before reset() clears prior session state.
                webView.evaluateJavascript("window.LeaflektBridge.reset();") {
                    webView.post {
                        bridge.onMapReady()
                        bridge.onMapFirstRender()
                    }
                }
            }

            webView
        },
        update = { webView ->
            controller.setWebView(webView)
            webView.contentDescription = contentDescription
            webViewState.value = webView
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            val webView = webViewState.value ?: return@onDispose
            controller.setWebView(null)
            webView.removeJavascriptInterface(JS_BRIDGE_ANDROID)
            webView.stopLoading()
            WebViewPool.release(webView)
            webViewState.value = null
        }
    }
}
