package com.binayshaw7777.leaflekt.compose

import android.webkit.WebView

actual class LeaflektController internal actual constructor() : LeaflektControllerBase() {
    private var webView: WebView? = null

    internal fun setWebView(view: WebView?) {
        webView = view
    }

    override fun platformExecuteJs(script: String) {
        webView?.post {
            webView?.evaluateJavascript(script, null)
        }
    }
}
