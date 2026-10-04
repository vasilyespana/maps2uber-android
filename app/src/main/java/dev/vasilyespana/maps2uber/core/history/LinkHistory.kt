package dev.vasilyespana.maps2uber.core.history

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

enum class HistoryKind { LINK, COORDS }

/**
 * One entry in the "recent" list on the home screen.
 * - LINK: [input] is the Google Maps URL the user pasted.
 * - COORDS: [input] is "lat,lng" from the manual coordinate form.
 */
data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val kind: HistoryKind,
    val input: String,
    val label: String,
    val timestamp: Long = System.currentTimeMillis(),
)

/**
 * Pure, JVM-testable history logic: most-recent-first, de-duplicated by
 * (kind, input), capped at [MAX_ENTRIES].
 */
object LinkHistory {
    const val MAX_ENTRIES = 5

    private val gson = Gson()
    private val listType = object : TypeToken<List<HistoryEntry>>() {}.type

    fun add(current: List<HistoryEntry>, entry: HistoryEntry): List<HistoryEntry> {
        val deduped = current.filterNot { it.kind == entry.kind && it.input == entry.input }
        return (listOf(entry) + deduped).take(MAX_ENTRIES)
    }

    fun encode(entries: List<HistoryEntry>): String = gson.toJson(entries)

    fun decode(json: String): List<HistoryEntry> {
        if (json.isBlank()) return emptyList()
        return runCatching { gson.fromJson<List<HistoryEntry>>(json, listType) ?: emptyList() }
            .getOrDefault(emptyList())
            .take(MAX_ENTRIES)
    }
}
