package com.binayshaw7777.leaflekt

import kotlin.test.Test
import kotlin.test.assertFailsWith

class LeaflektLatLngValidationTest {

    @Test
    fun validCoordinatesPass() {
        LeaflektLatLng(0.0, 0.0)
        LeaflektLatLng(90.0, 180.0)
        LeaflektLatLng(-90.0, -180.0)
        LeaflektLatLng(22.5726, 88.3639)
    }

    @Test
    fun latitudeAbove90Throws() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(90.0001, 0.0) }
    }

    @Test
    fun latitudeBelow90Throws() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(-91.0, 0.0) }
    }

    @Test
    fun longitudeAbove180Throws() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(0.0, 180.0001) }
    }

    @Test
    fun longitudeBelow180Throws() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(0.0, -181.0) }
    }

    @Test
    fun nanLatitudeThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(Double.NaN, 0.0) }
    }

    @Test
    fun nanLongitudeThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(0.0, Double.NaN) }
    }

    @Test
    fun infiniteLatitudeThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(Double.POSITIVE_INFINITY, 0.0) }
    }

    @Test
    fun infiniteLongitudeThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektLatLng(0.0, Double.NEGATIVE_INFINITY) }
    }

    @Test
    fun markerInfoOutOfRangeLatThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektMarkerInfo(lat = 91.0, lng = 0.0) }
    }

    @Test
    fun markerInfoOutOfRangeLngThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektMarkerInfo(lat = 0.0, lng = 181.0) }
    }

    @Test
    fun markerInfoNanLatThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektMarkerInfo(lat = Double.NaN, lng = 0.0) }
    }

    @Test
    fun markerInfoBlankIdThrows() {
        assertFailsWith<IllegalArgumentException> { LeaflektMarkerInfo(id = "  ", lat = 0.0, lng = 0.0) }
    }

    @Test
    fun markerIconInfoBlankDataUrlThrows() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektMarkerIconInfo(dataUrl = "", widthPx = 10, heightPx = 10, anchorFractionX = 0.5f, anchorFractionY = 0.5f)
        }
    }

    @Test
    fun markerIconInfoNegativeWidthThrows() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektMarkerIconInfo(dataUrl = "data:image/png;base64,abc", widthPx = 0, heightPx = 10, anchorFractionX = 0.5f, anchorFractionY = 0.5f)
        }
    }

    @Test
    fun markerIconInfoAnchorOutOfRangeThrows() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektMarkerIconInfo(dataUrl = "data:image/png;base64,abc", widthPx = 10, heightPx = 10, anchorFractionX = 1.5f, anchorFractionY = 0.5f)
        }
    }

    @Test
    fun polygonInfoRequiresThreePoints() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektPolygonInfo(
                id = "p1",
                points = listOf(LeaflektLatLng(0.0, 0.0), LeaflektLatLng(1.0, 1.0))
            )
        }
    }

    @Test
    fun polygonInfoHoleRequiresThreePoints() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektPolygonInfo(
                id = "p1",
                points = listOf(LeaflektLatLng(0.0, 0.0), LeaflektLatLng(1.0, 1.0), LeaflektLatLng(2.0, 0.0)),
                holes = listOf(listOf(LeaflektLatLng(0.1, 0.1), LeaflektLatLng(0.2, 0.2)))
            )
        }
    }

    @Test
    fun circleInfoZeroRadiusThrows() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektCircleInfo(id = "c1", center = LeaflektLatLng(0.0, 0.0), radiusMeters = 0.0)
        }
    }

    @Test
    fun circleInfoExceedsEarthRadiusThrows() {
        assertFailsWith<IllegalArgumentException> {
            LeaflektCircleInfo(id = "c1", center = LeaflektLatLng(0.0, 0.0), radiusMeters = 7_000_000.0)
        }
    }
}
