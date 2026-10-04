package dev.vasilyespana.maps2uber.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vasilyespana.maps2uber.core.geo.UberLinks
import dev.vasilyespana.maps2uber.core.settings.PickupMode
import dev.vasilyespana.maps2uber.core.settings.PickupPreset
import dev.vasilyespana.maps2uber.core.settings.SettingsStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val store: SettingsStore,
) : ViewModel() {
    val settings = store.settings

    fun setMode(mode: PickupMode) = viewModelScope.launch { store.setPickupMode(mode) }
    fun savePreset(name: String, lat: Double, lng: Double) =
        viewModelScope.launch { store.setPreset(PickupPreset(name, lat, lng)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settings by viewModel.settings.collectAsState(initial = null)

    var presetName by remember { mutableStateOf("") }
    var presetLat by remember { mutableStateOf("") }
    var presetLng by remember { mutableStateOf("") }
    var presetError by remember { mutableStateOf<String?>(null) }
    var detected by remember { mutableStateOf<Location?>(null) }
    var locationMsg by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { it }) {
            scope.launch {
                try {
                    val client = LocationServices.getFusedLocationProviderClient(context)
                    val loc = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                    detected = loc
                    locationMsg = if (loc != null) {
                        "Detected: ${UberLinks.formatCoord(loc.latitude)}, ${UberLinks.formatCoord(loc.longitude)}"
                    } else {
                        "Location unavailable — try again outdoors."
                    }
                } catch (e: SecurityException) {
                    locationMsg = "Location permission denied."
                } catch (e: Exception) {
                    locationMsg = "Couldn't get location: ${e.message}"
                }
            }
        } else {
            locationMsg = "Location permission denied."
        }
    }

    // Seed the preset fields once settings load.
    val s = settings
    if (s != null && presetName.isEmpty() && presetLat.isEmpty() && s.preset.name.isNotEmpty()) {
        presetName = s.preset.name
        presetLat = UberLinks.formatCoord(s.preset.lat)
        presetLng = UberLinks.formatCoord(s.preset.lng)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Pickup", style = MaterialTheme.typography.titleMedium)
            PickupMode.entries.forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = s?.pickupMode == mode,
                            onClick = { viewModel.setMode(mode) },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = s?.pickupMode == mode,
                        onClick = { viewModel.setMode(mode) },
                    )
                    Column {
                        Text(
                            when (mode) {
                                PickupMode.CURRENT_LOCATION -> "Use my current location"
                                PickupMode.PRESET -> "Use a saved preset"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        if (mode == PickupMode.CURRENT_LOCATION) {
                            Text(
                                "Pickup params are omitted — Uber uses the rider's live location.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            if (s?.pickupMode == PickupMode.CURRENT_LOCATION) {
                Button(onClick = {
                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
                        scope.launch {
                            try {
                                val client = LocationServices.getFusedLocationProviderClient(context)
                                val loc = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                                detected = loc
                                locationMsg = if (loc != null) {
                                    "Detected: ${UberLinks.formatCoord(loc.latitude)}, ${UberLinks.formatCoord(loc.longitude)}"
                                } else {
                                    "Location unavailable — try again outdoors."
                                }
                            } catch (e: Exception) {
                                locationMsg = "Couldn't get location: ${e.message}"
                            }
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                    }
                }) {
                    Text("Detect my location")
                }
                if (locationMsg != null) {
                    Text(locationMsg!!, style = MaterialTheme.typography.bodySmall)
                }
            }

            if (s?.pickupMode == PickupMode.PRESET) {
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it },
                    label = { Text("Preset name (e.g. Warehouse)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = presetLat,
                        onValueChange = { presetLat = it },
                        label = { Text("Lat") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = presetLng,
                        onValueChange = { presetLng = it },
                        label = { Text("Lng") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                    )
                }
                if (presetError != null) {
                    Text(presetError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Button(
                    onClick = {
                        val lat = presetLat.toDoubleOrNull()
                        val lng = presetLng.toDoubleOrNull()
                        presetError = when {
                            presetName.isBlank() -> "Give the preset a name."
                            lat == null || lng == null -> "Enter valid numbers."
                            lat !in -90.0..90.0 || lng !in -180.0..180.0 -> "Out of range."
                            else -> null
                        }
                        if (presetError == null) viewModel.savePreset(presetName.trim(), lat!!, lng!!)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save preset")
                }
            }
        }
    }
}
