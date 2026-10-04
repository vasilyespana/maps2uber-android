package dev.vasilyespana.maps2uber.ui.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material3.ExperimentalMaterial3Api;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.vasilyespana.maps2uber.core.geo.UberLinks;
import dev.vasilyespana.maps2uber.core.settings.PickupMode;
import dev.vasilyespana.maps2uber.core.settings.PickupPreset;
import dev.vasilyespana.maps2uber.core.settings.SettingsStore;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0007\u00a8\u0006\u0006"}, d2 = {"SettingsScreen", "", "onBack", "Lkotlin/Function0;", "viewModel", "Ldev/vasilyespana/maps2uber/ui/settings/SettingsViewModel;", "app_release"})
public final class SettingsScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.ui.settings.SettingsViewModel viewModel) {
    }
}