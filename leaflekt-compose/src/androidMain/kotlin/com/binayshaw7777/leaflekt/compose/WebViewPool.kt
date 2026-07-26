package com.binayshaw7777.leaflekt.compose

import android.content.Context
import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import com.binayshaw7777.leaflekt.MAP_ASSET_URL_ANDROID

internal object WebViewPool {
    private var instance: WebView? = null

    fun acquire(): WebView? = instance?.also { instance = null }

    fun release(webView: WebView) {
        if (instance == null) {
            instance = webView
        } else {
            webView.destroy()
        }
    }

    fun hasInstance(): Boolean = instance != null

    fun warmUp(context: Context) {
        if (instance != null) return
        val appContext = context.applicationContext
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(appContext))
            .build()
        val webView = WebView(appContext).apply {
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
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                val safeRequest = request ?: return null
                return assetLoader.shouldInterceptRequest(safeRequest.url)
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    val desc = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        error?.description?.toString() ?: "Unknown error"
                    } else "Unknown error"
                    Log.e("Leaflekt.WarmUp", "Warm-up load failed: $desc")
                }
            }
        }
        webView.loadUrl(MAP_ASSET_URL_ANDROID)
        instance = webView
    }

    fun clear() {
        instance?.destroy()
        instance = null
    }
}

/**
 * Pre-warms a [WebView] with the LeafleKT map page loaded so that the first
 * [LeaflektMap] composition enters the screen instantly without cold-start overhead.
 * Call from [android.app.Application.onCreate] or before navigating to any map screen.
 * Must be called on the main thread.
 */
public fun warmUpLeaflektMap(context: Context) {
    WebViewPool.warmUp(context)
}
