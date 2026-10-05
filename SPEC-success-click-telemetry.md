# Maps2Uber Android: Success + Click Telemetry Spec

## Objective
Extend the existing failure-reporting learning loop to also capture:
1. Successful link submissions (when a map link resolves to coordinates).
2. Uber deep-link clicks (when user taps any Uber link in the app).

## Existing Infrastructure (DO NOT CHANGE)
- `FailureReportRepository.report(url, error)` — already reports failures to POST /api/resolve-failures. Fire-and-forget, exceptions swallowed.
- `Maps2UberApi` — Retrofit interface with `reportFailure` method.
- `MainViewModel.startWithUrl` — calls `failureReports.report(url, r.error)` on ResolveResult.Err.

## New Requirements

### 1. Success Reporting
- New DTO: `SuccessReportDto(url: String, source: String = "android")`
- New API method: `POST /api/resolve-success` with SuccessReportDto body.
- New repository method: `suspend fun reportSuccess(url: String)` — fire-and-forget, swallow exceptions.
- Call from `MainViewModel.startWithUrl` in the `is ResolveResult.Ok` branch, in its own coroutine (never block UI).

### 2. Deep-Link Click Reporting
- New DTO: `DeepLinkClickDto(deep_link: String, map_url: String, link_type: String, probe_bearing: Int?, source: String = "android")`
- New API method: `POST /api/deep-link-clicks` with DeepLinkClickDto body.
- New repository method: `suspend fun reportClick(deepLink: String, mapUrl: String, linkType: String, probeBearing: Int?)` — fire-and-forget, swallow exceptions.
- `MainViewModel` needs:
  - A `lastSubmittedUrl: String` property to track the original map URL for click attribution.
  - A `reportDeepLinkClick(deepLink: String, linkType: String, probeBearing: Int?)` method that launches a coroutine to call `failureReports.reportClick`.
- UI call sites (in ResultsScreen.kt and NavGraph.kt):
  - Main "Open in Uber" button: linkType="main", probeBearing=null
  - Probe list rows: linkType="probe", probeBearing=bearing (0, 36, 72, ... 324)
  - Map pin popup: linkType="main" or "probe" with bearing as appropriate

## Constraints
- All reporting is fire-and-forget; never block UI or crash on network failure.
- Follow existing code patterns (coroutine launch, try/catch swallowing).
- Do not modify the existing failure-reporting behavior.

## Verification
- Unit tests for DTO serialization.
- Unit tests for repository methods (mock API, verify POST called).
- Verify no regression in existing failure reporting.
