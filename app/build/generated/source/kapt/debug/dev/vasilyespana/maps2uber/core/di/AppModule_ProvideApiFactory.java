package dev.vasilyespana.maps2uber.core.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import dev.vasilyespana.maps2uber.core.network.Maps2UberApi;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class AppModule_ProvideApiFactory implements Factory<Maps2UberApi> {
  private final Provider<OkHttpClient> clientProvider;

  public AppModule_ProvideApiFactory(Provider<OkHttpClient> clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public Maps2UberApi get() {
    return provideApi(clientProvider.get());
  }

  public static AppModule_ProvideApiFactory create(
      javax.inject.Provider<OkHttpClient> clientProvider) {
    return new AppModule_ProvideApiFactory(Providers.asDaggerProvider(clientProvider));
  }

  public static AppModule_ProvideApiFactory create(Provider<OkHttpClient> clientProvider) {
    return new AppModule_ProvideApiFactory(clientProvider);
  }

  public static Maps2UberApi provideApi(OkHttpClient client) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideApi(client));
  }
}
