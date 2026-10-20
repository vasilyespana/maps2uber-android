import Foundation

/// Simple lat/lng pair used for ride-provider deep link building.
struct RideLatLng: Equatable {
    let lat: Double
    let lng: Double
}

/// A ride-hailing provider — Swift port of Android's `RideProvider`
/// catalog (maps2uber-android
/// `app/src/main/java/dev/vasilyespana/maps2uber/core/rides/RideProviders.kt`).
///
/// Deep-link and universal-URL templates are byte-identical to Android
/// v1.5.0 so a destination opens the same way on both platforms.
struct RideProvider: Identifiable, Equatable {
    let id: String
    let name: String
    let region: String
    /// App Store numeric ID, used for the store fallback. Nil when unknown.
    let appStoreId: String?
    /// False for providers without destination deep linking — rendered
    /// as "coming soon" in settings.
    let deepLinkSupported: Bool
    /// Builds the native deep-link URI, or nil when the provider has no
    /// verified destination deep link.
    let buildDeepLink: (_ origin: RideLatLng?, _ dest: RideLatLng, _ destName: String) -> String?
    /// Builds the universal web fallback URL, or nil when the provider
    /// publishes none.
    let buildUniversalUrl: (_ dest: RideLatLng) -> String?

    static func == (lhs: RideProvider, rhs: RideProvider) -> Bool { lhs.id == rhs.id }
}

/// The ride provider catalog, in display order (mirrors Android).
enum RideProviders {
    static let defaultId = "uber"

    // MARK: - Formatting helpers (ports of Android's UberLinks)

    /// Like JavaScript `Number.toString` for typical coordinate
    /// magnitudes (port of `UberLinks.formatCoord`).
    static func formatCoord(_ d: Double) -> String {
        if d == 0 { return "0" }
        if d.rounded() == d, abs(d) < 1e15 { return String(Int(d)) }
        var s = String(format: "%.12f", d)
        while s.hasSuffix("0") { s.removeLast() }
        if s.hasSuffix(".") { s.removeLast() }
        return s
    }

    /// JavaScript `encodeURIComponent` semantics (UTF-8)
    /// (port of `UberLinks.encodeUriComponent`).
    static func encodeUriComponent(_ s: String) -> String {
        var out = ""
        out.reserveCapacity(s.count)
        for b in s.utf8 {
            let unreserved = (0x61...0x7A).contains(b) || (0x41...0x5A).contains(b)
                || (0x30...0x39).contains(b)
                || b == 0x2D || b == 0x5F || b == 0x2E || b == 0x21 // - _ . !
                || b == 0x7E || b == 0x2A || b == 0x27 || b == 0x28 || b == 0x29 // ~ * ' ( )
            if unreserved {
                out.append(Character(UnicodeScalar(b)))
            } else {
                out += String(format: "%%%02X", b)
            }
        }
        return out
    }

    // MARK: - Catalog

    private static let uber = RideProvider(
        id: "uber", name: "Uber", region: "Global",
        appStoreId: "368677349", deepLinkSupported: true,
        buildDeepLink: { origin, dest, destName in
            var s = "uber://?action=setPickup"
            if let origin {
                s += "&pickup[latitude]=\(formatCoord(origin.lat))"
                s += "&pickup[longitude]=\(formatCoord(origin.lng))"
            }
            s += "&dropoff[latitude]=\(formatCoord(dest.lat))"
            s += "&dropoff[longitude]=\(formatCoord(dest.lng))"
            s += "&dropoff[nickname]=\(encodeUriComponent(destName))"
            return s
        },
        buildUniversalUrl: { dest in
            "https://m.uber.com/ul/?action=setPickup"
                + "&dropoff[latitude]=\(formatCoord(dest.lat))"
                + "&dropoff[longitude]=\(formatCoord(dest.lng))"
        })

    private static let lyft = RideProvider(
        id: "lyft", name: "Lyft", region: "US & Canada",
        appStoreId: "529379082", deepLinkSupported: true,
        buildDeepLink: { _, dest, _ in
            "lyft://ridetype?id=lyft"
                + "&destination[latitude]=\(formatCoord(dest.lat))"
                + "&destination[longitude]=\(formatCoord(dest.lng))"
        },
        buildUniversalUrl: { dest in
            "https://lyft.com/ride?id=lyft"
                + "&destination[latitude]=\(formatCoord(dest.lat))"
                + "&destination[longitude]=\(formatCoord(dest.lng))"
        })

    private static let bolt = RideProvider(
        id: "bolt", name: "Bolt", region: "Europe & Africa",
        appStoreId: "675033630", deepLinkSupported: true,
        buildDeepLink: { _, dest, _ in
            "bolt://ride"
                + "?destination_lat=\(formatCoord(dest.lat))"
                + "&destination_lng=\(formatCoord(dest.lng))"
        },
        buildUniversalUrl: { _ in nil })

    private static let freeNow = RideProvider(
        id: "freenow", name: "FreeNow", region: "Europe",
        appStoreId: "357852748", deepLinkSupported: true,
        buildDeepLink: { _, dest, _ in
            "freenow://ride"
                + "?destination_latitude=\(formatCoord(dest.lat))"
                + "&destination_longitude=\(formatCoord(dest.lng))"
        },
        buildUniversalUrl: { _ in nil })

    private static let grab = RideProvider(
        id: "grab", name: "Grab", region: "Southeast Asia",
        appStoreId: "647268330", deepLinkSupported: true,
        buildDeepLink: { _, dest, _ in
            "grab://open?screenType=BOOKING"
                + "&dropoff[latitude]=\(formatCoord(dest.lat))"
                + "&dropoff[longitude]=\(formatCoord(dest.lng))"
        },
        buildUniversalUrl: { _ in nil })

    private static let gett = RideProvider(
        id: "gett", name: "Gett", region: "UK / Israel",
        appStoreId: "449655162", deepLinkSupported: true,
        buildDeepLink: { _, _, _ in
            // Verified scheme; destination params are not publicly
            // documented, so this opens the Gett app.
            "gett://"
        },
        buildUniversalUrl: { _ in nil })

    private static let didi = RideProvider(
        id: "didi", name: "DiDi", region: "Global / LATAM",
        appStoreId: "1362398401", deepLinkSupported: true,
        // No verifiable destination deep link: the launcher falls back
        // to the provider's App Store page.
        buildDeepLink: { _, _, _ in nil },
        buildUniversalUrl: { _ in nil })

    private static func unsupported(_ id: String, _ name: String, _ region: String) -> RideProvider {
        RideProvider(
            id: id, name: name, region: region,
            appStoreId: nil, deepLinkSupported: false,
            buildDeepLink: { _, _, _ in nil },
            buildUniversalUrl: { _ in nil })
    }

    /// Full catalog in display order.
    static let all: [RideProvider] = [
        uber, lyft, bolt, freeNow, grab, gett, didi,
        unsupported("waymo", "Waymo", "US"),
        unsupported("zoox", "Zoox Autonomous", "US"),
        unsupported("apollo-go", "Baidu Apollo Go", "China"),
    ]

    /// Selectable providers (deep link supported), in display order.
    static var selectable: [RideProvider] { all.filter { $0.deepLinkSupported } }

    /// Grayed-out providers (deep linking not supported), in display order.
    static var unsupported: [RideProvider] { all.filter { !$0.deepLinkSupported } }

    static var `default`: RideProvider { byId(defaultId)! }

    static func byId(_ id: String) -> RideProvider? { all.first { $0.id == id } }
}
