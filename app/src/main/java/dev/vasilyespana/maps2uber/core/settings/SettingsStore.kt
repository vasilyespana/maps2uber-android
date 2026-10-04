package dev.vasilyespana.maps2uber.core.settings

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "maps2uber_settings")

/** Pickup mode for the Uber links: rider's live location, or a saved preset. */
enum class PickupMode { CURRENT_LOCATION, PRESET }

data class AppSettings(
    val pickupMode: PickupMode = PickupMode.CURRENT_LOCATION,
    val presets: List<PickupPreset> = emptyList(),
    val activePresetId: String? = null,
) {
    /** The preset currently used for pickup params, if any. */
    val activePreset: PickupPreset?
        get() = presets.find { it.id == activePresetId } ?: presets.firstOrNull()
}

@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val PICKUP_MODE = stringPreferencesKey("pickup_mode")
        val PRESETS_JSON = stringPreferencesKey("presets_json")
        val ACTIVE_PRESET_ID = stringPreferencesKey("active_preset_id")

        // v1.0 legacy single-preset keys (migrated on read, cleared on write).
        // Types must match v1.0 exactly or the read misses.
        val LEGACY_NAME = stringPreferencesKey("preset_name")
        val LEGACY_LAT = doublePreferencesKey("preset_lat")
        val LEGACY_LNG = doublePreferencesKey("preset_lng")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        val presets = readPresets(p)
        val activeId = p[Keys.ACTIVE_PRESET_ID]?.takeIf { id -> presets.any { it.id == id } }
            ?: presets.firstOrNull()?.id
        AppSettings(
            pickupMode = runCatching {
                PickupMode.valueOf(p[Keys.PICKUP_MODE] ?: PickupMode.CURRENT_LOCATION.name)
            }.getOrDefault(PickupMode.CURRENT_LOCATION),
            presets = presets,
            activePresetId = activeId,
        )
    }

    suspend fun setPickupMode(mode: PickupMode) {
        context.dataStore.edit { it[Keys.PICKUP_MODE] = mode.name }
    }

    /** Adds a preset; the first preset becomes active. */
    suspend fun addPreset(preset: PickupPreset) {
        context.dataStore.edit { p ->
            val current = readPresets(p)
            val updated = current + preset
            writePresets(p, updated)
            if (current.isEmpty()) p[Keys.ACTIVE_PRESET_ID] = preset.id
        }
    }

    suspend fun deletePreset(id: String) {
        context.dataStore.edit { p ->
            val updated = readPresets(p).filterNot { it.id == id }
            writePresets(p, updated)
            if (p[Keys.ACTIVE_PRESET_ID] == id) {
                val next = updated.firstOrNull()?.id
                if (next != null) p[Keys.ACTIVE_PRESET_ID] = next else p.remove(Keys.ACTIVE_PRESET_ID)
            }
        }
    }

    suspend fun setActivePreset(id: String) {
        context.dataStore.edit { p ->
            if (readPresets(p).any { it.id == id }) p[Keys.ACTIVE_PRESET_ID] = id
        }
    }

    // ---- internal helpers (operate on the preferences inside edit) ----

    private fun readPresets(p: androidx.datastore.preferences.core.Preferences): List<PickupPreset> {
        val stored = PresetCodec.decode(p[Keys.PRESETS_JSON].orEmpty())
        if (stored.isNotEmpty()) return stored
        // One-time lazy migration from the v1.0 single preset.
        return PresetCodec.migrateLegacy(
            name = p[Keys.LEGACY_NAME].orEmpty(),
            lat = p[Keys.LEGACY_LAT] ?: 0.0,
            lng = p[Keys.LEGACY_LNG] ?: 0.0,
        )
    }

    private fun writePresets(p: MutablePreferences, presets: List<PickupPreset>) {
        p[Keys.PRESETS_JSON] = PresetCodec.encode(presets)
        // Clear legacy keys so they can never shadow the new list.
        p.remove(Keys.LEGACY_NAME)
        p.remove(Keys.LEGACY_LAT)
        p.remove(Keys.LEGACY_LNG)
    }
}
