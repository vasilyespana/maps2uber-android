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

/**
 * Click-event vocabulary for POST /api/click-events (worker contract).
 *
 * Vasily directive 2026-10-12: track ALL clicks, not just submitted
 * links — above all which provider people choose (on the circle vs the
 * table, Uber vs Lyft), primary vs the second-provider comparison link,
 * copy/share. Events carry the provider, the surface they happened on,
 * and the submitted maps link they belong to (when known), so the
 * weekly report can join clicks to submissions. No user identity and
 * no device id exist anywhere in this payload.
 */
object ClickEvents {
    const val PROVIDER_OPEN_PRIMARY = "provider_open_primary"
    const val PROVIDER_OPEN_SECOND = "provider_open_second"
    const val COPY_LINK = "copy_link"
    const val SHARE = "share"
    const val SETTINGS_DEFAULT_PROVIDER_CHANGE = "settings_default_provider_change"
    const val GENERIC_BUTTON_CLICK = "generic_button_click"

    const val SURFACE_CIRCLE = "circle"
    const val SURFACE_TABLE = "table"
    const val SURFACE_BUTTON = "button"
    const val SURFACE_SETTINGS = "settings"
    const val SURFACE_FALLBACK = "fallback"

    const val POSITION_PRIMARY = "primary"
    const val POSITION_SECOND = "second"
    const val POSITION_PROBE = "probe"
}

/** One tap, as emitted by UI code. [url] = the submitted maps link, when known. */
data class ClickEvent(
    val event: String,
    val providerId: String? = null,
    val surface: String,
    val position: String? = null,
    val url: String? = null,
    val buttonId: String? = null,
) {
    /** Wire shape for POST /api/click-events. Single mapping point, unit-tested. */
    fun toDto(appVersion: String?): ClickEventDto = ClickEventDto(
        event = event,
        provider = providerId,
        surface = surface,
        position = position,
        url = url,
        appVersion = appVersion,
        buttonId = buttonId,
    )
}

data class ClickEventDto(
    @SerializedName("event") val event: String,
    @SerializedName("provider") val provider: String? = null,
    @SerializedName("surface") val surface: String,
    @SerializedName("position") val position: String? = null,
    @SerializedName("url") val url: String? = null,
    @SerializedName("platform") val platform: String = "android",
    @SerializedName("app_version") val appVersion: String? = null,
    @SerializedName("button_id") val buttonId: String? = null,
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
