package dev.vasilyespana.maps2uber.core.rides

import dev.vasilyespana.maps2uber.core.geo.UberLinks

/** Simple lat/lng pair used for ride-provider deep link building. */
data class RideLatLng(val lat: Double, val lng: Double)

/**
 * A ride-hailing provider.
 *
 * @param playStorePackage Play Store package for install checks + store
 *   fallback. Null when unknown.
 * @param appStoreId App Store numeric ID (iOS metadata; unused on Android).
 *   Null when unknown.
 * @param deepLinkSupported False for providers without destination deep
 *   linking — rendered grayed-out in settings.
 * @param buildDeepLink Builds the native deep-link URI, or null when the
 *   provider has no verified destination deep link (the app itself is opened
 *   via launch intent when installed instead).
 * @param buildUniversalUrl Builds the universal web fallback URL, or null
 *   when the provider publishes none.
 */
data class RideProvider(
    val id: String,
    val name: String,
    val region: String,
    val playStorePackage: String?,
    val appStoreId: String?,
    val deepLinkSupported: Boolean,
    val buildDeepLink: (origin: RideLatLng?, dest: RideLatLng, destName: String) -> String?,
    val buildUniversalUrl: (dest: RideLatLng) -> String?,
)

/**
 * The ride provider catalog, in display order.
 *
 * URI templates follow ticket st_8abb4562. Play Store packages for Gett
 * (com.gettaxi.android) and DiDi (com.didiglobal.passenger) were verified
 * against public APK listings; App Store IDs against apps.apple.com.
 * Gett's `gett://` scheme is confirmed queryable (react-native-map-link);
 * its destination params are not publicly documented, so it opens the app.
 * DiDi publishes no verifiable destination deep link — the app is opened
 * via launch intent when installed.
 */
object RideProviders {
    const val DEFAULT_ID = "uber"

    private val uber = RideProvider(
        id = "uber",
        name = "Uber",
        region = "Global",
        playStorePackage = "com.ubercab",
        appStoreId = "368677349",
        deepLinkSupported = true,
        buildDeepLink = { origin, dest, destName ->
            buildString {
                append("uber://?action=setPickup")
                if (origin != null) {
                    append("&pickup[latitude]=").append(UberLinks.formatCoord(origin.lat))
                    append("&pickup[longitude]=").append(UberLinks.formatCoord(origin.lng))
                }
                append("&dropoff[latitude]=").append(UberLinks.formatCoord(dest.lat))
                append("&dropoff[longitude]=").append(UberLinks.formatCoord(dest.lng))
                append("&dropoff[nickname]=").append(UberLinks.encodeUriComponent(destName))
            }
        },
        buildUniversalUrl = { dest ->
            "https://m.uber.com/ul/?action=setPickup" +
                "&dropoff[latitude]=${UberLinks.formatCoord(dest.lat)}" +
                "&dropoff[longitude]=${UberLinks.formatCoord(dest.lng)}"
        },
    )

    private val lyft = RideProvider(
        id = "lyft",
        name = "Lyft",
        region = "US & Canada",
        playStorePackage = "me.lyft.android",
        appStoreId = "529379082",
        deepLinkSupported = true,
        buildDeepLink = { _, dest, _ ->
            "lyft://ridetype?id=lyft" +
                "&destination[latitude]=${UberLinks.formatCoord(dest.lat)}" +
                "&destination[longitude]=${UberLinks.formatCoord(dest.lng)}"
        },
        buildUniversalUrl = { dest ->
            "https://lyft.com/ride?id=lyft" +
                "&destination[latitude]=${UberLinks.formatCoord(dest.lat)}" +
                "&destination[longitude]=${UberLinks.formatCoord(dest.lng)}"
        },
    )

    private val bolt = RideProvider(
        id = "bolt",
        name = "Bolt",
        region = "Europe & Africa",
        playStorePackage = "ee.mtakso.client",
        appStoreId = "675033630",
        deepLinkSupported = true,
        buildDeepLink = { _, dest, _ ->
            "bolt://ride" +
                "?destination_lat=${UberLinks.formatCoord(dest.lat)}" +
                "&destination_lng=${UberLinks.formatCoord(dest.lng)}"
        },
        buildUniversalUrl = { _ -> null },
    )

    private val freeNow = RideProvider(
        id = "freenow",
        name = "FreeNow",
        region = "Europe",
        playStorePackage = "mobi.myTaxi.android.user",
        appStoreId = "357852748",
        deepLinkSupported = true,
        buildDeepLink = { _, dest, _ ->
            "freenow://ride" +
                "?destination_latitude=${UberLinks.formatCoord(dest.lat)}" +
                "&destination_longitude=${UberLinks.formatCoord(dest.lng)}"
        },
        buildUniversalUrl = { _ -> null },
    )

    private val grab = RideProvider(
        id = "grab",
        name = "Grab",
        region = "Southeast Asia",
        playStorePackage = "com.grabtaxi.passenger",
        appStoreId = "647268330",
        deepLinkSupported = true,
        buildDeepLink = { _, dest, _ ->
            "grab://open?screenType=BOOKING" +
                "&dropoff[latitude]=${UberLinks.formatCoord(dest.lat)}" +
                "&dropoff[longitude]=${UberLinks.formatCoord(dest.lng)}"
        },
        buildUniversalUrl = { _ -> null },
    )

    private val gett = RideProvider(
        id = "gett",
        name = "Gett",
        region = "UK / Israel",
        playStorePackage = "com.gettaxi.android",
        appStoreId = "449655162",
        deepLinkSupported = true,
        buildDeepLink = { _, _, _ ->
            // Verified scheme; destination params are not publicly
            // documented, so this opens the Gett app.
            "gett://"
        },
        buildUniversalUrl = { _ -> null },
    )

    private val didi = RideProvider(
        id = "didi",
        name = "DiDi",
        region = "Global / LATAM",
        playStorePackage = "com.didiglobal.passenger",
        appStoreId = "1362398401",
        deepLinkSupported = true,
        // No verifiable destination deep link: the launcher opens the app
        // via launch intent when installed.
        buildDeepLink = { _, _, _ -> null },
        buildUniversalUrl = { _ -> null },
    )

    private fun unsupported(id: String, name: String, region: String) = RideProvider(
        id = id,
        name = name,
        region = region,
        playStorePackage = null,
        appStoreId = null,
        deepLinkSupported = false,
        buildDeepLink = { _, _, _ -> null },
        buildUniversalUrl = { _ -> null },
    )

    /** Full catalog in display order. */
    val all: List<RideProvider> = listOf(
        uber,
        lyft,
        bolt,
        freeNow,
        grab,
        gett,
        didi,
        unsupported("waymo", "Waymo", "US"),
        unsupported("zoox", "Zoox Autonomous", "US"),
        unsupported("apollo-go", "Baidu Apollo Go", "China"),
    )

    /** Selectable providers (deep link supported), in display order. */
    val selectable: List<RideProvider> get() = all.filter { it.deepLinkSupported }

    /** Grayed-out providers (deep linking not supported), in display order. */
    val unsupported: List<RideProvider> get() = all.filterNot { it.deepLinkSupported }

    val default: RideProvider get() = byId(DEFAULT_ID)!!

    fun byId(id: String): RideProvider? = all.find { it.id == id }
}
