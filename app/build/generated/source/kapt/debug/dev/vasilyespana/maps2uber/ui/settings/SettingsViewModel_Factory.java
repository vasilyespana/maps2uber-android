package dev.vasilyespana.maps2uber.ui.settings;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.vasilyespana.maps2uber.core.settings.SettingsStore;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SettingsStore> storeProvider;

  public SettingsViewModel_Factory(Provider<SettingsStore> storeProvider) {
    this.storeProvider = storeProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(storeProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<SettingsStore> storeProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(storeProvider));
  }

  public static SettingsViewModel_Factory create(Provider<SettingsStore> storeProvider) {
    return new SettingsViewModel_Factory(storeProvider);
  }

  public static SettingsViewModel newInstance(SettingsStore store) {
    return new SettingsViewModel(store);
  }
}
