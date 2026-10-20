# Map2Ride for iOS

Native iOS port of Map2Ride (this repo's Android app) — paste a Google
Maps link, get the destination, and open it in your ride app.

**The loop:** paste a maps link (or pick it up from the clipboard) →
the app resolves it via the production worker
(`https://maps2uber.vasilyespana.workers.dev/api/resolve`) → the
destination is shown with **two** provider actions — your primary
provider and your second provider — so you can open both and compare
prices before booking.

## Features (parity with Android v1.5.0 for the resolve-and-go loop)

- **Resolve** — short/long Maps links resolved server-side by the same
  worker the Android app uses; unresolvable links are reported back to
  the worker's learning-loop intake (`/api/resolve-failures`,
  source `ios`), exactly like Android.
- **Providers** — the Android v1.5.0 catalog, byte-identical deep
  links: Uber, Lyft, Bolt, FreeNow, Grab, Gett, DiDi. Waymo, Zoox and
  Baidu Apollo Go appear in Settings as "Coming soon", as on Android.
- **Settings** — primary provider + **second provider** (price
  comparison): defaults to Lyft when the primary is Uber, can be any
  provider except the primary, and auto-shifts if the primary is
  changed onto it (Vasily's rework spec, 2026-10-08).
- **Open fallbacks** — native deep link when the provider app is
  installed → universal web URL → App Store page (same order as
  Android's launcher).

## Not in this version (deliberate gaps)

- **Fare probe links** (the 100 m circle of 10 points and the bearing
  table) stay Android-only for now; the iOS loop is resolve-and-go
  with the two-provider comparison on the resolved destination.
- **Pickup presets / current-location pickup** — provider links carry
  the destination only; Uber/Lyft default the pickup to your current
  location inside their apps.
- **Share Extension** — links arrive via paste/clipboard for now.
- **Push, accounts, history** — none exist on Android either.

## Build

```sh
brew install xcodegen
cd ios && xcodegen generate
xcodebuild -project Map2Ride.xcodeproj -scheme Map2Ride \
  -destination 'platform=iOS Simulator,name=iPhone 16' test
```

CI (`.github/workflows/ios.yml`, macos-15) runs the unit + UI tests,
builds the **unsigned** IPA on every PR/push, and attaches it to a
GitHub Release on `ios-v*` tags.

## Distribution — SideStore (free)

There is no App Store listing (no Apple Developer Program membership).
The app ships through **SideStore**: add the source feed once and both
Map2Ride **and** CallPages install/update from it — see
[INSTALL-FREE.md](INSTALL-FREE.md). The feed lives at the repo root:
`sidestore-source.json`.

The app icon (`icon-512.png`, and the 1024 px app-icon derived from
it) is the product's existing iOS icon, carried over from the earlier
standalone iOS prototype repo (vasilyespana/maps2uber-ios).
