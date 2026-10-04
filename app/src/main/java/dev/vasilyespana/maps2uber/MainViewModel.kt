package dev.vasilyespana.maps2uber

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.vasilyespana.maps2uber.core.geo.Pickup
import dev.vasilyespana.maps2uber.core.geo.UberLinks
import dev.vasilyespana.maps2uber.core.network.FailureReportRepository
import dev.vasilyespana.maps2uber.core.network.ResolveRepository
import dev.vasilyespana.maps2uber.core.network.ResolveResult
import dev.vasilyespana.maps2uber.core.settings.AppSettings
import dev.vasilyespana.maps2uber.core.settings.PickupMode
import dev.vasilyespana.maps2uber.core.settings.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProbeLink(
    val bearingDeg: Double,
    val lat: Double,
    val lng: Double,
    val uberUrl: String,
)

data class ResolvedPage(
    val lat: Double,
    val lng: Double,
    val name: String,
    val address: String,
    val mainUberUrl: String,
    val probes: List<ProbeLink>,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ResolveRepository,
    private val failureReports: FailureReportRepository,
    private val settingsStore: SettingsStore,
) : ViewModel() {

    sealed interface FlowState {
        data object Idle : FlowState
        data class Resolving(val input: String) : FlowState
        data class Ready(val page: ResolvedPage) : FlowState
        data class Failed(val message: String) : FlowState
    }

    private val _flow = MutableStateFlow<FlowState>(FlowState.Idle)
    val flow: StateFlow<FlowState> = _flow.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    /** Cold start with a shared / tapped maps link: skip Home, go straight to Resolving. */
    fun startWithUrl(url: String) {
        _flow.value = FlowState.Resolving(url)
        viewModelScope.launch {
            when (val r = repository.resolve(url)) {
                is ResolveResult.Ok -> _flow.value = FlowState.Ready(buildPage(r.lat, r.lng, r.name, r.address))
                is ResolveResult.Err -> {
                    // Learning-loop intake: report the unrecognized link in its
                    // own coroutine so it can never delay or break the UI.
                    launch { failureReports.report(url, r.error) }
                    _flow.value = FlowState.Failed(
                        "Couldn't resolve that link (${r.error}). Try pasting another link or enter coordinates manually.",
                    )
                }
            }
        }
    }

    /** Manual lat/lng fallback when resolution fails. Validates ranges. */
    fun startManual(lat: Double, lng: Double): Boolean {
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) return false
        val label = "Custom point (${UberLinks.formatCoord(lat)}, ${UberLinks.formatCoord(lng)})"
        _flow.value = FlowState.Ready(buildPage(lat, lng, label, label))
        return true
    }

    fun backToHome() {
        _flow.value = FlowState.Idle
    }

    private fun currentPickup(): Pickup? {
        val s = settings.value
        val preset = s.activePreset
        return if (s.pickupMode == PickupMode.PRESET &&
            preset != null &&
            preset.name.isNotBlank() &&
            preset.lat in -90.0..90.0 && preset.lng in -180.0..180.0 &&
            (preset.lat != 0.0 || preset.lng != 0.0)
        ) {
            Pickup(preset.lat, preset.lng, preset.name)
        } else {
            // Current location: omit pickup params entirely — Uber uses the rider's location.
            null
        }
    }

    private fun buildPage(lat: Double, lng: Double, name: String, address: String): ResolvedPage {
        val pickup = currentPickup()
        return ResolvedPage(
            lat = lat,
            lng = lng,
            name = name,
            address = address,
            mainUberUrl = UberLinks.uberLink(lat, lng, name, address, pickup),
            probes = UberLinks.probePoints(lat, lng).map { p ->
                ProbeLink(
                    bearingDeg = p.bearingDeg,
                    lat = p.lat,
                    lng = p.lng,
                    uberUrl = UberLinks.uberLink(p.lat, p.lng, name, address, pickup),
                )
            },
        )
    }
}
