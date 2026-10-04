package dev.vasilyespana.maps2uber.ui.settings

import android.Manifest
import android.content.Context
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
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
import dev.vasilyespana.maps2uber.core.settings.PresetCodec
import dev.vasilyespana.maps2uber.core.settings.SettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val store: SettingsStore,
) : ViewModel() {
    val settings = store.settings

    private val _notice = MutableStateFlow<String?>(null)
    /** One-shot confirmation shown after a preset is persisted. */
    val notice: StateFlow<String?> = _notice

    fun setMode(mode: PickupMode) = viewModelScope.launch { store.setPickupMode(mode) }

    fun addPreset(name: String, lat: Double, lng: Double) = viewModelScope.launch {
        store.addPreset(PickupPreset(name = name.trim(), lat = lat, lng = lng))
        // Only shown after the DataStore write completes — the save is real.
        _notice.value = "Preset \"$name\" saved ✓"
    }

    fun deletePreset(id: String) = viewModelScope.launch { store.deletePreset(id) }

    fun setActivePreset(id: String) = viewModelScope.launch { store.setActivePreset(id) }

    fun clearNotice() {
        _notice.value = null
    }
}

/** Requests location permission if needed, then delivers the GPS fix (or null). */
private fun detectLocation(
    context: Context,
    scope: CoroutineScope,
    onPermissionNeeded: () -> Unit,
    onResult: (Location?, String) -> Unit,
) {
    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
        onPermissionNeeded()
        return
    }
    scope.launch {
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val loc = client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
            if (loc != null) {
                onResult(loc, "Detected: ${UberLinks.formatCoord(loc.latitude)}, ${UberLinks.formatCoord(loc.longitude)}")
            } else {
                onResult(null, "Location unavailable — try again outdoors.")
            }
        } catch (e: SecurityException) {
            onResult(null, "Location permission denied.")
        } catch (e: Exception) {
            onResult(null, "Couldn't get location: ${e.message}")
        }
    }
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
    val notice by viewModel.notice.collectAsState()

    var detected by remember { mutableStateOf<Location?>(null) }
    var locationMsg by remember { mutableStateOf<String?>(null) }
    var showSaveDetected by remember { mutableStateOf(false) }
    var detectedName by remember { mutableStateOf("") }

    var presetName by remember { mutableStateOf("") }
    var presetLat by remember { mutableStateOf("") }
    var presetLng by remember { mutableStateOf("") }
    var presetError by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        if (grants.values.any { it }) {
            detectLocation(context, scope, {}, { loc, msg ->
                detected = loc
                locationMsg = msg
            })
        } else {
            locationMsg = "Location permission denied."
        }
    }
    val requestPermission = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    val s = settings

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
            // One-shot save confirmation — the fix for "nothing happens".
            if (notice != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        notice!!,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

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
                        } else if (s != null && s.presets.isNotEmpty()) {
                            val active = s.activePreset
                            Text(
                                if (active != null) {
                                    "Active: ${active.name} " +
                                        "(${UberLinks.formatCoord(active.lat)}, " +
                                        "${UberLinks.formatCoord(active.lng)})"
                                } else "No preset selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            if (s?.pickupMode == PickupMode.CURRENT_LOCATION) {
                Text("Save places you go often", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Detect your location and save it as a preset. As you move around, " +
                        "keep saving — then pick any of them as your Uber pickup.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = {
                        detectLocation(context, scope, requestPermission) { loc, msg ->
                            detected = loc
                            locationMsg = msg
                        }
                    },
                    modifier = Modifier.testTag("detect_location_button"),
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                    Text("  Detect my location")
                }
                if (locationMsg != null) {
                    Text(locationMsg!!, style = MaterialTheme.typography.bodySmall)
                }
                if (detected != null && !showSaveDetected) {
                    OutlinedButton(
                        onClick = {
                            detectedName = "My location " +
                                "${UberLinks.formatCoord(detected!!.latitude)}, " +
                                "${UberLinks.formatCoord(detected!!.longitude)}"
                            showSaveDetected = true
                        },
                        modifier = Modifier.testTag("save_detected_button"),
                    ) {
                        Text("Save this location as a preset")
                    }
                }
                if (showSaveDetected && detected != null) {
                    OutlinedTextField(
                        value = detectedName,
                        onValueChange = { detectedName = it; viewModel.clearNotice() },
                        label = { Text("Preset name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detected_preset_name"),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                val name = detectedName.trim()
                                if (name.isEmpty()) {
                                    locationMsg = "Give the preset a name."
                                } else {
                                    viewModel.addPreset(name, detected!!.latitude, detected!!.longitude)
                                    showSaveDetected = false
                                    detected = null
                                    locationMsg = null
                                }
                            },
                            modifier = Modifier.testTag("confirm_save_detected_button"),
                        ) {
                            Text("Save preset")
                        }
                        OutlinedButton(onClick = { showSaveDetected = false }) {
                            Text("Cancel")
                        }
                    }
                }
            }

            if (s?.pickupMode == PickupMode.PRESET) {
                Text("Your presets", style = MaterialTheme.typography.titleMedium)
                if (s.presets.isEmpty()) {
                    Text(
                        "No presets yet — add your first one below, or detect your " +
                            "location from the other pickup mode and save it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                s.presets.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = s.activePresetId == preset.id,
                                onClick = { viewModel.setActivePreset(preset.id) },
                                role = Role.RadioButton,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = s.activePresetId == preset.id,
                            onClick = { viewModel.setActivePreset(preset.id) },
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(preset.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "${UberLinks.formatCoord(preset.lat)}, ${UberLinks.formatCoord(preset.lng)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { viewModel.deletePreset(preset.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete ${preset.name}")
                        }
                    }
                }

                Text("Add a preset", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = presetName,
                    onValueChange = { presetName = it; viewModel.clearNotice(); presetError = null },
                    label = { Text("Preset name (e.g. Warehouse)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("preset_name_field"),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = presetLat,
                        onValueChange = { presetLat = it; viewModel.clearNotice(); presetError = null },
                        label = { Text("Lat") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_lat_field"),
                    )
                    OutlinedTextField(
                        value = presetLng,
                        onValueChange = { presetLng = it; viewModel.clearNotice(); presetError = null },
                        label = { Text("Lng") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_lng_field"),
                    )
                }
                OutlinedButton(
                    onClick = {
                        detectLocation(context, scope, requestPermission) { loc, msg ->
                            if (loc != null) {
                                presetLat = UberLinks.formatCoord(loc.latitude)
                                presetLng = UberLinks.formatCoord(loc.longitude)
                                locationMsg = null
                            } else {
                                locationMsg = msg
                            }
                        }
                    },
                    modifier = Modifier.testTag("fill_from_location_button"),
                ) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                    Text("  Fill from my location")
                }
                if (locationMsg != null && s.pickupMode == PickupMode.PRESET) {
                    Text(locationMsg!!, style = MaterialTheme.typography.bodySmall)
                }
                if (presetError != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            presetError!!,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Button(
                    onClick = {
                        // Locale-tolerant: "18,12" (Spanish keyboards) parses like "18.12".
                        val lat = PresetCodec.parseCoordinate(presetLat)
                        val lng = PresetCodec.parseCoordinate(presetLng)
                        presetError = PresetCodec.validatePreset(presetName.trim(), lat, lng)
                        if (presetError == null) {
                            viewModel.addPreset(presetName.trim(), lat!!, lng!!)
                            presetName = ""
                            presetLat = ""
                            presetLng = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_preset_button"),
                ) {
                    Text("Save preset")
                }
            }
        }
    }
}
