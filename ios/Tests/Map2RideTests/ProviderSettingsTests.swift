import XCTest
@testable import Map2Ride

/// Second-provider rules from Vasily's rework brief (2026-10-08):
/// default second = Lyft when primary is Uber; second == primary is
/// rejected/shifted; changing the primary onto the second provider
/// re-homes the second provider.
final class ProviderSettingsTests: XCTestCase {
    private var suiteName: String!
    private var defaults: UserDefaults!

    override func setUp() {
        super.setUp()
        suiteName = "map2ride-tests-\(UUID().uuidString)"
        defaults = UserDefaults(suiteName: suiteName)
    }

    override func tearDown() {
        defaults.removePersistentDomain(forName: suiteName)
        super.tearDown()
    }

    func testDefaultsAreUberPrimaryLyftSecond() {
        let settings = ProviderSettings(defaults: defaults)
        XCTAssertEqual(settings.primaryId, "uber")
        XCTAssertEqual(settings.secondId, "lyft")
        XCTAssertEqual(settings.primary.name, "Uber")
        XCTAssertEqual(settings.second.name, "Lyft")
    }

    func testDefaultSecondForLyftPrimaryIsUber() {
        XCTAssertEqual(ProviderSettings.defaultSecond(forPrimary: "lyft"), "uber")
        XCTAssertEqual(ProviderSettings.defaultSecond(forPrimary: "uber"), "lyft")
        XCTAssertEqual(ProviderSettings.defaultSecond(forPrimary: "bolt"), "lyft")
    }

    func testSetSecondToPrimaryIsShiftedToDefault() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setSecond("uber") // == primary
        XCTAssertEqual(settings.secondId, "lyft")
    }

    func testSetSecondToValidProvider() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setSecond("bolt")
        XCTAssertEqual(settings.secondId, "bolt")
    }

    func testSetSecondToUnsupportedProviderIsShifted() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setSecond("waymo")
        XCTAssertEqual(settings.secondId, "lyft")
    }

    func testSetSecondToUnknownProviderIsShifted() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setSecond("not-a-provider")
        XCTAssertEqual(settings.secondId, "lyft")
    }

    func testChangingPrimaryOntoSecondRehomesSecond() {
        let settings = ProviderSettings(defaults: defaults)
        XCTAssertEqual(settings.secondId, "lyft")
        settings.setPrimary("lyft")
        XCTAssertEqual(settings.primaryId, "lyft")
        XCTAssertEqual(settings.secondId, "uber")
    }

    func testChangingPrimaryElsewhereKeepsSecond() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setPrimary("bolt")
        XCTAssertEqual(settings.primaryId, "bolt")
        XCTAssertEqual(settings.secondId, "lyft")
    }

    func testUnknownPrimaryFallsBackToUber() {
        let settings = ProviderSettings(defaults: defaults)
        settings.setPrimary("not-a-provider")
        XCTAssertEqual(settings.primaryId, "uber")
    }

    func testSelectionPersistsAcrossInstances() {
        let first = ProviderSettings(defaults: defaults)
        first.setPrimary("grab")
        first.setSecond("bolt")
        let second = ProviderSettings(defaults: defaults)
        XCTAssertEqual(second.primaryId, "grab")
        XCTAssertEqual(second.secondId, "bolt")
    }

    func testStoredCollisionIsRepairedOnLoad() {
        defaults.set("lyft", forKey: "ride_provider_id")
        defaults.set("lyft", forKey: "second_ride_provider_id")
        let settings = ProviderSettings(defaults: defaults)
        XCTAssertEqual(settings.primaryId, "lyft")
        XCTAssertEqual(settings.secondId, "uber")
    }
}
