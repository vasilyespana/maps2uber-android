package dev.vasilyespana.maps2uber.core.geo

import java.math.BigDecimal
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Pickup preset for the Uber link; null = rider's current location (params omitted). */
data class Pickup(val lat: Double, val lng: Double, val nickname: String)

data class ProbePoint(val bearingDeg: Double, val lat: Double, val lng: Double)

/**
 * Port of the maps2uber worker's link/probe math.
 *
 * Uber link format (byte-exact vs the worker):
 *   https://m.uber.com/ul/?action=setPickup
 *   [&pickup[latitude]=..&pickup[longitude]=..&pickup[nickname]=..]
 *   &dropoff[latitude]=..&dropoff[longitude]=..&dropoff[nickname]=..&dropoff[formatted_address]=..
 * Text fields use encodeURIComponent semantics.
 */
object UberLinks {
    const val PROBE_DISTANCE_M = 100.0
    const val EARTH_RADIUS_M = 6_371_000.0
    const val PROBE_COUNT = 10

    /** JavaScript encodeURIComponent semantics (UTF-8). */
    fun encodeUriComponent(s: String): String {
        val sb = StringBuilder(s.length)
        for (b in s.toByteArray(Charsets.UTF_8)) {
            val c = b.toInt() and 0xFF
            val unreserved =
                c in 0x61..0x7A || c in 0x41..0x5A || c in 0x30..0x39 ||
                    c == 0x2D || c == 0x5F || c == 0x2E || c == 0x21 || // - _ . !
                    c == 0x7E || c == 0x2A || c == 0x27 || c == 0x28 || c == 0x29 // ~ * ' ( )
            if (unreserved) {
                sb.append(c.toChar())
            } else {
                sb.append('%')
                sb.append(c.toString(16).uppercase().padStart(2, '0'))
            }
        }
        return sb.toString()
    }

    /** Like JavaScript Number.toString for typical coordinate magnitudes. */
    fun formatCoord(d: Double): String =
        BigDecimal.valueOf(d).stripTrailingZeros().toPlainString()

    fun uberLink(
        lat: Double,
        lng: Double,
        name: String,
        address: String,
        pickup: Pickup?,
    ): String {
        val sb = StringBuilder("https://m.uber.com/ul/?action=setPickup")
        if (pickup != null) {
            sb.append("&pickup[latitude]=").append(formatCoord(pickup.lat))
            sb.append("&pickup[longitude]=").append(formatCoord(pickup.lng))
            sb.append("&pickup[nickname]=").append(encodeUriComponent(pickup.nickname))
        }
        sb.append("&dropoff[latitude]=").append(formatCoord(lat))
        sb.append("&dropoff[longitude]=").append(formatCoord(lng))
        sb.append("&dropoff[nickname]=").append(encodeUriComponent(name))
        sb.append("&dropoff[formatted_address]=").append(encodeUriComponent(address))
        return sb.toString()
    }

    /**
     * Haversine destination point. Returns (lat, lng) in degrees.
     * Same formula as the worker's destPoint().
     */
    fun destPoint(
        latDeg: Double,
        lngDeg: Double,
        bearingDeg: Double,
        distanceM: Double,
    ): Pair<Double, Double> {
        val rad = Math.PI / 180.0
        val lat1 = latDeg * rad
        val lng1 = lngDeg * rad
        val brng = bearingDeg * rad
        val delta = distanceM / EARTH_RADIUS_M
        val lat2 = asin(
            sin(lat1) * cos(delta) + cos(lat1) * sin(delta) * cos(brng),
        )
        val lng2 = lng1 + atan2(
            sin(brng) * sin(delta) * cos(lat1),
            cos(delta) - sin(lat1) * sin(lat2),
        )
        return Pair(lat2 / rad, lng2 / rad)
    }

    /** 10 probe points at bearings 0, 36, …, 324 deg, 100 m out. */
    fun probePoints(lat: Double, lng: Double): List<ProbePoint> =
        (0 until PROBE_COUNT).map { i ->
            val bearing = i * 36.0
            val (plat, plng) = destPoint(lat, lng, bearing, PROBE_DISTANCE_M)
            ProbePoint(bearing, plat, plng)
        }

    fun haversineM(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val rad = Math.PI / 180.0
        val dLat = (lat2 - lat1) * rad
        val dLng = (lng2 - lng1) * rad
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(lat1 * rad) * cos(lat2 * rad) * sin(dLng / 2) * sin(dLng / 2)
        return 2 * EARTH_RADIUS_M * asin(sqrt(a.coerceIn(0.0, 1.0)))
    }
}
