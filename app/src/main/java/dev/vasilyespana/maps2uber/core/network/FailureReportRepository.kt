package dev.vasilyespana.maps2uber.core.network

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fire-and-forget intake for the resolve-failure learning loop.
 *
 * Contract: [report] MUST never throw and MUST never be awaited by the UI
 * flow. The endpoint may not exist yet (404) or may fail — in all cases this
 * is a silent no-op. Callers should additionally launch it in its own
 * coroutine so a slow network never delays the user-visible state.
 */
@Singleton
class FailureReportRepository @Inject constructor(
    private val api: Maps2UberApi,
) {
    suspend fun report(url: String, error: String) {
        try {
            api.reportFailure(FailureReportDto(url = url, error = error))
        } catch (e: Exception) {
            // Intentionally swallowed: reporting is best-effort telemetry.
        }
    }
}
