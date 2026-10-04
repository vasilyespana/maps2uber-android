package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.geo.LatLng
import dev.vasilyespana.maps2uber.core.geo.MapBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapBoundsTest {

    @Test
    fun `bounds of points cover the extremes`() {
        val bounds = MapBounds.ofPoints(
            listOf(LatLng(18.1, -66.5), LatLng(18.2, -66.4), LatLng(18.15, -66.45)),
        )!!
        assertEquals(18.2, bounds.north, 1e-9)
        assertEquals(18.1, bounds.south, 1e-9)
        assertEquals(-66.4, bounds.east, 1e-9)
        assertEquals(-66.5, bounds.west, 1e-9)
    }

    @Test
    fun `empty list returns null`() {
        assertNull(MapBounds.ofPoints(emptyList()))
    }

    @Test
    fun `single point is padded to the minimum span`() {
        val bounds = MapBounds.ofPoints(listOf(LatLng(18.1, -66.5)))!!
        assertTrue(bounds.north - bounds.south >= MapBounds.MIN_SPAN_DEG - 1e-12)
        assertTrue(bounds.east - bounds.west >= MapBounds.MIN_SPAN_DEG - 1e-12)
        // stays centered on the point
        assertEquals(18.1, (bounds.north + bounds.south) / 2, 1e-9)
        assertEquals(-66.5, (bounds.east + bounds.west) / 2, 1e-9)
    }

    @Test
    fun `a 100m probe circle yields a non-trivial box`() {
        // Simulates destination + 10 probes on a 100 m circle (~0.0009 deg).
        val center = LatLng(18.4665, -66.1183)
        val pts = mutableListOf(center)
        repeat(10) { i ->
            val rad = Math.toRadians(i * 36.0)
            pts += LatLng(
                center.lat + 0.0009 * Math.cos(rad),
                center.lng + 0.0009 * Math.sin(rad),
            )
        }
        val bounds = MapBounds.ofPoints(pts)!!
        assertTrue(bounds.north > center.lat)
        assertTrue(bounds.south < center.lat)
        assertTrue(bounds.east > center.lng)
        assertTrue(bounds.west < center.lng)
    }
}
