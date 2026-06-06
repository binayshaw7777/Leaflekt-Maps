package com.binayshaw7777.leaflekt.compose

import platform.WebKit.WKWebView

actual class LeaflektController internal actual constructor() : LeaflektControllerBase() {
    private var webView: WKWebView? = null

    internal fun setWebView(view: WKWebView?) {
        webView = view
    }

    override fun platformExecuteJs(script: String) {
        webView?.evaluateJavaScript(script, null)
    }
}
