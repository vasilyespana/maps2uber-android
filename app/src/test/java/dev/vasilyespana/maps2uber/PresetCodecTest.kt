package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.settings.PickupPreset
import dev.vasilyespana.maps2uber.core.settings.PresetCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import kotlin.math.abs

class PresetCodecTest {

    @Test
    fun `encode then decode round-trips a preset list`() {
        val presets = listOf(
            PickupPreset(id = "a1", name = "Warehouse", lat = 18.123456, lng = -66.654321),
            PickupPreset(id = "b2", name = "Casa \"El\" Vigía", lat = 18.0, lng = -66.0),
        )
        val decoded = PresetCodec.decode(PresetCodec.encode(presets))
        assertEquals(presets, decoded)
    }

    @Test
    fun `decode of empty or blank returns empty list`() {
        assertEquals(emptyList<PickupPreset>(), PresetCodec.decode(""))
        assertEquals(emptyList<PickupPreset>(), PresetCodec.decode("   "))
    }

    @Test
    fun `decode of garbage returns empty list instead of throwing`() {
        assertEquals(emptyList<PickupPreset>(), PresetCodec.decode("not json {{{"))
    }

    @Test
    fun `parseCoordinate accepts dot decimals`() {
        assertEquals(18.123456, PresetCodec.parseCoordinate("18.123456")!!, 1e-9)
    }

    @Test
    fun `parseCoordinate accepts comma decimals (Spanish locale keyboards)`() {
        assertEquals(18.123456, PresetCodec.parseCoordinate("18,123456")!!, 1e-9)
        assertEquals(-66.5, PresetCodec.parseCoordinate("-66,5")!!, 1e-9)
    }

    @Test
    fun `parseCoordinate trims whitespace`() {
        assertEquals(18.5, PresetCodec.parseCoordinate("  18.5  ")!!, 1e-9)
    }

    @Test
    fun `parseCoordinate rejects garbage`() {
        assertNull(PresetCodec.parseCoordinate(""))
        assertNull(PresetCodec.parseCoordinate("abc"))
        assertNull(PresetCodec.parseCoordinate("18.12.34"))
    }

    @Test
    fun `validatePreset rejects blank name`() {
        val err = PresetCodec.validatePreset("", 18.0, -66.0)
        assertEquals("Give the preset a name.", err)
    }

    @Test
    fun `validatePreset rejects unparseable coordinates`() {
        val err = PresetCodec.validatePreset("Casa", null, -66.0)
        assertEquals("Enter valid numbers for latitude and longitude.", err)
    }

    @Test
    fun `validatePreset rejects out-of-range coordinates`() {
        val err = PresetCodec.validatePreset("Casa", 91.0, -66.0)
        assertEquals("Coordinates out of range.", err)
        val err2 = PresetCodec.validatePreset("Casa", 18.0, -181.0)
        assertEquals("Coordinates out of range.", err2)
    }

    @Test
    fun `validatePreset accepts a good preset`() {
        assertNull(PresetCodec.validatePreset("Casa", 18.123, -66.456))
    }

    @Test
    fun `migrateLegacy builds a preset list from old single-preset keys`() {
        val migrated = PresetCodec.migrateLegacy(name = "Warehouse", lat = 18.1, lng = -66.4)
        assertEquals(1, migrated.size)
        assertEquals("Warehouse", migrated[0].name)
        assertEquals(18.1, migrated[0].lat, 1e-9)
        assertEquals(-66.4, migrated[0].lng, 1e-9)
    }

    @Test
    fun `migrateLegacy returns empty list when no legacy preset was saved`() {
        assertEquals(emptyList<PickupPreset>(), PresetCodec.migrateLegacy(name = "", lat = 0.0, lng = 0.0))
        assertEquals(emptyList<PickupPreset>(), PresetCodec.migrateLegacy(name = "  ", lat = 18.1, lng = -66.4))
    }

    private fun assertEquals(expected: Double, actual: Double, delta: Double) {
        assert(abs(expected - actual) <= delta) { "expected $expected but was $actual" }
    }
}
