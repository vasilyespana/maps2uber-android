package dev.vasilyespana.maps2uber.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.vasilyespana.maps2uber.core.network.Maps2UberApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient): Maps2UberApi =
        Retrofit.Builder()
            .baseUrl("https://maps2uber.vasilyespana.workers.dev/")
            .client(client)
            // No converter: the repository reads the raw body and routes it
            // through ResolveParser (the single, unit-tested mapping point).
            .build()
            .create(Maps2UberApi::class.java)
}
