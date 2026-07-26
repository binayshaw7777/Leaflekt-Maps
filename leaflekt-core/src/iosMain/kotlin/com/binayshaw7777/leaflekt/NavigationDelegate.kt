package com.binayshaw7777.leaflekt

import platform.Foundation.NSError
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject

class NavigationDelegate(private val callbacks: LeaflektBridgeCallbacks) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(webView: WKWebView, didFailProvisionalNavigation: WKNavigation?, withError: NSError) {
        callbacks.onMapError(withError.localizedDescription)
    }

    override fun webViewWebContentProcessDidTerminate(webView: WKWebView) {
        callbacks.onMapError("WKWebView process terminated. Reloading web content.")
        webView.reload()
    }
}
