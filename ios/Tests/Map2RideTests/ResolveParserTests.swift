import XCTest
@testable import Map2Ride

/// ResolveParser must map the worker's /api/resolve payloads exactly
/// like Android's ResolveParser does.
final class ResolveParserTests: XCTestCase {
    private func parse(_ json: String) -> ResolveOutcome {
        ResolveParser.parse(Data(json.utf8))
    }

    func testOkPayload() {
        let outcome = parse("""
        {"ok":true,"lat":18.3875655,"lng":-66.0951019,
         "name":"Some Place","address":"1 Main St","geocoded":false}
        """)
        XCTAssertEqual(outcome, .success(ResolvedDestination(
            lat: 18.3875655, lng: -66.0951019,
            name: "Some Place", address: "1 Main St")))
    }

    func testOkPayloadWithMissingNameAndAddress() {
        let outcome = parse(#"{"ok":true,"lat":1.5,"lng":2.5}"#)
        XCTAssertEqual(outcome, .success(ResolvedDestination(
            lat: 1.5, lng: 2.5, name: "", address: "")))
    }

    func testErrorPayload() {
        let outcome = parse(#"{"ok":false,"error":"no-location","name":"x"}"#)
        XCTAssertEqual(outcome, .failure("no-location"))
    }

    func testErrorPayloadWithoutErrorText() {
        XCTAssertEqual(parse(#"{"ok":false}"#), .failure("bad-response"))
    }

    func testOkWithoutCoordinatesIsBadResponse() {
        XCTAssertEqual(parse(#"{"ok":true,"name":"x"}"#), .failure("bad-response"))
    }

    func testGarbageIsBadResponse() {
        XCTAssertEqual(parse("not json at all"), .failure("bad-response"))
        XCTAssertEqual(parse(""), .failure("bad-response"))
    }
}
