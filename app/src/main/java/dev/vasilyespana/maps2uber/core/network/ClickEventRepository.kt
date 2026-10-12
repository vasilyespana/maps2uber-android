package dev.vasilyespana.maps2uber.core.network

import dev.vasilyespana.maps2uber.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fire-and-forget intake for click events (provider choices, copy,
 * settings changes) — POST /api/click-events on the worker.
 *
 * Same contract as [FailureReportRepository]: [report] MUST never throw
 * and MUST never be awaited by the UI flow. Telemetry is best-effort;
 * a failed report is a silent no-op.
 */
@Singleton
class ClickEventRepository @Inject constructor(
    private val api: Maps2UberApi,
) {
    suspend fun report(event: ClickEvent) {
        try {
            api.reportClickEvent(event.toDto(BuildConfig.VERSION_NAME))
        } catch (e: Exception) {
            // Intentionally swallowed: reporting is best-effort telemetry.
        }
    }
}
