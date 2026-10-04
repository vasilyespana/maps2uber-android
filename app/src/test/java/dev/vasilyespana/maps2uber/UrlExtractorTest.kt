package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.geo.UrlExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UrlExtractorTest {

    @Test
    fun firstUrl_shortLinkBuriedInText() {
        val text = "Mira este punto 👀 https://maps.app.goo.gl/aBcDeFgHiJk te va a encantar"
        assertEquals("https://maps.app.goo.gl/aBcDeFgHiJk", UrlExtractor.firstUrl(text))
    }

    @Test
    fun firstUrl_fullMapsUrl() {
        val text = "https://www.google.com/maps?q=18.4665,-66.1183"
        assertEquals(text, UrlExtractor.firstUrl(text))
    }

    @Test
    fun firstUrl_noUrl_returnsNull() {
        assertNull(UrlExtractor.firstUrl("No hay ningún enlace aquí, solo texto"))
        assertNull(UrlExtractor.firstUrl(""))
    }

    @Test
    fun firstUrl_takesFirst_whenSeveral() {
        val text = "https://example.com/x then https://maps.app.goo.gl/second"
        assertEquals("https://example.com/x", UrlExtractor.firstUrl(text))
    }

    @Test
    fun firstUrl_stripsTrailingPunctuation() {
        assertEquals(
            "https://maps.app.goo.gl/aBcDeF",
            UrlExtractor.firstUrl("Pin: https://maps.app.goo.gl/aBcDeF."),
        )
        assertEquals(
            "https://maps.app.goo.gl/aBcDeF",
            UrlExtractor.firstUrl("(https://maps.app.goo.gl/aBcDeF)"),
        )
    }

    @Test
    fun firstUrl_httpAlsoMatches() {
        assertEquals(
            "http://goo.gl/maps/xyz",
            UrlExtractor.firstUrl("old link http://goo.gl/maps/xyz ok"),
        )
    }
}
