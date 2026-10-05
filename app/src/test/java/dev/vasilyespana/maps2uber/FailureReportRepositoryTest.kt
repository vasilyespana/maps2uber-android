package dev.vasilyespana.maps2uber

import com.google.gson.Gson
import dev.vasilyespana.maps2uber.core.network.DeepLinkClickDto
import dev.vasilyespana.maps2uber.core.network.FailureReportDto
import dev.vasilyespana.maps2uber.core.network.FailureReportRepository
import dev.vasilyespana.maps2uber.core.network.Maps2UberApi
import dev.vasilyespana.maps2uber.core.network.SuccessReportDto
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/**
 * Fail-first tests for the resolve-failure learning loop intake.
 *
 * When /api/resolve returns ok:false (or errors), the app fire-and-forgets
 * POST /api/resolve-failures {url, error, source:"android"}. Reporting must
 * NEVER break the user flow: no throw on network errors, no throw on HTTP
 * errors (e.g. the endpoint not being live yet).
 */
class FailureReportRepositoryTest {

    private class FakeApi(
        var onReport: (FailureReportDto) -> Response<ResponseBody> = {
            Response.success("".toResponseBody("application/json".toMediaType()))
        },
        var onSuccess: (SuccessReportDto) -> Response<ResponseBody> = {
            Response.success("".toResponseBody("application/json".toMediaType()))
        },
        var onClick: (DeepLinkClickDto) -> Response<ResponseBody> = {
            Response.success("".toResponseBody("application/json".toMediaType()))
        },
    ) : Maps2UberApi {
        var reported: FailureReportDto? = null
        var successReported: SuccessReportDto? = null
        var clickReported: DeepLinkClickDto? = null
        var calls = 0
        var successCalls = 0
        var clickCalls = 0

        override suspend fun resolve(url: String): Response<ResponseBody> =
            throw UnsupportedOperationException("not under test")

        override suspend fun reportFailure(body: FailureReportDto): Response<ResponseBody> {
            calls++
            reported = body
            return onReport(body)
        }

        override suspend fun reportSuccess(body: SuccessReportDto): Response<ResponseBody> {
            successCalls++
            successReported = body
            return onSuccess(body)
        }

        override suspend fun reportClick(body: DeepLinkClickDto): Response<ResponseBody> {
            clickCalls++
            clickReported = body
            return onClick(body)
        }
    }

    @Test
    fun report_sendsAndroidSourcePayload() = runBlocking {
        val api = FakeApi()
        val repo = FailureReportRepository(api)

        repo.report("https://maps.app.goo.gl/xyz", "unresolvable")

        assertEquals(1, api.calls)
        val dto = api.reported!!
        assertEquals("https://maps.app.goo.gl/xyz", dto.url)
        assertEquals("unresolvable", dto.error)
        assertEquals("android", dto.source)

        // Wire shape: the JSON the server receives must carry the three keys.
        val json = Gson().toJson(dto)
        assertTrue(json.contains("\"url\""))
        assertTrue(json.contains("\"error\""))
        assertTrue(json.contains("\"source\""))
        assertTrue(json.contains("\"android\""))
    }

    @Test
    fun report_neverThrowsOnNetworkError() = runBlocking {
        val api = FakeApi(onReport = { throw IOException("no route to host") })
        val repo = FailureReportRepository(api)

        // Must return normally — the user flow continues to the manual fallback.
        repo.report("https://maps.app.goo.gl/xyz", "network-error")
        assertEquals(1, api.calls)
    }

    @Test
    fun report_neverThrowsOnHttpError() = runBlocking {
        // Endpoint not live yet (or 500): still a no-op, never a crash.
        val api = FakeApi(onReport = {
            Response.error(404, "".toResponseBody("text/plain".toMediaType()))
        })
        val repo = FailureReportRepository(api)

        repo.report("https://maps.app.goo.gl/xyz", "unresolvable")
        assertEquals(1, api.calls)
    }

    @Test
    fun report_neverThrowsOnUnexpectedException() = runBlocking {
        val api = FakeApi(onReport = { throw RuntimeException("boom") })
        val repo = FailureReportRepository(api)

        repo.report("https://maps.app.goo.gl/xyz", "boom")
        assertEquals(1, api.calls)
    }

    // ---- Success reporting ----

    @Test
    fun reportSuccess_sendsAndroidSourcePayload() = runBlocking {
        val api = FakeApi()
        val repo = FailureReportRepository(api)

        repo.reportSuccess("https://maps.app.goo.gl/xyz")

        assertEquals(1, api.successCalls)
        val dto = api.successReported!!
        assertEquals("https://maps.app.goo.gl/xyz", dto.url)
        assertEquals("android", dto.source)

        val json = Gson().toJson(dto)
        assertTrue(json.contains("\"url\""))
        assertTrue(json.contains("\"source\""))
        assertTrue(json.contains("\"android\""))
    }

    @Test
    fun reportSuccess_neverThrowsOnNetworkError() = runBlocking {
        val api = FakeApi(onSuccess = { throw IOException("no route to host") })
        val repo = FailureReportRepository(api)

        repo.reportSuccess("https://maps.app.goo.gl/xyz")
        assertEquals(1, api.successCalls)
    }

    @Test
    fun reportSuccess_neverThrowsOnHttpError() = runBlocking {
        val api = FakeApi(onSuccess = {
            Response.error(404, "".toResponseBody("text/plain".toMediaType()))
        })
        val repo = FailureReportRepository(api)

        repo.reportSuccess("https://maps.app.goo.gl/xyz")
        assertEquals(1, api.successCalls)
    }

    // ---- Deep-link click reporting ----

    @Test
    fun reportClick_sendsSnakeCasePayload() = runBlocking {
        val api = FakeApi()
        val repo = FailureReportRepository(api)

        repo.reportClick(
            deepLink = "https://m.uber.com/ul/?action=setPickup",
            linkType = "probe",
            probeBearing = 72,
            mapUrl = "https://maps.app.goo.gl/xyz",
        )

        assertEquals(1, api.clickCalls)
        val dto = api.clickReported!!
        assertEquals("https://m.uber.com/ul/?action=setPickup", dto.deepLink)
        assertEquals("probe", dto.linkType)
        assertEquals(72, dto.probeBearing)
        assertEquals("https://maps.app.goo.gl/xyz", dto.mapUrl)
        assertEquals("android", dto.source)

        // Wire shape: server expects snake_case keys.
        val json = Gson().toJson(dto)
        assertTrue(json.contains("\"deep_link\""))
        assertTrue(json.contains("\"link_type\""))
        assertTrue(json.contains("\"probe_bearing\""))
        assertTrue(json.contains("\"map_url\""))
        assertTrue(json.contains("\"source\""))
    }

    @Test
    fun reportClick_mainLinkHasNullBearing() = runBlocking {
        val api = FakeApi()
        val repo = FailureReportRepository(api)

        repo.reportClick(
            deepLink = "https://m.uber.com/ul/?action=setPickup",
            linkType = "destination",
            probeBearing = null,
            mapUrl = "https://maps.app.goo.gl/xyz",
        )

        assertEquals(1, api.clickCalls)
        assertEquals(null, api.clickReported!!.probeBearing)
        assertEquals("destination", api.clickReported!!.linkType)
    }

    @Test
    fun reportClick_neverThrowsOnNetworkError() = runBlocking {
        val api = FakeApi(onClick = { throw IOException("no route to host") })
        val repo = FailureReportRepository(api)

        repo.reportClick("https://m.uber.com/ul/", "destination", null, "https://maps.app.goo.gl/xyz")
        assertEquals(1, api.clickCalls)
    }
}
