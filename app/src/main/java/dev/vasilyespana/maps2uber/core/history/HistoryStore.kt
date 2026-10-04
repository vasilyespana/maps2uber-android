package dev.vasilyespana.maps2uber.core.history

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.historyDataStore by preferencesDataStore(name = "maps2uber_history")

/** Thin DataStore wrapper; ordering/dedup/cap live in [LinkHistory] (unit-tested). */
@Singleton
class HistoryStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ENTRIES_JSON = stringPreferencesKey("entries_json")
    }

    val history: Flow<List<HistoryEntry>> =
        context.historyDataStore.data.map { LinkHistory.decode(it[Keys.ENTRIES_JSON].orEmpty()) }

    suspend fun record(entry: HistoryEntry) {
        context.historyDataStore.edit { p ->
            val current = LinkHistory.decode(p[Keys.ENTRIES_JSON].orEmpty())
            p[Keys.ENTRIES_JSON] = LinkHistory.encode(LinkHistory.add(current, entry))
        }
    }

    suspend fun clear() {
        context.historyDataStore.edit { p -> p.remove(Keys.ENTRIES_JSON) }
    }
}
