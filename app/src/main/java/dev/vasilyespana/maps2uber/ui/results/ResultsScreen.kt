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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

@Composable
fun ResultsScreen(page: ResolvedPage) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
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
        }

        item {
            Button(
                onClick = { openUberLink(context, page.mainUberUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Text("Open in Uber", style = MaterialTheme.typography.titleMedium)
            }
        }

        item {
            OutlinedButton(
                onClick = { copyText(context, "Uber link", page.mainUberUrl) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Copy main link")
            }
        }

        item {
            Text("Probe points — 100 m circle", style = MaterialTheme.typography.titleMedium)
        }

        items(page.probes) { probe: ProbeLink ->
            ProbeRow(
                probe = probe,
                onOpen = { openUberLink(context, probe.uberUrl) },
                onCopy = { copyText(context, "Probe link", probe.uberUrl) },
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Text(
                    "Tip: open each probe link in the Uber app and compare the fare — " +
                        "if a 100 m shift changes the price, the pin may be sitting on a zone boundary.",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(8.dp))
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
