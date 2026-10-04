package dev.vasilyespana.maps2uber.core.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Maps2UberApi {
    /** Short-link resolution lives server-side — the app never reimplements it. */
    @GET("api/resolve")
    suspend fun resolve(@Query("url") url: String): Response<ResponseBody>

    /**
     * Learning-loop intake: reports links the resolver couldn't handle so the
     * backend can improve its link-recognition scripts. Fire-and-forget from
     * the app's perspective — failures are swallowed by [FailureReportRepository].
     */
    @POST("api/resolve-failures")
    suspend fun reportFailure(@Body body: FailureReportDto): Response<ResponseBody>
}
