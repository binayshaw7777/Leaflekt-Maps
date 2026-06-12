package com.binayshaw7777.leaflekt.compose

import android.webkit.WebView

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

    fun clear() {
        instance?.destroy()
        instance = null
    }
}
