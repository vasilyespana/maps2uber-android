package dev.vasilyespana.maps2uber.core.settings

import android.content.Context
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

data class PickupPreset(val name: String, val lat: Double, val lng: Double)

data class AppSettings(
    val pickupMode: PickupMode = PickupMode.CURRENT_LOCATION,
    val preset: PickupPreset = PickupPreset("", 0.0, 0.0),
)

@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val PICKUP_MODE = stringPreferencesKey("pickup_mode")
        val PRESET_NAME = stringPreferencesKey("preset_name")
        val PRESET_LAT = doublePreferencesKey("preset_lat")
        val PRESET_LNG = doublePreferencesKey("preset_lng")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { p ->
        AppSettings(
            pickupMode = runCatching {
                PickupMode.valueOf(p[Keys.PICKUP_MODE] ?: PickupMode.CURRENT_LOCATION.name)
            }.getOrDefault(PickupMode.CURRENT_LOCATION),
            preset = PickupPreset(
                name = p[Keys.PRESET_NAME].orEmpty(),
                lat = p[Keys.PRESET_LAT] ?: 0.0,
                lng = p[Keys.PRESET_LNG] ?: 0.0,
            ),
        )
    }

    suspend fun setPickupMode(mode: PickupMode) {
        context.dataStore.edit { it[Keys.PICKUP_MODE] = mode.name }
    }

    suspend fun setPreset(preset: PickupPreset) {
        context.dataStore.edit {
            it[Keys.PRESET_NAME] = preset.name
            it[Keys.PRESET_LAT] = preset.lat
            it[Keys.PRESET_LNG] = preset.lng
        }
    }
}
