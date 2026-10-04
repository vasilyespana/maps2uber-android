package dev.vasilyespana.maps2uber.core.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Raw shape of GET https://maps2uber.vasilyespana.workers.dev/api/resolve?url=…
 * Verified live: {"ok":true,"lat":..,"lng":..,"name":"..","address":"..",
 * "final_url":"..","geocoded":false} or {"ok":false,"error":"..","name":".."}.
 */
data class ResolveDto(
    @SerializedName("ok") val ok: Boolean = false,
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lng") val lng: Double? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("address") val address: String? = null,
    @SerializedName("error") val error: String? = null,
)

sealed interface ResolveResult {
    data class Ok(
        val lat: Double,
        val lng: Double,
        val name: String,
        val address: String,
    ) : ResolveResult

    data class Err(val error: String, val name: String? = null) : ResolveResult
}

/**
 * Learning-loop intake payload for POST /api/resolve-failures.
 * Lets the backend improve its link-recognition scripts over time.
 */
data class FailureReportDto(
    @SerializedName("url") val url: String,
    @SerializedName("error") val error: String,
    @SerializedName("source") val source: String = "android",
)

/** Single mapping point: raw JSON -> ResolveResult. Unit-tested. */
object ResolveParser {
    private val gson = Gson()

    fun parse(json: String): ResolveResult {
        return try {
            val dto = gson.fromJson(json, ResolveDto::class.java)
                ?: return ResolveResult.Err("bad-response")
            if (dto.ok && dto.lat != null && dto.lng != null) {
                ResolveResult.Ok(
                    lat = dto.lat,
                    lng = dto.lng,
                    name = dto.name.orEmpty(),
                    address = dto.address.orEmpty(),
                )
            } else {
                ResolveResult.Err(dto.error ?: "bad-response", dto.name)
            }
        } catch (e: Exception) {
            ResolveResult.Err("bad-response")
        }
    }
}
