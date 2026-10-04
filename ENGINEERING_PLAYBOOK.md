# Engineering Playbook (EOS)

> **Location:** repo root · **Applies to:** every human engineer, AI agent, and contractor.
> **Enforcement:** strict. Non-compliant PRs fail automated checks and are rejected.
> This is the Single Source of Truth for how we build. Repo-specific notes
> live in `AGENTS.md`; when they conflict, this playbook wins unless
> Vasily has ruled otherwise in writing.

## 1. Core Principles

- **Full-stack ownership — no silent backends.** Backend code, DB migrations,
  or API endpoints without a corresponding UI/UX implementation are
  INCOMPLETE. If a setting or feature ships, the merchant dashboard and the
  end-customer experience reflect it immediately. "It needs to work both on
  the front end and the back end."
- **Visual proof required for merge.** No PR merges without rendered
  screenshots or video of the finished experience on desktop and on a
  375px mobile viewport.
- **Zero unverified claims.** A task is done only when proven by automated
  dynamic testing (passing test output + screenshot/video). Banned until
  verified: "should be fixed", "implemented", "done/shipped/live".
- **IaC & immutable secrets.** No manual secret editing in cloud dashboards.
  Secrets live in the CI/CD vault (GitHub Secrets) with pre-flight
  deployment validation.
- **Platform parity.** Web feature releases keep 100% feature parity on
  native mobile unless explicitly designated platform-specific.
- **Never make the user the manual tester.** Verification is autonomous and
  programmatic. Never ask the user to visit URLs, sign in, or confirm a
  page loads.
- **GitHub + Cloudflare only.** No Vercel anywhere in the stack — no deploys,
  configs, SDKs, or integrations.

## 2. Navigation & Layout Standards

Container decision matrix:

```
Is the action destructive, urgent, or < 3 input fields?
  ├─ YES → modal/dialog permitted (delete confirm, quick rename)
  └─ NO  → standalone page with tabbed layout
```

**Forbidden in modals** (`<Dialog>`, `<Drawer>`): multi-component forms,
product editing, settings, integrations, tabbed navigation inside a modal,
long scrolling forms, media previews.

**Required:** heavy editing/configuration uses dedicated routes
(e.g. `/settings/payments`, `/products/:id/edit#details`) with breadcrumb
return links and top-level tab bars.

## 3. Security, Secrets & Drift Prevention

- **Zero dashboard editing.** Secrets are never added/edited by hand in the
  Cloudflare console. Source of truth: GitHub Secrets; deploys inject them.
- **Pre-flight CI gates.** Every deploy runs `scripts/validate-secrets.sh`
  (or equivalent) before touching production. Any missing mandatory variable
  (`GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `SESSION_SECRET`, …) aborts
  the build immediately.
- **Runtime health.** Services expose `/api/health/config` reporting variable
  *presence* (`PRESENT`/`MISSING`) — never values. `MISSING` triggers an
  instant high-priority alert.
- **Third-party auth testing (Vasily's 2026-10-04 "middle" decision).**
  Never automate the live provider login UI (bot detection makes it flaky;
  real credentials in CI are a security risk). Use **OAuth redirect
  validators**: assert the auth endpoint 302s to the provider with correct
  `client_id`/`redirect_uri`/`scope`, plus server-side callback health.
  (The "real handshake" alternative was considered and rejected.)

## 4. Native Mobile Directives (Android; iOS when it exists)

- **Full architecture parity:** native apps (Kotlin/Jetpack Compose;
  Swift/SwiftUI) match 100% of web admin functionality.
- **Hardware integration:** audio, WebSockets, WebRTC, and push (FCM/APNs)
  use native foreground services and audio session controllers.
- **Offline-first:** Room/CoreData with WorkManager background sync for
  voicemail recordings, catalog drafts, etc.
- **Release gate:** no APK/AAB ships without static analysis (valid
  V2/V3 signature, correct package/ABIs) AND install+launch on real cloud
  devices — proof (logs + screenshot), never "should install fine".

## 5. Testing & Release Pipeline

- **Layers:** unit/integration, E2E UI flows (Playwright/Espresso), visual
  regression snapshots.
- **Fail-first bug protocol:** reproduce with an executable test that FAILS
  before any fix; the fix is proven only by the test passing.
- **No silent skips:** `.skip()`/`@Ignore` without an active tracking item
  is prohibited.
- **Pipeline gate (staging → prod):**
  `[ Clean E2E ] → [ Mobile smoke ] → [ Secret validation ] → [ Deploy ]`
- **Instrument first:** any feature whose failures feed a learning loop
  ships its failure reporting in v1 — never as a follow-up. Never ask the
  user to resend data the instrumentation should have captured.

## 6. PR Submission Standard

Every PR description **must** contain:

```markdown
## 📝 Summary of Changes
- **Backend/Infra:** [APIs, DB migrations, health checks]
- **Frontend/CX:** [Merchant dashboard & end-customer UI updates]

## 🧪 Testing & Secret Validation
| Layer | Tests | Pass rate | Pre-flight secrets |
|---|---|---|---|
| Unit / Integration | … | … | ✅/N/A |
| E2E Web & Auth | … | … | ✅ `/api/health/config` |
| Mobile E2E | … | … | N/A |

## 📸 UX Proof (required)
### 1. Merchant / standalone edit page (no modals for edit views)
![Merchant UI Screenshot]
### 2. End-customer view (checkout / call / interactive)
![Customer View Screenshot]
### 3. Mobile 375px viewport
![Mobile UI Screenshot]
```

The `pr-standards` CI check fails the PR when these sections or the
screenshot placeholders are missing/unaltered.

## 7. AI Agent Directive

Before generating code or PRs, every agent (Copilot, Cursor, custom agents)
must follow this playbook: full-stack completeness, standalone tabbed
pages over modals, pre-flight secret checks, fail-first executable tests,
and the mandatory PR screenshot template. Copilot drafts, the PM agent
reviews and verifies — nothing merges on code inspection alone.
