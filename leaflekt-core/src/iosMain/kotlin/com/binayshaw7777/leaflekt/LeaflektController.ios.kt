package com.binayshaw7777.leaflekt

import platform.WebKit.WKWebView

actual class LeaflektController actual constructor() : LeaflektControllerBase() {
    private var webView: WKWebView? = null

    fun setWebView(view: WKWebView?) {
        webView = view
    }

    override fun platformExecuteJs(script: String) {
        webView?.evaluateJavaScript(script, null)
    }
}
