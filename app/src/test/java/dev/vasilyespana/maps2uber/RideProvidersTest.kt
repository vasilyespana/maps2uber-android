package dev.vasilyespana.maps2uber

import dev.vasilyespana.maps2uber.core.rides.RideLatLng
import dev.vasilyespana.maps2uber.core.rides.RideProviders
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RideProvidersTest {

    @Test
    fun defaultIsUber() {
        assertEquals("uber", RideProviders.DEFAULT_ID)
        assertEquals("uber", RideProviders.default.id)
    }

    @Test
    fun sevenSelectableProvidersInOrder() {
        assertEquals(
            listOf("uber", "lyft", "bolt", "freenow", "grab", "gett", "didi"),
            RideProviders.selectable.map { it.id },
        )
    }

    @Test
    fun threeUnsupportedProvidersGrayedOut() {
        assertEquals(
            listOf("waymo", "zoox", "apollo-go"),
            RideProviders.unsupported.map { it.id },
        )
        assertTrue(RideProviders.unsupported.all { !it.deepLinkSupported })
    }

    @Test
    fun uberDeepLinkCarriesOriginAndDestination() {
        val uber = RideProviders.byId("uber")!!
        val url = uber.buildDeepLink(
            RideLatLng(1.0, 2.0),
            RideLatLng(3.0, 4.0),
            "Hotel",
        )!!
        assertTrue(url.startsWith("uber://?action=setPickup"))
        assertTrue(url.contains("pickup[latitude]=1"))
        assertTrue(url.contains("pickup[longitude]=2"))
        assertTrue(url.contains("dropoff[latitude]=3"))
        assertTrue(url.contains("dropoff[longitude]=4"))
        assertTrue(url.contains("dropoff[nickname]=Hotel"))
    }

    @Test
    fun uberDeepLinkOmitsPickupWhenOriginUnknown() {
        val uber = RideProviders.byId("uber")!!
        val url = uber.buildDeepLink(null, RideLatLng(3.0, 4.0), "Hotel")!!
        assertFalse(url.contains("pickup["))
        assertTrue(url.contains("dropoff[latitude]=3"))
    }

    @Test
    fun lyftLinksMatchSpec() {
        val lyft = RideProviders.byId("lyft")!!
        val deep = lyft.buildDeepLink(null, RideLatLng(3.0, 4.0), "Hotel")!!
        assertTrue(deep.startsWith("lyft://ridetype?id=lyft"))
        assertTrue(deep.contains("destination[latitude]=3"))
        val web = lyft.buildUniversalUrl(RideLatLng(3.0, 4.0))!!
        assertTrue(web.startsWith("https://lyft.com/ride?id=lyft"))
    }

    @Test
    fun uberUniversalUrlOmitsPickup() {
        val url = RideProviders.default.buildUniversalUrl(RideLatLng(3.0, 4.0))!!
        assertTrue(url.startsWith("https://m.uber.com/ul/?action=setPickup"))
        assertFalse(url.contains("pickup["))
    }

    @Test
    fun selectableProvidersHavePlayStorePackages() {
        assertTrue(RideProviders.selectable.all { it.playStorePackage != null })
    }

    @Test
    fun unknownIdReturnsNull() {
        assertNull(RideProviders.byId("nope"))
    }

    @Test
    fun destNameIsEncoded() {
        val uber = RideProviders.byId("uber")!!
        val url = uber.buildDeepLink(null, RideLatLng(3.0, 4.0), "Hotel & Spa")!!
        assertTrue(url.contains("dropoff[nickname]=Hotel%20%26%20Spa"))
    }

    @Test
    fun compareAlternativeIsLyftForUber() {
        val uber = RideProviders.byId("uber")!!
        assertEquals("lyft", RideProviders.compareAlternative(uber).id)
    }

    @Test
    fun compareAlternativeIsUberForLyft() {
        val lyft = RideProviders.byId("lyft")!!
        assertEquals("uber", RideProviders.compareAlternative(lyft).id)
    }

    @Test
    fun compareAlternativeIsUberForOtherProviders() {
        val bolt = RideProviders.byId("bolt")!!
        assertEquals("uber", RideProviders.compareAlternative(bolt).id)
    }

    @Test
    fun compareAlternativeIsNeverTheSameProvider() {
        RideProviders.selectable.forEach { provider ->
            val other = RideProviders.compareAlternative(provider)
            assertFalse(
                "compare alternative must differ from ${provider.id}",
                other.id == provider.id,
            )
        }
    }
}
