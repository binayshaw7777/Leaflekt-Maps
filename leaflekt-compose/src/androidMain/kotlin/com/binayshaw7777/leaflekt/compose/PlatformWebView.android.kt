package com.binayshaw7777.leaflekt.compose

import com.binayshaw7777.leaflekt.*

import android.content.pm.ApplicationInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.RenderProcessGoneDetail
import android.webkit.ServiceWorkerClient
import android.webkit.ServiceWorkerController
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceError
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.webkit.WebViewAssetLoader

@Composable
internal actual fun PlatformWebView(
    modifier: Modifier,
    controller: LeaflektController,
    bridge: LeaflektBridgeCallbacks,
    contentDescription: String?,
    isFirstRenderDone: Boolean
) {
    val context = LocalContext.current
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var crashRestartKey by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val assetLoader = remember(context) {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    // Process-global setup — runs once per context, outside crash-restart key
    DisposableEffect(context) {
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        WebView.setWebContentsDebuggingEnabled(isDebuggable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ServiceWorkerController.getInstance().setServiceWorkerClient(object : ServiceWorkerClient() {
                override fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? =
                    assetLoader.shouldInterceptRequest(request.url)
            })
        }
        onDispose { }
    }

    val alpha by animateFloatAsState(
        targetValue = if (isFirstRenderDone) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "mapAlpha"
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                webViewState.value?.freeMemory()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    key(crashRestartKey) {
        AndroidView(
            modifier = modifier.alpha(alpha),
            factory = { ctx ->
                val tileCache = TileCache.get(ctx)

                val pooled = WebViewPool.acquire()
                val webView = pooled ?: WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.allowFileAccess = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        setRendererPriorityPolicy(WebView.RENDERER_PRIORITY_IMPORTANT, false)
                    }
                }

                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

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
                            val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                error?.description?.toString() ?: "Unknown error"
                            } else "Unknown error"
                            Log.e("Leaflekt.WebView", "Main frame error: ${request.url} - $desc")
                            // Reload from local assets — should always succeed even without network
                            view?.loadUrl(MAP_ASSET_URL_ANDROID)
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
                            view?.loadUrl(MAP_ASSET_URL_ANDROID)
                            bridge.onMapError(desc)
                        }
                    }

                    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                        val crashed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) detail?.didCrash() else null
                        Log.e("Leaflekt.WebView", "Renderer process gone — crashed=$crashed")
                        controller.resetState()
                        controller.emitError(LeaflektMapError.ProcessCrash)
                        bridge.onMapError("ProcessCrash")
                        webViewState.value = null
                        crashRestartKey++
                        return true
                    }
                }

                webView.removeJavascriptInterface(JS_BRIDGE_ANDROID)
                webView.addJavascriptInterface(LeaflektJsBridgeAndroid(bridge), JS_BRIDGE_ANDROID)
                controller.setWebView(webView)
                webViewState.value = webView

                if (pooled == null) {
                    webView.loadUrl(MAP_ASSET_URL_ANDROID)
                } else {
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
    }

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

actual fun prewarmLeaflektWebView() {
    // Android WebViewPool handles pre-warming; nothing needed here
}
