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

private fun openUberLink(context: Context, url: String) {
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
    onUberLinkClick: (deepLink: String, linkType: String, probeBearing: Int?) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current
    var selectedPin by remember { mutableStateOf<SelectedPin?>(null) }
    val sheetState = rememberModalBottomSheetState()

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
                    onUberLinkClick(page.mainUberUrl, "destination", null)
                    openUberLink(context, page.mainUberUrl)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
            ) {
                Text("Open in Uber", style = MaterialTheme.typography.titleMedium)
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
                onOpen = {
                    onUberLinkClick(probe.uberUrl, "probe", probe.bearingDeg.toInt())
                    openUberLink(context, probe.uberUrl)
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
                "Tip: tap a blue pin to open that probe in Uber — if a 100 m shift " +
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
            sheetState = sheetState,
        ) {
            PinSheetContent(
                pin = pin,
                page = page,
                onOpen = { url ->
                    val (linkType, bearing) = when (pin) {
                        is SelectedPin.Main -> "destination" to null
                        is SelectedPin.Probe -> "probe" to pin.probe.bearingDeg.toInt()
                    }
                    onUberLinkClick(url, linkType, bearing)
                    selectedPin = null
                    openUberLink(context, url)
                },
                onCopy = { url -> copyText(context, "Probe link", url) },
            )
        }
    }
}

@Composable
private fun ProbeRow(probe: ProbeLink, onOpen: () -> Unit, onCopy: () -> Unit) {
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
            TextButton(onClick = onOpen) { Text("Uber") }
        }
    }
}

@Composable
private fun PinSheetContent(
    pin: SelectedPin,
    page: ResolvedPage,
    onOpen: (String) -> Unit,
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
            onClick = { onOpen(uberUrl) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text("Open in Uber", style = MaterialTheme.typography.titleMedium)
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
