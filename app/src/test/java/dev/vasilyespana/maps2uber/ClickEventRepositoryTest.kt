package dev.vasilyespana.maps2uber

import com.google.gson.Gson
import dev.vasilyespana.maps2uber.core.network.ClickEvent
import dev.vasilyespana.maps2uber.core.network.ClickEventDto
import dev.vasilyespana.maps2uber.core.network.ClickEventRepository
import dev.vasilyespana.maps2uber.core.network.ClickEvents
import dev.vasilyespana.maps2uber.core.network.FailureReportDto
import dev.vasilyespana.maps2uber.core.network.Maps2UberApi
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Click-event telemetry (Vasily directive 2026-10-12: track ALL clicks —
 * which provider people choose on the circle vs the table, primary vs
 * second-provider link, copy/share).
 *
 * Contract under test: UI taps become POST /api/click-events payloads with
 * {event, provider, surface, position, url, platform:"android",
 * app_version}; reporting must NEVER break the user flow (no throw on
 * network errors or HTTP errors), mirroring FailureReportRepository.
 */
class ClickEventRepositoryTest {

    private class FakeApi(
        var onReport: (ClickEventDto) -> Response<ResponseBody> = {
            Response.success("".toResponseBody("application/json".toMediaType()))
        },
    ) : Maps2UberApi {
        var reported: ClickEventDto? = null
        var calls = 0

        override suspend fun resolve(url: String): Response<ResponseBody> =
            throw UnsupportedOperationException("not under test")

        override suspend fun reportFailure(body: FailureReportDto): Response<ResponseBody> =
            throw UnsupportedOperationException("not under test")

        override suspend fun reportClickEvent(body: ClickEventDto): Response<ResponseBody> {
            calls++
            reported = body
            return onReport(body)
        }
    }

    @Test
    fun report_sendsFullClickPayload() = runBlocking {
        val api = FakeApi()
        val repo = ClickEventRepository(api)

        repo.report(
            ClickEvent(
                event = ClickEvents.PROVIDER_OPEN_SECOND,
                providerId = "lyft",
                surface = ClickEvents.SURFACE_TABLE,
                position = ClickEvents.POSITION_SECOND,
                url = "https://maps.app.goo.gl/xyz",
            ),
        )

        assertEquals(1, api.calls)
        val dto = api.reported!!
        assertEquals("provider_open_second", dto.event)
        assertEquals("lyft", dto.provider)
        assertEquals("table", dto.surface)
        assertEquals("second", dto.position)
        assertEquals("https://maps.app.goo.gl/xyz", dto.url)
        assertEquals("android", dto.platform)
        assertEquals(BuildConfig.VERSION_NAME, dto.appVersion)

        // Wire shape: the JSON the server receives must carry the keys.
        val json = Gson().toJson(dto)
        assertTrue(json.contains("\"event\""))
        assertTrue(json.contains("\"provider\""))
        assertTrue(json.contains("\"surface\""))
        assertTrue(json.contains("\"app_version\""))
    }

    @Test
    fun report_omitsAbsentOptionalFields() = runBlocking {
        val api = FakeApi()
        val repo = ClickEventRepository(api)

        repo.report(
            ClickEvent(
                event = ClickEvents.SETTINGS_DEFAULT_PROVIDER_CHANGE,
                providerId = "bolt",
                surface = ClickEvents.SURFACE_SETTINGS,
            ),
        )

        val dto = api.reported!!
        assertNull(dto.position)
        assertNull(dto.url)
        assertNull(dto.buttonId)
        // Gson drops nulls: no position/url/button_id keys on the wire.
        val json = Gson().toJson(dto)
        assertFalse(json.contains("\"position\""))
        assertFalse(json.contains("\"url\""))
        assertFalse(json.contains("\"button_id\""))
    }

    @Test
    fun report_neverThrowsOnNetworkError() = runBlocking {
        val api = FakeApi(onReport = { throw IOException("offline") })
        val repo = ClickEventRepository(api)

        // Must complete silently — a throw here would fail the test.
        repo.report(
            ClickEvent(
                event = ClickEvents.PROVIDER_OPEN_PRIMARY,
                providerId = "uber",
                surface = ClickEvents.SURFACE_CIRCLE,
                position = ClickEvents.POSITION_PRIMARY,
                url = "https://maps.app.goo.gl/xyz",
            ),
        )
        assertEquals(1, api.calls)
    }

    @Test
    fun report_neverThrowsOnHttpError() = runBlocking {
        val api = FakeApi(
            onReport = {
                Response.error<ResponseBody>(
                    500,
                    "err".toResponseBody("text/plain".toMediaType()),
                )
            },
        )
        val repo = ClickEventRepository(api)

        repo.report(
            ClickEvent(
                event = ClickEvents.COPY_LINK,
                providerId = "uber",
                surface = ClickEvents.SURFACE_BUTTON,
            ),
        )
        assertEquals(1, api.calls)
    }
}
