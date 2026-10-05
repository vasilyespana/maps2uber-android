package dev.vasilyespana.maps2uber.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.vasilyespana.maps2uber.MainViewModel
import dev.vasilyespana.maps2uber.core.history.HistoryEntry
import dev.vasilyespana.maps2uber.core.history.HistoryKind
import dev.vasilyespana.maps2uber.ui.home.HomeScreen
import dev.vasilyespana.maps2uber.ui.resolving.ResolvingScreen
import dev.vasilyespana.maps2uber.ui.results.ResultsScreen
import dev.vasilyespana.maps2uber.ui.settings.SettingsScreen

/**
 * The flow state (Idle / Resolving / Ready / Failed) is the single source of
 * truth: a cold start carrying a shared or tapped maps link renders Resolving
 * immediately — Home is skipped, never flashed.
 */
@Composable
fun NavGraph(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val flow by viewModel.flow.collectAsState()
            val history by viewModel.history.collectAsState()
            BackHandler(enabled = flow !is MainViewModel.FlowState.Idle) {
                viewModel.backToHome()
            }
            val onSelectHistory: (HistoryEntry) -> Unit = { entry ->
                when (entry.kind) {
                    HistoryKind.LINK -> viewModel.startWithUrl(entry.input)
                    HistoryKind.COORDS -> {
                        val coords = entry.input.split(",").mapNotNull { it.toDoubleOrNull() }
                        if (coords.size == 2) viewModel.startManual(coords[0], coords[1])
                    }
                }
            }
            when (val f = flow) {
                is MainViewModel.FlowState.Idle ->
                    HomeScreen(
                        onGenerateLink = { viewModel.startWithUrl(it) },
                        onGenerateManual = { lat, lng -> viewModel.startManual(lat, lng) },
                        onOpenSettings = { navController.navigate("settings") },
                        error = null,
                        history = history,
                        onSelectHistory = onSelectHistory,
                        onClearHistory = { viewModel.clearHistory() },
                    )
                is MainViewModel.FlowState.Resolving -> ResolvingScreen(input = f.input)
                is MainViewModel.FlowState.Ready -> ResultsScreen(
                    page = f.page,
                    onUberLinkClick = { deepLink, linkType, probeBearing ->
                        viewModel.reportDeepLinkClick(deepLink, linkType, probeBearing)
                    },
                )
                is MainViewModel.FlowState.Failed ->
                    HomeScreen(
                        onGenerateLink = { viewModel.startWithUrl(it) },
                        onGenerateManual = { lat, lng -> viewModel.startManual(lat, lng) },
                        onOpenSettings = { navController.navigate("settings") },
                        error = f.message,
                        history = history,
                        onSelectHistory = onSelectHistory,
                        onClearHistory = { viewModel.clearHistory() },
                    )
            }
        }
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
