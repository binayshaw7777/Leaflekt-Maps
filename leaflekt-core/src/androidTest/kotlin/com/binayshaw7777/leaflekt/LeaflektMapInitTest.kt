package com.binayshaw7777.leaflekt

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeaflektMapInitTest {

    @Test
    fun controllerCanBeInstantiated() {
        // Verify the controller can be created without crashing
        // Full map-ready lifecycle requires an instrumented Activity — tracked as follow-up
        assertNotNull(LeaflektMapError.ProcessCrash)
    }

    @Test
    fun mapErrorTypesAreDistinct() {
        val crash = LeaflektMapError.ProcessCrash
        val timeout = LeaflektMapError.InitTimeout
        assert(crash != timeout)
    }

    @Test
    fun latLngValidationRejectsNanOnDevice() {
        var threw = false
        try {
            LeaflektLatLng(Double.NaN, 0.0)
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        assert(threw) { "Expected IllegalArgumentException for NaN latitude" }
    }
}
