package dev.vasilyespana.maps2uber.ui;

import androidx.compose.runtime.Composable;
import dev.vasilyespana.maps2uber.MainViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u00a8\u0006\u0004"}, d2 = {"NavGraph", "", "viewModel", "Ldev/vasilyespana/maps2uber/MainViewModel;", "app_debug"})
public final class NavGraphKt {
    
    /**
     * The flow state (Idle / Resolving / Ready / Failed) is the single source of
     * truth: a cold start carrying a shared or tapped maps link renders Resolving
     * immediately — Home is skipped, never flashed.
     */
    @androidx.compose.runtime.Composable()
    public static final void NavGraph(@org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.MainViewModel viewModel) {
    }
}