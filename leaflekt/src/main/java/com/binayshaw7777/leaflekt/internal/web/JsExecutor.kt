package com.binayshaw7777.leaflekt.internal.web

import android.util.Log
import android.webkit.WebView

internal class JsExecutor(
    private val webViewProvider: () -> WebView?
) {
    fun runJS(script: String) {
        val webView = webViewProvider.invoke() ?: return
        webView.post {
            runCatching {
                webView.evaluateJavascript(script, null)
            }.onFailure { error ->
                Log.e(TAG, "Leaflekt JS execution failed", error)
            }
        }
    }

    private companion object {
        const val TAG = "LeafleKT.JsExecutor"
    }
}
