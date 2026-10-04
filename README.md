# Maps2Uber (Android)

Native Android port of https://maps2uber.vasilyespana.workers.dev/ — share a Google Maps pin, get Uber deep links.

**What it does:** share a Google Maps pin to the app (or tap a maps link in WhatsApp/Facebook) → the app resolves it via the worker's `/api/resolve` endpoint → produces one Uber deep link to the exact spot **plus 10 probe links on a 100 m circle** to compare fares / detect zone boundaries.

## Setup

- JDK 17, Android SDK (platform 34, build-tools 34.0.0)
- Release signing: `release.keystore` + `keystore.properties` (both gitignored, never committed).
  CI restores them from repo secrets: `ANDROID_KEYSTORE_BASE64` / `KEYSTORE_PASSWORD` /
  `KEY_ALIAS` / `KEY_PASSWORD`.
- Cloud device gate needs repo secrets `BROWSERSTACK_USERNAME` / `BROWSERSTACK_ACCESS_KEY`.

## Build

```bash
./gradlew :app:testDebugUnitTest   # unit tests (fail-first: UberLinks, UrlExtractor, ResolveParser)
./gradlew assembleRelease          # per-ABI release splits (arm64-v8a + armeabi-v7a)
```

## Intent filters (the core feature)

- `ACTION_SEND` + `text/plain` → appears in the Android share sheet as **Maps2Uber**; the first http(s) URL is extracted from the shared text.
- `ACTION_VIEW` + https for `maps.app.goo.gl`, `goo.gl/maps`, `maps.google.com`, `google.com/maps`, `www.google.com/maps` → tapping a maps link offers this app.

A cold start carrying a link skips Home and goes straight to Resolving → Results.

## Link format (port of the worker, byte-exact)

```
https://m.uber.com/ul/?action=setPickup
[&pickup[latitude]=..&pickup[longitude]=..&pickup[nickname]=..]   # omitted when pickup = current location
&dropoff[latitude]=..&dropoff[longitude]=..&dropoff[nickname]=..&dropoff[formatted_address]=..
```

Probes: 10 points at bearings 0°, 36°, …, 324°, 100 m out (haversine destination-point formula).

## Quality gate

Every PR to `main` runs `.github/workflows/android-device-gate.yml`: unit tests → release build → `apksigner`/`aapt2` static checks → install + launch on a real BrowserStack Pixel 7 → screenshot artifact. No Vercel anywhere in this stack (GitHub + Cloudflare only).
