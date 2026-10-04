package dev.vasilyespana.maps2uber;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dev.vasilyespana.maps2uber.core.geo.Pickup;
import dev.vasilyespana.maps2uber.core.geo.UberLinks;
import dev.vasilyespana.maps2uber.core.network.ResolveRepository;
import dev.vasilyespana.maps2uber.core.network.ResolveResult;
import dev.vasilyespana.maps2uber.core.settings.AppSettings;
import dev.vasilyespana.maps2uber.core.settings.PickupMode;
import dev.vasilyespana.maps2uber.core.settings.SettingsStore;
import kotlinx.coroutines.flow.SharingStarted;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0001!B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0011\u001a\u00020\u0012J(\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0019H\u0002J\n\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0002J\u0016\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016J\u000e\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u0019R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Ldev/vasilyespana/maps2uber/core/network/ResolveRepository;", "settingsStore", "Ldev/vasilyespana/maps2uber/core/settings/SettingsStore;", "(Ldev/vasilyespana/maps2uber/core/network/ResolveRepository;Ldev/vasilyespana/maps2uber/core/settings/SettingsStore;)V", "_flow", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "flow", "Lkotlinx/coroutines/flow/StateFlow;", "getFlow", "()Lkotlinx/coroutines/flow/StateFlow;", "settings", "Ldev/vasilyespana/maps2uber/core/settings/AppSettings;", "getSettings", "backToHome", "", "buildPage", "Ldev/vasilyespana/maps2uber/ResolvedPage;", "lat", "", "lng", "name", "", "address", "currentPickup", "Ldev/vasilyespana/maps2uber/core/geo/Pickup;", "startManual", "", "startWithUrl", "url", "FlowState", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class MainViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final dev.vasilyespana.maps2uber.core.network.ResolveRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final dev.vasilyespana.maps2uber.core.settings.SettingsStore settingsStore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<dev.vasilyespana.maps2uber.MainViewModel.FlowState> _flow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<dev.vasilyespana.maps2uber.MainViewModel.FlowState> flow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<dev.vasilyespana.maps2uber.core.settings.AppSettings> settings = null;
    
    @javax.inject.Inject()
    public MainViewModel(@org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.core.network.ResolveRepository repository, @org.jetbrains.annotations.NotNull()
    dev.vasilyespana.maps2uber.core.settings.SettingsStore settingsStore) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<dev.vasilyespana.maps2uber.MainViewModel.FlowState> getFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<dev.vasilyespana.maps2uber.core.settings.AppSettings> getSettings() {
        return null;
    }
    
    /**
     * Cold start with a shared / tapped maps link: skip Home, go straight to Resolving.
     */
    public final void startWithUrl(@org.jetbrains.annotations.NotNull()
    java.lang.String url) {
    }
    
    /**
     * Manual lat/lng fallback when resolution fails. Validates ranges.
     */
    public final boolean startManual(double lat, double lng) {
        return false;
    }
    
    public final void backToHome() {
    }
    
    private final dev.vasilyespana.maps2uber.core.geo.Pickup currentPickup() {
        return null;
    }
    
    private final dev.vasilyespana.maps2uber.ResolvedPage buildPage(double lat, double lng, java.lang.String name, java.lang.String address) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u00002\u00020\u0001:\u0004\u0002\u0003\u0004\u0005\u0082\u0001\u0004\u0006\u0007\b\t\u00a8\u0006\n"}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "", "Failed", "Idle", "Ready", "Resolving", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Failed;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Idle;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Ready;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Resolving;", "app_debug"})
    public static abstract interface FlowState {
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Failed;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Failed implements dev.vasilyespana.maps2uber.MainViewModel.FlowState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Failed(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                super();
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final dev.vasilyespana.maps2uber.MainViewModel.FlowState.Failed copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c7\n\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0013\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00d6\u0003J\t\u0010\u0007\u001a\u00020\bH\u00d6\u0001J\t\u0010\t\u001a\u00020\nH\u00d6\u0001\u00a8\u0006\u000b"}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Idle;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Idle implements dev.vasilyespana.maps2uber.MainViewModel.FlowState {
            @org.jetbrains.annotations.NotNull()
            public static final dev.vasilyespana.maps2uber.MainViewModel.FlowState.Idle INSTANCE = null;
            
            private Idle() {
                super();
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0010H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0011"}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Ready;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "page", "Ldev/vasilyespana/maps2uber/ResolvedPage;", "(Ldev/vasilyespana/maps2uber/ResolvedPage;)V", "getPage", "()Ldev/vasilyespana/maps2uber/ResolvedPage;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
        public static final class Ready implements dev.vasilyespana.maps2uber.MainViewModel.FlowState {
            @org.jetbrains.annotations.NotNull()
            private final dev.vasilyespana.maps2uber.ResolvedPage page = null;
            
            public Ready(@org.jetbrains.annotations.NotNull()
            dev.vasilyespana.maps2uber.ResolvedPage page) {
                super();
            }
            
            @org.jetbrains.annotations.NotNull()
            public final dev.vasilyespana.maps2uber.ResolvedPage getPage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final dev.vasilyespana.maps2uber.ResolvedPage component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final dev.vasilyespana.maps2uber.MainViewModel.FlowState.Ready copy(@org.jetbrains.annotations.NotNull()
            dev.vasilyespana.maps2uber.ResolvedPage page) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Ldev/vasilyespana/maps2uber/MainViewModel$FlowState$Resolving;", "Ldev/vasilyespana/maps2uber/MainViewModel$FlowState;", "input", "", "(Ljava/lang/String;)V", "getInput", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Resolving implements dev.vasilyespana.maps2uber.MainViewModel.FlowState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String input = null;
            
            public Resolving(@org.jetbrains.annotations.NotNull()
            java.lang.String input) {
                super();
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getInput() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final dev.vasilyespana.maps2uber.MainViewModel.FlowState.Resolving copy(@org.jetbrains.annotations.NotNull()
            java.lang.String input) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
    }
}