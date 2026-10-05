package dev.vasilyespana.maps2uber.core.network

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fire-and-forget intake for the resolve learning loop (failures, successes,
 * and deep-link clicks).
 *
 * Contract: all report methods MUST never throw and MUST never be awaited by
 * the UI flow. Endpoints may 404 or fail — in all cases this is a silent
 * no-op. Callers should additionally launch in their own coroutine so a slow
 * network never delays the user-visible state.
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

    suspend fun reportSuccess(url: String) {
        try {
            api.reportSuccess(SuccessReportDto(url = url))
        } catch (e: Exception) {
            // Intentionally swallowed: reporting is best-effort telemetry.
        }
    }

    suspend fun reportClick(deepLink: String, linkType: String, probeBearing: Int?, mapUrl: String) {
        try {
            api.reportClick(DeepLinkClickDto(
                deepLink = deepLink,
                linkType = linkType,
                probeBearing = probeBearing,
                mapUrl = mapUrl,
            ))
        } catch (e: Exception) {
            // Intentionally swallowed: reporting is best-effort telemetry.
        }
    }
}
