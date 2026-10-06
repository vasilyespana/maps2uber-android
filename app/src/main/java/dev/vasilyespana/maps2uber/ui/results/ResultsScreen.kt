package dev.vasilyespana.maps2uber.ui.results

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.vasilyespana.maps2uber.ProbeLink
import dev.vasilyespana.maps2uber.ResolvedPage
import dev.vasilyespana.maps2uber.core.geo.UberLinks
import dev.vasilyespana.maps2uber.core.rides.RideLatLng
import dev.vasilyespana.maps2uber.core.rides.RideProvider
import dev.vasilyespana.maps2uber.core.rides.RideProviderLauncher
import dev.vasilyespana.maps2uber.core.rides.RideProviders

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (e: Exception) {
        Toast.makeText(context, "No app can open this link", Toast.LENGTH_SHORT).show()
    }
}

private fun copyText(context: Context, label: String, text: String) {
    val cm = context.getSystemService(ClipboardManager::class.java)
    cm.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    page: ResolvedPage,
    provider: RideProvider,
) {
    val context = LocalContext.current
    var selectedPin by remember { mutableStateOf<SelectedPin?>(null) }
    var fallbackProvider by remember { mutableStateOf<RideProvider?>(null) }
    var fallbackDest by remember { mutableStateOf<RideLatLng?>(null) }
    var fallbackDestName by remember { mutableStateOf("") }
    val pinSheetState = rememberModalBottomSheetState()
    val fallbackSheetState = rememberModalBottomSheetState()

    val origin = page.pickup?.let { RideLatLng(it.lat, it.lng) }

    /**
     * Opens the provider app for [lat]/[lng]. When the app isn't installed,
     * stashes the request and shows the install fallback sheet instead.
     */
    fun openRide(p: RideProvider, lat: Double, lng: Double, name: String) {
        val dest = RideLatLng(lat, lng)
        val intent = RideProviderLauncher.rideIntent(context, p, origin, dest, name)
        if (intent != null) {
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No app can open this link", Toast.LENGTH_SHORT).show()
            }
        } else {
            fallbackProvider = p
            fallbackDest = dest
            fallbackDestName = name
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    page.name.ifEmpty { "Dropped pin" },
                    style = MaterialTheme.typography.titleLarge,
                )
                if (page.address.isNotEmpty() && page.address != page.name) {
                    Text(page.address, style = MaterialTheme.typography.bodyMedium)
                }
                Text(
                    "${UberLinks.formatCoord(page.lat)}, ${UberLinks.formatCoord(page.lng)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Visual pins widget (same OSM tiles as the web page): green = destination,
        // blue = the 10 fare probes. Tap a pin to choose it. The map zooms so the
        // pins fill the widget.
        ProbeMap(
            page = page,
            onPinSelected = { selectedPin = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp)),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = {
                    openRide(provider, page.lat, page.lng, page.name.ifEmpty { "Dropped pin" })
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text("Open in ${provider.name}", style = MaterialTheme.typography.titleMedium)
            }
            OutlinedButton(
                onClick = { copyText(context, "Uber link", page.mainUberUrl) },
                modifier = Modifier.height(56.dp),
            ) {
                Text("Copy")
            }
        }

        Text("Probe points — 100 m circle", style = MaterialTheme.typography.titleMedium)
        page.probes.forEach { probe ->
            ProbeRow(
                probe = probe,
                providerName = provider.name,
                onOpen = {
                    openRide(
                        provider,
                        probe.lat,
                        probe.lng,
                        page.name.ifEmpty { "Dropped pin" },
                    )
                },
                onCopy = { copyText(context, "Probe link", probe.uberUrl) },
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Text(
                "Tip: tap a blue pin to open that probe in ${provider.name} — if a 100 m shift " +
                    "changes the fare, the pin may be sitting on a zone boundary.",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Spacer(Modifier.height(4.dp))
    }

    selectedPin?.let { pin ->
        ModalBottomSheet(
            onDismissRequest = { selectedPin = null },
            sheetState = pinSheetState,
        ) {
            PinSheetContent(
                pin = pin,
                page = page,
                provider = provider,
                onOpen = { lat, lng, name ->
                    selectedPin = null
                    openRide(provider, lat, lng, name)
                },
                onCopy = { url -> copyText(context, "Probe link", url) },
            )
        }
    }

    val fp = fallbackProvider
    val fd = fallbackDest
    if (fp != null && fd != null) {
        ModalBottomSheet(
            onDismissRequest = { fallbackProvider = null },
            sheetState = fallbackSheetState,
        ) {
            ProviderFallbackSheet(
                provider = fp,
                dest = fd,
                origin = origin,
                onDismiss = { fallbackProvider = null },
            )
        }
    }
}

/**
 * Shown when the selected provider's app isn't installed. Offers the Play
 * Store listing, the universal web fallback (when the provider publishes
 * one), and a 1-tap Uber backup.
 */
@Composable
private fun ProviderFallbackSheet(
    provider: RideProvider,
    dest: RideLatLng,
    origin: RideLatLng?,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            "${provider.name} isn't installed",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            "Install it to book this ride, open the web version, or continue with Uber.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        if (provider.playStorePackage != null) {
            Button(
                onClick = {
                    RideProviderLauncher.playStoreIntent(provider)?.let {
                        try {
                            context.startActivity(it)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Couldn't open the Play Store",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Get ${provider.name} on Google Play")
            }
        }
        val webUrl = provider.buildUniversalUrl(dest)
        if (webUrl != null) {
            OutlinedButton(
                onClick = {
                    openUrl(context, webUrl)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continue in browser")
            }
        }
        OutlinedButton(
            onClick = {
                val uber = RideProviders.default
                // 1-tap backup: prefer the universal URL so it works even
                // when the Uber app isn't installed either.
                val backup = uber.buildUniversalUrl(dest)
                    ?: uber.buildDeepLink(origin, dest, "")!!
                openUrl(context, backup)
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Use Uber instead")
        }
    }
}

@Composable
private fun ProbeRow(
    probe: ProbeLink,
    providerName: String,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    "${probe.bearingDeg.toInt()}°",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    "${UberLinks.formatCoord(probe.lat)}, ${UberLinks.formatCoord(probe.lng)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onCopy) { Text("Copy") }
            TextButton(onClick = onOpen) { Text(providerName) }
        }
    }
}

@Composable
private fun PinSheetContent(
    pin: SelectedPin,
    page: ResolvedPage,
    provider: RideProvider,
    onOpen: (lat: Double, lng: Double, name: String) -> Unit,
    onCopy: (String) -> Unit,
) {
    val (title, subtitle, lat, lng, uberUrl) = when (pin) {
        is SelectedPin.Main -> Quint(
            page.name.ifEmpty { "Destination" },
            page.address,
            page.lat, page.lng, page.mainUberUrl,
        )
        is SelectedPin.Probe -> Quint(
            "Probe ${pin.probe.bearingDeg.toInt()}° — 100 m",
            "Fare probe around ${page.name.ifEmpty { "the destination" }}",
            pin.probe.lat, pin.probe.lng, pin.probe.uberUrl,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (subtitle.isNotEmpty()) {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            "${UberLinks.formatCoord(lat)}, ${UberLinks.formatCoord(lng)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Button(
            onClick = { onOpen(lat, lng, title) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("Open in ${provider.name}", style = MaterialTheme.typography.titleMedium)
        }
        OutlinedButton(
            onClick = { onCopy(uberUrl) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Copy link")
        }
    }
}

private data class Quint(
    val title: String,
    val subtitle: String,
    val lat: Double,
    val lng: Double,
    val uberUrl: String,
)
