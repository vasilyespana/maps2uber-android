package dev.vasilyespana.maps2uber.ui.results

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.vasilyespana.maps2uber.ProbeLink
import dev.vasilyespana.maps2uber.ResolvedPage
import dev.vasilyespana.maps2uber.core.geo.LatLng
import dev.vasilyespana.maps2uber.core.geo.MapBounds
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/** Which pin the user tapped on the map. */
sealed interface SelectedPin {
    data object Main : SelectedPin
    data class Probe(val probe: ProbeLink) : SelectedPin
}

private fun pinDrawable(context: Context, @ColorInt color: Int, diameterDp: Int): Drawable {
    val d = (diameterDp * context.resources.displayMetrics.density).toInt().coerceAtLeast(1)
    val bmp = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val r = d / 2f
    val white = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = Color.WHITE }
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    canvas.drawCircle(r, r, r - 1f, white)
    canvas.drawCircle(r, r, r - 4f * context.resources.displayMetrics.density / 2f, fill)
    return BitmapDrawable(context.resources, bmp)
}

/**
 * Native OSM map (osmdroid, same tiles as the web widget — no API key):
 * green main pin on the destination + blue pins for the 10 probes.
 * Tapping a pin reports it via [onPinSelected].
 */
@Composable
fun ProbeMap(
    page: ResolvedPage,
    onPinSelected: (SelectedPin) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            // Initial framing; refined to the pin bounds once laid out.
            controller.setZoom(16.0)
            controller.setCenter(GeoPoint(page.lat, page.lng))
        }
    }

    // (Re)build the 11 markers whenever the resolved page changes, then zoom
    // so the pins fill the widget instead of sitting in a tiny cluster.
    DisposableEffect(page) {
        mapView.overlays.clear()
        val mainIcon = pinDrawable(context, Color.parseColor("#2E7D32"), 44)
        val probeIcon = pinDrawable(context, Color.parseColor("#1565C0"), 32)

        Marker(mapView).apply {
            position = GeoPoint(page.lat, page.lng)
            icon = mainIcon
            title = page.name.ifEmpty { "Destination" }
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setOnMarkerClickListener { _, _ ->
                onPinSelected(SelectedPin.Main)
                true
            }
        }.also { mapView.overlays.add(it) }

        page.probes.forEach { probe ->
            Marker(mapView).apply {
                position = GeoPoint(probe.lat, probe.lng)
                icon = probeIcon
                title = "${probe.bearingDeg.toInt()}° probe"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                setOnMarkerClickListener { _, _ ->
                    onPinSelected(SelectedPin.Probe(probe))
                    true
                }
            }.also { mapView.overlays.add(it) }
        }
        mapView.invalidate()

        // Fit the pins to the widget. Must run after layout, hence post().
        val points = listOf(LatLng(page.lat, page.lng)) +
            page.probes.map { LatLng(it.lat, it.lng) }
        MapBounds.ofPoints(points)?.let { b ->
            val box = BoundingBox(b.north, b.east, b.south, b.west)
            mapView.post { mapView.zoomToBoundingBox(box, false, 120) }
        }
        onDispose { }
    }

    // osmdroid needs explicit lifecycle forwarding.
    DisposableEffect(lifecycle, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}
