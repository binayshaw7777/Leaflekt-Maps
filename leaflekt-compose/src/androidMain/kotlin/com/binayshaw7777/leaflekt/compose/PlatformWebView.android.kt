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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader

@Composable
internal actual fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?
) {
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()

            val tileCache = TileCache(context)
            val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            WebView.setWebContentsDebuggingEnabled(isDebuggable)
            WebView(context).apply {
                this.contentDescription = contentDescription
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true

                webChromeClient = object : WebChromeClient() {
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

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        val url = request?.url?.toString() ?: return false
                        val isInternal = url.startsWith("https://appassets.androidplatform.net/")
                        if (isInternal || isTileUrl(url)) return false
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
                        if (isTileUrl(url)) {
                            val cached = tileCache.get(url)
                            if (cached != null) {
                                return WebResourceResponse(mimeTypeForTileUrl(url), null, cached.inputStream())
                            }
                            val data = fetchTile(url) ?: return null
                            tileCache.put(url, data)
                            return WebResourceResponse(mimeTypeForTileUrl(url), null, data.inputStream())
                        }

                        return null
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        if (request?.url?.toString()?.endsWith("/favicon.ico") == true) return
                        Log.e("Leaflekt.WebView", "Web resource error: ${request?.url} - $error")
                        super.onReceivedError(view, request, error)
                    }
                }

                addJavascriptInterface(LeaflektJsBridgeAndroid(bridge), JS_BRIDGE_ANDROID)
                loadUrl(MAP_ASSET_URL_ANDROID)
                controller.setWebView(this)
                webViewState.value = this
            }
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
            webView.destroy()
            webViewState.value = null
        }
    }
}
