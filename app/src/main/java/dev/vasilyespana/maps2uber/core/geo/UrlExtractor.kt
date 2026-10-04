package dev.vasilyespana.maps2uber.core.geo

/** Extracts the first http(s) URL from shared text (share-sheet payloads). */
object UrlExtractor {
    private val URL_RE = Regex("https?://[^\\s<>\"']+")
    private val TRAILING_PUNCT = setOf('.', ',', ';', ':', '!', '?', ')', ']', '}', '\'', '"')

    fun firstUrl(text: String): String? {
        val match = URL_RE.find(text) ?: return null
        return match.value.trimEnd { it in TRAILING_PUNCT }.ifEmpty { null }
    }
}
