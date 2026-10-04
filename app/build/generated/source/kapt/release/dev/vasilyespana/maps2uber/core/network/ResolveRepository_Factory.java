package dev.vasilyespana.maps2uber.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ResolveRepository_Factory implements Factory<ResolveRepository> {
  private final Provider<Maps2UberApi> apiProvider;

  public ResolveRepository_Factory(Provider<Maps2UberApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public ResolveRepository get() {
    return newInstance(apiProvider.get());
  }

  public static ResolveRepository_Factory create(javax.inject.Provider<Maps2UberApi> apiProvider) {
    return new ResolveRepository_Factory(Providers.asDaggerProvider(apiProvider));
  }

  public static ResolveRepository_Factory create(Provider<Maps2UberApi> apiProvider) {
    return new ResolveRepository_Factory(apiProvider);
  }

  public static ResolveRepository newInstance(Maps2UberApi api) {
    return new ResolveRepository(api);
  }
}
