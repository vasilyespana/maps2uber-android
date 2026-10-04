"""
BrowserStack device gate for Maps2Uber: install + launch the release APK on a
real physical device and capture a screenshot as proof.

Reads BROWSERSTACK_USERNAME / BROWSERSTACK_ACCESS_KEY / APP_URL from the
environment. Exits non-zero if the session cannot start (install failed)
or the screenshot cannot be captured (launch failed/crashed).
"""
import os
import sys

from appium import webdriver
from appium.options.android import UiAutomator2Options

USERNAME = os.environ["BROWSERSTACK_USERNAME"]
ACCESS_KEY = os.environ["BROWSERSTACK_ACCESS_KEY"]
APP_URL = os.environ["APP_URL"]

options = UiAutomator2Options()
options.set_capability("deviceName", "Google Pixel 7")
options.set_capability("os_version", "13.0")
options.set_capability("app", APP_URL)
options.set_capability("project", "Maps2Uber")
options.set_capability("build", os.environ.get("GITHUB_SHA", "local")[:8])
options.set_capability("name", "device-gate install+launch")
options.set_capability("browserstack.debug", "true")

# A session that starts at all means BrowserStack installed the app and
# launched its MAIN/LAUNCHER activity on a real device.
driver = webdriver.Remote(
    f"https://{USERNAME}:{ACCESS_KEY}@hub-cloud.browserstack.com/wd/hub",
    options=options,
)
try:
    print("Session started:", driver.session_id)
    print("Device:", driver.capabilities.get("device"), driver.capabilities.get("os_version"))
    driver.implicitly_wait(20)
    png = driver.get_screenshot_as_png()
    with open("/tmp/bs-launch.png", "wb") as f:
        f.write(png)
    print("Screenshot saved: /tmp/bs-launch.png (%d bytes)" % len(png))
    # Sanity: the app UI must be visible (not a crash dialog or launcher).
    src = driver.page_source
    launched = "Maps" in src and "Uber" in src
    crashed = "has stopped" in src or "keeps stopping" in src
    print("App UI visible:", launched, "| crash dialog:", crashed)
    if crashed:
        print("FAIL: crash dialog detected")
        print("--- page_source (first 3000 chars) ---")
        print(src[:3000])
        sys.exit(1)
    if not launched:
        print("FAIL: app UI did not become visible (no 'Maps → Uber' text on screen)")
        print("--- page_source (first 3000 chars) ---")
        print(src[:3000])
        sys.exit(1)
finally:
    driver.quit()
print("DEVICE GATE PASSED")
