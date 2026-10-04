package dev.vasilyespana.maps2uber;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.vasilyespana.maps2uber.core.network.ResolveRepository;
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<ResolveRepository> repositoryProvider;

  private final Provider<SettingsStore> settingsStoreProvider;

  public MainViewModel_Factory(Provider<ResolveRepository> repositoryProvider,
      Provider<SettingsStore> settingsStoreProvider) {
    this.repositoryProvider = repositoryProvider;
    this.settingsStoreProvider = settingsStoreProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(repositoryProvider.get(), settingsStoreProvider.get());
  }

  public static MainViewModel_Factory create(
      javax.inject.Provider<ResolveRepository> repositoryProvider,
      javax.inject.Provider<SettingsStore> settingsStoreProvider) {
    return new MainViewModel_Factory(Providers.asDaggerProvider(repositoryProvider), Providers.asDaggerProvider(settingsStoreProvider));
  }

  public static MainViewModel_Factory create(Provider<ResolveRepository> repositoryProvider,
      Provider<SettingsStore> settingsStoreProvider) {
    return new MainViewModel_Factory(repositoryProvider, settingsStoreProvider);
  }

  public static MainViewModel newInstance(ResolveRepository repository,
      SettingsStore settingsStore) {
    return new MainViewModel(repository, settingsStore);
  }
}
