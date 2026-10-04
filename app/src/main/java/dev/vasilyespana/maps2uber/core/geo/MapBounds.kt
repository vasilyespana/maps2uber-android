package dev.vasilyespana.maps2uber.core.geo

/** Pure geographic point, no Android dependency. */
data class LatLng(val lat: Double, val lng: Double)

/** Pure bounding box, no Android dependency (convert to osmdroid's BoundingBox at the UI layer). */
data class LatLngBounds(
    val north: Double,
    val south: Double,
    val east: Double,
    val west: Double,
)

/**
 * Computes the smallest box containing all [points], padded so the points
 * fill the map widget instead of sitting in a tiny cluster.
 *
 * A minimum span guards the degenerate single-point case (e.g. manual
 * coordinates with no probes) against infinite zoom.
 */
object MapBounds {
    /** Minimum span in degrees (~111 m of latitude) to avoid a zero-area box. */
    const val MIN_SPAN_DEG = 0.001

    fun ofPoints(points: List<LatLng>): LatLngBounds? {
        if (points.isEmpty()) return null
        var north = points.maxOf { it.lat }
        var south = points.minOf { it.lat }
        var east = points.maxOf { it.lng }
        var west = points.minOf { it.lng }
        if (north - south < MIN_SPAN_DEG) {
            val mid = (north + south) / 2
            north = mid + MIN_SPAN_DEG / 2
            south = mid - MIN_SPAN_DEG / 2
        }
        if (east - west < MIN_SPAN_DEG) {
            val mid = (east + west) / 2
            east = mid + MIN_SPAN_DEG / 2
            west = mid - MIN_SPAN_DEG / 2
        }
        return LatLngBounds(north, south, east, west)
    }
}
