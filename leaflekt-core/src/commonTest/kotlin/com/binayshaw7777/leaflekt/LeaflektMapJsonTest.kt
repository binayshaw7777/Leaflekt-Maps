package com.binayshaw7777.leaflekt

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LeaflektMapJsonTest {
    @Test
    fun encodesLatitudeAndLongitude() {
        val json = LeaflektMapJson.encodeLatLng(LeaflektLatLng(22.5726, 88.3639))

        assertTrue(json.contains("22.5726"))
        assertTrue(json.contains("88.3639"))
    }

    @Test
    fun escapesQuotesInStrings() {
        val encoded = LeaflektMapJson.encodeString("hello \"world\"")

        assertEquals("\"hello \\\"world\\\"\"", encoded)
    }
}
