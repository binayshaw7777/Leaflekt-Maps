package com.binayshaw7777.leaflekt.compose

import android.content.Context
import android.util.Log
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class TileCache private constructor(context: Context, private val maxSizeBytes: Long = 50L * 1024 * 1024) {

    companion object {
        @Volatile private var instance: TileCache? = null

        fun get(context: Context): TileCache = instance ?: synchronized(this) {
            instance ?: TileCache(context.applicationContext).also { instance = it }
        }
    }

    private val cacheDir = File(context.cacheDir, "leaflekt_tiles").also { it.mkdirs() }
    private val inFlight = ConcurrentHashMap<String, CountDownLatch>()

    @Synchronized
    fun get(url: String): ByteArray? {
        val file = fileFor(url)
        if (!file.exists()) return null
        file.setLastModified(System.currentTimeMillis())
        return runCatching { file.readBytes() }.getOrNull()
    }

    @Synchronized
    fun put(url: String, data: ByteArray) {
        evictIfNeeded(data.size.toLong())
        runCatching { fileFor(url).writeBytes(data) }
    }

    fun getOrFetch(url: String): ByteArray? {
        val cached = get(url)
        if (cached != null) return cached

        val newLatch = CountDownLatch(1)
        val existing = inFlight.putIfAbsent(url, newLatch)
        if (existing != null) {
            existing.await(10, TimeUnit.SECONDS)
            return get(url)
        }

        return try {
            val data = fetchTile(url)
            if (data != null) put(url, data)
            data
        } finally {
            inFlight.remove(url)
            newLatch.countDown()
        }
    }

    private fun fileFor(url: String): File {
        val md = MessageDigest.getInstance("MD5")
        val hash = md.digest(url.toByteArray()).joinToString("") { "%02x".format(it) }
        return File(cacheDir, hash)
    }

    private fun evictIfNeeded(incoming: Long) {
        val files = cacheDir.listFiles() ?: return
        var total = files.sumOf { it.length() }
        if (total + incoming <= maxSizeBytes) return
        files.sortBy { it.lastModified() }
        for (file in files) {
            if (total + incoming <= maxSizeBytes) break
            total -= file.length()
            file.delete()
        }
    }
}

internal fun fetchTile(url: String): ByteArray? {
    return try {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000
        conn.setRequestProperty("User-Agent", "LeafleKT Android")
        if (conn.responseCode == HttpURLConnection.HTTP_OK) conn.inputStream.readBytes() else null
    } catch (e: Exception) {
        Log.w("Leaflekt.TileCache", "Tile fetch failed: $url — ${e.message}")
        null
    }
}

internal fun mimeTypeForTileUrl(url: String): String = when {
    url.endsWith(".jpg") || url.endsWith(".jpeg") -> "image/jpeg"
    url.endsWith(".pbf") -> "application/x-protobuf"
    else -> "image/png"
}

// All map-related URLs that should remain in WebView (not open external browser)
internal fun isKnownMapUrl(url: String): Boolean =
    url.contains(".tile.openstreetmap.org") ||
    url.contains(".basemaps.cartocdn.com") ||
    url.contains(".tile.opentopomap.org") ||
    url.contains("server.arcgisonline.com") ||
    url.contains("openfreemap.org")

// Only raster image tiles safe to cache — MapLibre GL manages its own cache for vector tiles
internal fun isCacheableRasterTileUrl(url: String): Boolean {
    val isRasterDomain =
        url.contains(".tile.openstreetmap.org") ||
        url.contains(".basemaps.cartocdn.com") ||
        url.contains(".tile.opentopomap.org") ||
        url.contains("server.arcgisonline.com")
    if (!isRasterDomain) return false
    return url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg")
}
