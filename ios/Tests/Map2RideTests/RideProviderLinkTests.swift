import XCTest
@testable import Map2Ride

/// Provider deep-link / universal-URL templates must stay byte-identical
/// to Android v1.5.0 (RideProviders.kt) — these tests pin the exact
/// strings Android produces for the same inputs.
final class RideProviderLinkTests: XCTestCase {
    private let dest = RideLatLng(lat: 18.3875655, lng: -66.0951019)

    private func provider(_ id: String) -> RideProvider {
        RideProviders.byId(id)!
    }

    // MARK: - Catalog

    func testSelectableProvidersMatchAndroidV150() {
        XCTAssertEqual(
            RideProviders.selectable.map(\.id),
            ["uber", "lyft", "bolt", "freenow", "grab", "gett", "didi"])
        XCTAssertEqual(RideProviders.defaultId, "uber")
        XCTAssertEqual(RideProviders.default.id, "uber")
    }

    func testUnsupportedProvidersMatchAndroid() {
        XCTAssertEqual(
            RideProviders.unsupported.map(\.id),
            ["waymo", "zoox", "apollo-go"])
        for p in RideProviders.unsupported {
            XCTAssertFalse(p.deepLinkSupported)
            XCTAssertNil(p.buildDeepLink(nil, dest, "X"))
            XCTAssertNil(p.buildUniversalUrl(dest))
        }
    }

    // MARK: - Deep links

    func testUberDeepLinkWithoutOrigin() {
        XCTAssertEqual(
            provider("uber").buildDeepLink(nil, dest, "Test Place"),
            "uber://?action=setPickup"
                + "&dropoff[latitude]=18.3875655&dropoff[longitude]=-66.0951019"
                + "&dropoff[nickname]=Test%20Place")
    }

    func testUberDeepLinkWithOrigin() {
        let origin = RideLatLng(lat: 18.0, lng: -66.0)
        XCTAssertEqual(
            provider("uber").buildDeepLink(origin, dest, "Home"),
            "uber://?action=setPickup"
                + "&pickup[latitude]=18&pickup[longitude]=-66"
                + "&dropoff[latitude]=18.3875655&dropoff[longitude]=-66.0951019"
                + "&dropoff[nickname]=Home")
    }

    func testUberUniversalUrl() {
        XCTAssertEqual(
            provider("uber").buildUniversalUrl(dest),
            "https://m.uber.com/ul/?action=setPickup"
                + "&dropoff[latitude]=18.3875655&dropoff[longitude]=-66.0951019")
    }

    func testLyftDeepLinkAndUniversalUrl() {
        XCTAssertEqual(
            provider("lyft").buildDeepLink(nil, dest, "Ignored"),
            "lyft://ridetype?id=lyft"
                + "&destination[latitude]=18.3875655&destination[longitude]=-66.0951019")
        XCTAssertEqual(
            provider("lyft").buildUniversalUrl(dest),
            "https://lyft.com/ride?id=lyft"
                + "&destination[latitude]=18.3875655&destination[longitude]=-66.0951019")
    }

    func testBoltDeepLinkNoUniversal() {
        XCTAssertEqual(
            provider("bolt").buildDeepLink(nil, dest, "Ignored"),
            "bolt://ride?destination_lat=18.3875655&destination_lng=-66.0951019")
        XCTAssertNil(provider("bolt").buildUniversalUrl(dest))
    }

    func testFreeNowDeepLinkNoUniversal() {
        XCTAssertEqual(
            provider("freenow").buildDeepLink(nil, dest, "Ignored"),
            "freenow://ride?destination_latitude=18.3875655&destination_longitude=-66.0951019")
        XCTAssertNil(provider("freenow").buildUniversalUrl(dest))
    }

    func testGrabDeepLinkNoUniversal() {
        XCTAssertEqual(
            provider("grab").buildDeepLink(nil, dest, "Ignored"),
            "grab://open?screenType=BOOKING"
                + "&dropoff[latitude]=18.3875655&dropoff[longitude]=-66.0951019")
        XCTAssertNil(provider("grab").buildUniversalUrl(dest))
    }

    func testGettOpensAppOnly() {
        XCTAssertEqual(provider("gett").buildDeepLink(nil, dest, "Ignored"), "gett://")
        XCTAssertNil(provider("gett").buildUniversalUrl(dest))
    }

    func testDiDiHasNoDeepLink() {
        XCTAssertNil(provider("didi").buildDeepLink(nil, dest, "Ignored"))
        XCTAssertNil(provider("didi").buildUniversalUrl(dest))
        XCTAssertEqual(provider("didi").appStoreId, "1362398401")
    }

    // MARK: - Formatting helpers

    func testFormatCoord() {
        XCTAssertEqual(RideProviders.formatCoord(18.3875655), "18.3875655")
        XCTAssertEqual(RideProviders.formatCoord(-66.0951019), "-66.0951019")
        XCTAssertEqual(RideProviders.formatCoord(18.0), "18")
        XCTAssertEqual(RideProviders.formatCoord(0.0), "0")
        XCTAssertEqual(RideProviders.formatCoord(-66.5), "-66.5")
    }

    func testEncodeUriComponent() {
        XCTAssertEqual(RideProviders.encodeUriComponent("Test Place"), "Test%20Place")
        XCTAssertEqual(RideProviders.encodeUriComponent("Avenida #1"), "Avenida%20%231")
        XCTAssertEqual(RideProviders.encodeUriComponent("Café"), "Caf%C3%A9")
        XCTAssertEqual(RideProviders.encodeUriComponent("a-b_c.d!~*'()"), "a-b_c.d!~*'()")
    }
}
