package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.network.ResolveParser
import dev.vasilyespana.maps2uber.core.network.ResolveResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResolveParsingTest {

    @Test
    fun parse_okTrue() {
        val json = """{"ok":true,"lat":18.4665,"lng":-66.1183,"name":"Calle del Cristo, La Perla, Viejo San Juan","address":"Calle del Cristo, La Perla, Viejo San Juan","final_url":"https://www.google.com/sorry/index?continue=x","geocoded":false}"""
        val r = ResolveParser.parse(json)
        assertTrue(r is ResolveResult.Ok)
        r as ResolveResult.Ok
        assertEquals(18.4665, r.lat, 1e-9)
        assertEquals(-66.1183, r.lng, 1e-9)
        assertEquals("Calle del Cristo, La Perla, Viejo San Juan", r.name)
        assertEquals("Calle del Cristo, La Perla, Viejo San Juan", r.address)
    }

    @Test
    fun parse_okFalse_carriesError() {
        val json = """{"ok":false,"error":"unresolvable","name":"could not resolve link"}"""
        val r = ResolveParser.parse(json)
        assertTrue(r is ResolveResult.Err)
        r as ResolveResult.Err
        assertEquals("unresolvable", r.error)
    }

    @Test
    fun parse_okFalse_missingCoords_isErr() {
        // Defensive: ok:true without coordinates must not produce an Ok.
        val json = """{"ok":true,"name":"nowhere","address":"nowhere"}"""
        val r = ResolveParser.parse(json)
        assertTrue(r is ResolveResult.Err)
    }

    @Test
    fun parse_garbage_isErr() {
        val r = ResolveParser.parse("not json at all {{{")
        assertTrue(r is ResolveResult.Err)
    }
}
