package dev.vasilyespana.maps2uber.core.settings

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

/** A named saved pickup location. */
data class PickupPreset(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val lat: Double,
    val lng: Double,
)

/**
 * Pure, JVM-testable logic for the multi-preset feature:
 * JSON persistence, locale-tolerant coordinate parsing, validation,
 * and one-time migration from the v1.0 single-preset keys.
 */
object PresetCodec {
    private val gson = Gson()
    private val listType = object : TypeToken<List<PickupPreset>>() {}.type

    fun encode(presets: List<PickupPreset>): String = gson.toJson(presets)

    fun decode(json: String): List<PickupPreset> {
        if (json.isBlank()) return emptyList()
        return runCatching { gson.fromJson<List<PickupPreset>>(json, listType) ?: emptyList() }
            .getOrDefault(emptyList())
    }

    /**
     * Parses a coordinate typed by the user. Accepts both "18.123" and
     * "18,123" because Spanish-locale keyboards emit a comma decimal
     * separator — previously such input silently failed validation and
     * the Save button appeared to do nothing.
     */
    fun parseCoordinate(raw: String): Double? {
        val normalized = raw.trim().replace(',', '.')
        // Reject malformed input like "18.12.34" that toDoubleOrNull already rejects,
        // plus anything with more than one decimal separator after normalization.
        if (normalized.count { it == '.' } > 1) return null
        return normalized.toDoubleOrNull()
    }

    /** Returns an error message, or null when the preset is valid. */
    fun validatePreset(name: String, lat: Double?, lng: Double?): String? = when {
        name.isBlank() -> "Give the preset a name."
        lat == null || lng == null -> "Enter valid numbers for latitude and longitude."
        lat !in -90.0..90.0 || lng !in -180.0..180.0 -> "Coordinates out of range."
        else -> null
    }

    /** One-time migration from the v1.0 single-preset keys. */
    fun migrateLegacy(name: String, lat: Double, lng: Double): List<PickupPreset> {
        if (name.isBlank()) return emptyList()
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return emptyList()
        if (lat == 0.0 && lng == 0.0) return emptyList()
        return listOf(PickupPreset(name = name.trim(), lat = lat, lng = lng))
    }
}
