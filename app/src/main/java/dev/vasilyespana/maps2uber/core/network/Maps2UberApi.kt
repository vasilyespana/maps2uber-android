package dev.vasilyespana.maps2uber.core.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Maps2UberApi {
    /** Short-link resolution lives server-side — the app never reimplements it. */
    @GET("api/resolve")
    suspend fun resolve(@Query("url") url: String): Response<ResponseBody>
}
