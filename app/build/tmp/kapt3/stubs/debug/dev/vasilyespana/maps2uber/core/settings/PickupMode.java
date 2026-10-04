package dev.vasilyespana.maps2uber.core.settings;

import android.content.Context;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.Flow;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Pickup mode for the Uber links: rider's live location, or a saved preset.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2 = {"Ldev/vasilyespana/maps2uber/core/settings/PickupMode;", "", "(Ljava/lang/String;I)V", "CURRENT_LOCATION", "PRESET", "app_debug"})
public enum PickupMode {
    /*public static final*/ CURRENT_LOCATION /* = new CURRENT_LOCATION() */,
    /*public static final*/ PRESET /* = new PRESET() */;
    
    PickupMode() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<dev.vasilyespana.maps2uber.core.settings.PickupMode> getEntries() {
        return null;
    }
}