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

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011J\u000e\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u0015R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u00a8\u0006\u0016"}, d2 = {"Ldev/vasilyespana/maps2uber/ui/settings/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "store", "Ldev/vasilyespana/maps2uber/core/settings/SettingsStore;", "(Ldev/vasilyespana/maps2uber/core/settings/SettingsStore;)V", "settings", "Lkotlinx/coroutines/flow/Flow;", "Ldev/vasilyespana/maps2uber/core/settings/AppSettings;", "getSettings", "()Lkotlinx/coroutines/flow/Flow;", "getStore", "()Ldev/vasilyespana/maps2uber/core/settings/SettingsStore;", "savePreset", "Lkotlinx/coroutines/Job;", "name", "", "lat", "", "lng", "setMode", "mode", "Ldev/vasilyespana/maps2uber/core/settings/PickupMode;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final dev.vasilyespana.maps2uber.core.settings.SettingsStore store = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<dev.vasilyespana.maps2uber.core.settings.AppSettings> settings = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.core.settings.SettingsStore store) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final dev.vasilyespana.maps2uber.core.settings.SettingsStore getStore() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<dev.vasilyespana.maps2uber.core.settings.AppSettings> getSettings() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job setMode(@org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.core.settings.PickupMode mode) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job savePreset(@org.jetbrains.annotations.NotNull()
    java.lang.String name, double lat, double lng) {
        return null;
    }
}