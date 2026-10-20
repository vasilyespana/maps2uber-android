# Installing Map2Ride & CallPages on your iPhone — the free way

No App Store, no $99 developer account. Both apps are installed with
a free tool called **SideStore**, which signs them with **your own
Apple ID** on your own devices. Your Apple ID is typed only into the
tools on your computer and phone — never into a website, never sent
to us.

**One source, both apps.** Map2Ride and CallPages live in a single
SideStore source feed. You add the source once; both apps appear in
SideStore and install/update from there:

```
https://raw.githubusercontent.com/vasilyespana/maps2uber-android/main/sidestore-source.json
```

## ⚠️ The 3-app math — read this first

Apple's free signing allows **at most 3 sideloaded apps per phone**,
and SideStore itself counts as one. So:

- **SideStore + CallPages + Map2Ride = 3 of 3 — full.**
- There is no room for a fourth sideloaded app. If you ever need one,
  you must remove one of these inside SideStore first.

And the other free-signing facts:

- **The signature lasts 7 days.** SideStore shows a countdown; before
  it hits 0 you tap **Refresh All** (about 30 seconds). The daily
  automation under "Your weekly routine" can do it for you.
- **If the 7 days lapse, the apps won't open** until refreshed.
  Nothing is deleted and **no data is lost** — refresh, never delete.
- **Maximum 10 app IDs** per free account per week. CallPages +
  Map2Ride use two; this only matters if you sideload many apps.
- Each person needs their own Apple ID + tool — this channel is for
  you and a few testers, not for shipping to hundreds of customers.

## ⚠️ One version warning — read before installing anything

> **SideStore 0.6.4 (the current "stable") is BROKEN for sign-in. DO
> NOT USE it.** In September 2026 Apple changed its sign-in servers,
> and 0.6.4 and earlier can no longer sign in (you'll see an HTTP 503
> error — it is *not* your password). The SideStore project itself
> labels that release "DO NOT USE."
>
> **Install the current nightly / 0.7.0-alpha build instead** — it
> contains Apple's-server fix. On **iOS 26.4 or later the nightly is
> also required for refreshing** to work.

## What you need

1. Your iPhone (both apps need iOS 17 or newer).
2. An Apple ID for signing. **Use a dedicated second Apple ID, not
   your main iCloud account** — create a free one just for
   sideloading. It does **not** need to be the account signed into
   the iPhone.
3. A computer (Windows, Mac, or Linux) and a USB cable — **one time
   only**, for setup.
4. A Wi-Fi network (SideStore installs and refreshes over Wi-Fi, not
   cellular).

## One-time setup — SideStore via iloader (~15–30 minutes)

*Skip this whole section if SideStore is already on your phone (for
example from installing CallPages before) — go straight to "Add the
source". The long version of this setup, with full troubleshooting,
lives in the CallPages repo's `ios/INSTALL-FREE.md`.*

**iloader** is the small official installer that puts SideStore on
your phone from the computer. After setup, the computer is out of the
picture.

1. **On the iPhone:** install **LocalDevVPN** from the App Store
   (free; the local VPN SideStore uses to install apps on-device).
   Open it once so it's ready.
2. **On the computer:** download **iloader** — only from its official
   GitHub repo (github.com/nab138/iloader) or iloader.app.
3. Connect the iPhone by USB, unlock it, tap **Trust** if asked.
4. Open iloader, sign in with your **dedicated secondary Apple ID**,
   select your iPhone, and choose **Install SideStore** — picking the
   **nightly / 0.7.0 build**, not 0.6.4 (see the warning above).
   iloader also places the pairing file SideStore needs.
5. **On the iPhone:** Settings → General → VPN & Device Management →
   under *Developer App*, tap the entry named for your Apple ID →
   **Trust** (on iOS 18+ this reads "Allow & Restart").
6. Settings → Privacy & Security → **Developer Mode** → **On**. The
   phone restarts; confirm **Turn On**.
7. Open **LocalDevVPN** and tap **Connect**. Keep it connected
   whenever you install or refresh apps.
8. Open **SideStore** and sign in with the **same secondary Apple
   ID**.
9. In SideStore → **My Apps**, tap the **"7 DAYS"** counter next to
   SideStore itself to refresh it once, **before installing any
   app** — this first refresh fixes SideStore's own registration.

## Add the source — then install both apps

1. Join your Wi-Fi network and make sure **LocalDevVPN shows
   Connected**.
2. Open **SideStore → Browse** (the sources view) → tap **+** (add
   source) → paste the source URL:

   ```
   https://raw.githubusercontent.com/vasilyespana/maps2uber-android/main/sidestore-source.json
   ```

3. The source **"Vasily's Apps"** appears with **two apps**:
   **CallPages** and **Map2Ride**.
4. Tap **GET** / **Install** on each. SideStore signs them with your
   free certificate and installs them — they appear on your Home
   Screen like normal apps.
5. **Updating later:** updates show up in SideStore from the same
   source. Install over the top — your data is kept. **Never delete
   the old app first.**

## Your weekly routine (under a minute)

- Before the counters in My Apps reach 0: connect LocalDevVPN, open
  SideStore → My Apps → **Refresh All**. Done.
- **Make it automatic (recommended):** in the Shortcuts app, create a
  Personal Automation that runs **once a day** (for example when the
  phone starts charging at night): *LocalDevVPN → Connect* →
  *SideStore → Refresh All Apps* → *LocalDevVPN → Disconnect*, set to
  run immediately without asking.
- **If an app has already expired:** don't delete it. Wi-Fi +
  LocalDevVPN + **Refresh All** brings it back, data intact.

## Alternates (the computer stays involved)

- **Sideloadly** (sideloadly.io only) — download the IPA directly
  (Map2Ride: this repo's GitHub **Releases**, every `ios-v…` release
  has `map2ride-ios-unsigned.ipa` attached; CallPages: the CallPages
  GitHub Releases), connect the phone by USB, drag the IPA in, enter
  your Apple ID, **Start**, then do the Trust and Developer Mode
  steps. Refreshing needs the computer again within 7 days.
- **AltStore Classic** (altstore.io) — same idea; refresh requires
  AltServer running on your computer on the same Wi-Fi. Use AltServer
  1.7.6+ (older builds have the same September 2026 sign-in breakage).

## Troubleshooting

- **Sign-in fails with "503"** → you're on SideStore 0.6.4 or an old
  AltServer. Update to the nightly / 0.7.0 build (or AltServer
  1.7.6+). It was never your password.
- **"App is no longer available" / app won't open** → the 7 days ran
  out. Refresh (Wi-Fi + LocalDevVPN + Refresh All). Data is intact.
- **"Maximum number of apps"** → you already have 3 (SideStore
  counts). Remove one inside SideStore, then install.
- **Pairing file errors** → reconnect the phone to the computer by
  USB and run iloader again to place a fresh pairing file.
- **Untrusted Developer** → Settings → General → VPN & Device
  Management → Trust your Apple ID entry.
- **Apple ID got locked** → a temporary lock, cleared by Apple's
  password/verification flow — and one more reason to use the
  dedicated secondary account.

## Safety rules (non-negotiable)

- **Official sources only.** SideStore: **sidestore.io** /
  **docs.sidestore.io**; iloader: its official GitHub repo or
  **iloader.app**; Sideloadly: **sideloadly.io** (lookalike sites such
  as "sideloadly.me" are NOT official); AltStore: **altstore.io**. The
  apps: only the source URL above and the GitHub Releases pages of
  the CallPages and Map2Ride repos.
- **Never upload a `.p12` file anywhere.** Any site or service that
  asks for your signing certificate (`.p12`) or profile is asking for
  the keys to sign anything as you.
- **Ignore paid "lifetime certificate" offers** — and "free
  certificate" files, Telegram/Discord certificates, and online
  IPA-signing websites. Shared certificates get revoked in bulk;
  "lifetime" certificates are a known scam pattern.
- Enter your Apple ID **only** inside SideStore / iloader / AltStore
  / Sideloadly on your own devices — never on a web page, never in
  chat, never send it to us.
