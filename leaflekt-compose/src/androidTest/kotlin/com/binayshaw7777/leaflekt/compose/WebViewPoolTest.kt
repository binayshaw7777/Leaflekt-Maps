package com.binayshaw7777.leaflekt.compose

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WebViewPoolTest {

    @After
    fun tearDown() {
        WebViewPool.clear()
    }

    @Test
    fun warmUpCreatesInstance() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        WebViewPool.warmUp(ctx)
        assert(WebViewPool.hasInstance()) { "warmUp should create a pooled instance" }
    }

    @Test
    fun acquireReturnsInstanceAndClearsPool() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        WebViewPool.warmUp(ctx)
        val webView = WebViewPool.acquire()
        assertNotNull(webView)
        assert(!WebViewPool.hasInstance()) { "pool should be empty after acquire" }
        webView?.destroy()
    }

    @Test
    fun acquireFromEmptyPoolReturnsNull() {
        val result = WebViewPool.acquire()
        assertNull(result)
    }

    @Test
    fun doubleWarmUpIsIdempotent() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        WebViewPool.warmUp(ctx)
        WebViewPool.warmUp(ctx)
        assert(WebViewPool.hasInstance()) { "second warmUp should not crash or duplicate" }
    }
}
