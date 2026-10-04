package dev.vasilyespana.maps2uber.core.network

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResolveRepository @Inject constructor(
    private val api: Maps2UberApi,
) {
    suspend fun resolve(url: String): ResolveResult {
        return try {
            val res = api.resolve(url)
            if (!res.isSuccessful) {
                ResolveResult.Err("http-${res.code()}")
            } else {
                ResolveParser.parse(res.body()?.string().orEmpty())
            }
        } catch (e: Exception) {
            ResolveResult.Err("network-error")
        }
    }
}
