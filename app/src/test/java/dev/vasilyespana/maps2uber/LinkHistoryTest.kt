package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.history.HistoryEntry
import dev.vasilyespana.maps2uber.core.history.HistoryKind
import dev.vasilyespana.maps2uber.core.history.LinkHistory
import org.junit.Assert.assertEquals
import org.junit.Test

class LinkHistoryTest {

    private fun entry(input: String, kind: HistoryKind = HistoryKind.LINK) =
        HistoryEntry(kind = kind, input = input, label = "label-$input")

    @Test
    fun `add to empty list`() {
        val result = LinkHistory.add(emptyList(), entry("https://a"))
        assertEquals(listOf("https://a"), result.map { it.input })
    }

    @Test
    fun `newest goes first`() {
        val result = LinkHistory.add(listOf(entry("https://a")), entry("https://b"))
        assertEquals(listOf("https://b", "https://a"), result.map { it.input })
    }

    @Test
    fun `re-adding an existing link moves it to the front without duplicating`() {
        val start = listOf(entry("https://a"), entry("https://b"), entry("https://c"))
        val result = LinkHistory.add(start, entry("https://b"))
        assertEquals(listOf("https://b", "https://a", "https://c"), result.map { it.input })
    }

    @Test
    fun `list is capped at five entries`() {
        var list = emptyList<HistoryEntry>()
        repeat(7) { i -> list = LinkHistory.add(list, entry("https://$i")) }
        assertEquals(5, list.size)
        assertEquals(
            listOf("https://6", "https://5", "https://4", "https://3", "https://2"),
            list.map { it.input },
        )
    }

    @Test
    fun `link and coords with different inputs do not dedupe each other`() {
        val start = listOf(entry("18.1,-66.1", HistoryKind.COORDS))
        val result = LinkHistory.add(start, entry("18.1,-66.1", HistoryKind.LINK))
        assertEquals(2, result.size)
    }

    @Test
    fun `encode then decode round-trips`() {
        val entries = listOf(entry("https://a"), entry("18.1,-66.1", HistoryKind.COORDS))
        val decoded = LinkHistory.decode(LinkHistory.encode(entries))
        assertEquals(entries.map { it.input }, decoded.map { it.input })
        assertEquals(entries.map { it.kind }, decoded.map { it.kind })
        assertEquals(entries.map { it.label }, decoded.map { it.label })
    }

    @Test
    fun `decode of blank or garbage returns empty list`() {
        assertEquals(emptyList<HistoryEntry>(), LinkHistory.decode(""))
        assertEquals(emptyList<HistoryEntry>(), LinkHistory.decode("nope{{{"))
    }
}
