import XCTest

/// MVR-VP flow on the Xcode Simulator with a stub destination
/// (-uitest-stub): [1] launch → resolved destination with BOTH provider
/// actions → [2] Settings shows the primary + second provider pickers.
///
/// Screenshots are saved as XCTAttachments and exported by CI as the
/// `ios-simulator-screenshots` artifact. This is SIMULATOR proof with
/// demo data — never real-device proof (our IPAs are unsigned; see
/// ios/INSTALL-FREE.md).
final class FlowScreenshotsUITests: XCTestCase {

    override func setUp() {
        super.setUp()
        continueAfterFailure = false
    }

    private func snap(_ app: XCUIApplication, _ name: String) {
        let attachment = XCTAttachment(screenshot: app.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }

    func testResolvedDestinationAndSettings() {
        let app = XCUIApplication()
        app.launchArguments = ["-uitest-stub"]
        app.launch()

        // Stage 1: the resolved destination offers BOTH providers —
        // primary (Uber) and second (Lyft) — side by side.
        let openUber = app.buttons["Open in Uber"]
        XCTAssertTrue(openUber.waitForExistence(timeout: 15))
        XCTAssertTrue(app.buttons["Open in Lyft"].exists)
        snap(app, "stage1-resolved-destination")

        // Stage 2: Settings carries the primary + second provider
        // pickers (the second-provider rule lives here, not on the
        // result screen's map or pin).
        app.buttons["Settings"].tap()
        let secondHeader = app.staticTexts["Second provider (price comparison)"]
        XCTAssertTrue(secondHeader.waitForExistence(timeout: 10))
        snap(app, "stage2-settings-second-provider")
    }
}
