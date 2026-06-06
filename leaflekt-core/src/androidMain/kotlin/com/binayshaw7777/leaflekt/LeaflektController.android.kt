package com.binayshaw7777.leaflekt

import android.webkit.WebView

actual class LeaflektController actual constructor() : LeaflektControllerBase() {
    private var webView: WebView? = null

    fun setWebView(view: WebView?) {
        webView = view
    }

    override fun platformExecuteJs(script: String) {
        webView?.post {
            webView?.evaluateJavascript(script, null)
        }
    }
}
