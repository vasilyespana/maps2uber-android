package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.geo.Pickup
import dev.vasilyespana.maps2uber.core.geo.UberLinks
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

/**
 * Fail-first: port of the worker's link/probe math.
 * Expected strings were derived from the worker's documented format:
 *   https://m.uber.com/ul/?action=setPickup
 *   [&pickup[latitude]=..&pickup[longitude]=..&pickup[nickname]=..]
 *   &dropoff[latitude]=..&dropoff[longitude]=..&dropoff[nickname]=..&dropoff[formatted_address]=..
 * with encodeURIComponent semantics on the text fields.
 */
class UberLinksTest {

    @Test
    fun uberLink_noPickup_byteExact() {
        val link = UberLinks.uberLink(
            lat = 18.4665,
            lng = -66.1183,
            name = "Calle del Cristo",
            address = "Calle del Cristo, La Perla",
            pickup = null,
        )
        assertEquals(
            "https://m.uber.com/ul/?action=setPickup" +
                "&dropoff[latitude]=18.4665" +
                "&dropoff[longitude]=-66.1183" +
                "&dropoff[nickname]=Calle%20del%20Cristo" +
                "&dropoff[formatted_address]=Calle%20del%20Cristo%2C%20La%20Perla",
            link,
        )
    }

    @Test
    fun uberLink_withPickupPreset_byteExact() {
        val link = UberLinks.uberLink(
            lat = 18.4665,
            lng = -66.1183,
            name = "Cliente (La Perla)",
            address = "Calle del Cristo 123, San Juan",
            pickup = Pickup(lat = 18.2, lng = -66.0, nickname = "Warehouse"),
        )
        assertEquals(
            "https://m.uber.com/ul/?action=setPickup" +
                "&pickup[latitude]=18.2" +
                "&pickup[longitude]=-66" +
                "&pickup[nickname]=Warehouse" +
                "&dropoff[latitude]=18.4665" +
                "&dropoff[longitude]=-66.1183" +
                "&dropoff[nickname]=Cliente%20(La%20Perla)" +
                "&dropoff[formatted_address]=Calle%20del%20Cristo%20123%2C%20San%20Juan",
            link,
        )
    }

    @Test
    fun uberLink_specialChars_encodedLikeEncodeURIComponent() {
        val link = UberLinks.uberLink(
            lat = 1.0, lng = 2.0,
            name = "Café & Bar 'El Sol'!",
            address = "a/b?c=d",
            pickup = null,
        )
        // encodeURIComponent leaves: A-Za-z0-9 - _ . ! ~ * ' ( )
        // "&" -> %26, "/" -> %2F, "?" -> %3F, "=" -> %3D, é -> %C3%A9
        assertEquals(
            "https://m.uber.com/ul/?action=setPickup" +
                "&dropoff[latitude]=1" +
                "&dropoff[longitude]=2" +
                "&dropoff[nickname]=Caf%C3%A9%20%26%20Bar%20'El%20Sol'!" +
                "&dropoff[formatted_address]=a%2Fb%3Fc%3Dd",
            link,
        )
    }

    @Test
    fun destPoint_bearingNorth_100m() {
        val (lat, lng) = UberLinks.destPoint(18.4665, -66.1183, 0.0, 100.0)
        assertEquals(18.46739935, lat, 1e-6)
        assertEquals(-66.1183, lng, 1e-9)
    }

    @Test
    fun destPoint_bearingEast_100m() {
        val (lat, lng) = UberLinks.destPoint(18.4665, -66.1183, 90.0, 100.0)
        // Great-circle eastward travel curves a hair south of the start parallel.
        assertEquals(18.4664999976, lat, 1e-6)
        assertEquals(-66.11735186, lng, 1e-6)
    }

    @Test
    fun destPoint_zeroDistance_returnsStart() {
        val (lat, lng) = UberLinks.destPoint(18.4665, -66.1183, 123.0, 0.0)
        assertEquals(18.4665, lat, 1e-12)
        assertEquals(-66.1183, lng, 1e-12)
    }

    @Test
    fun probePoints_tenPoints_bearingsEvery36deg() {
        val probes = UberLinks.probePoints(18.4665, -66.1183)
        assertEquals(10, probes.size)
        probes.forEachIndexed { i, p ->
            assertEquals(i * 36.0, p.bearingDeg, 1e-9)
        }
        // First probe == due north == destPoint(0°).
        val (nLat, nLng) = UberLinks.destPoint(18.4665, -66.1183, 0.0, 100.0)
        assertEquals(nLat, probes[0].lat, 1e-12)
        assertEquals(nLng, probes[0].lng, 1e-12)
        // Every probe is ~100 m from the center.
        probes.forEach { p ->
            val d = UberLinks.haversineM(18.4665, -66.1183, p.lat, p.lng)
            assertEquals(100.0, d, 0.5)
            assert(abs(p.lat - 18.4665) > 1e-9 || abs(p.lng - -66.1183) > 1e-9)
        }
    }

    @Test
    fun formatCoord_matchesJsNumberToString() {
        assertEquals("18.4665", UberLinks.formatCoord(18.4665))
        assertEquals("18.5", UberLinks.formatCoord(18.5))
        assertEquals("-66", UberLinks.formatCoord(-66.0))
        assertEquals("0", UberLinks.formatCoord(0.0))
    }
}
