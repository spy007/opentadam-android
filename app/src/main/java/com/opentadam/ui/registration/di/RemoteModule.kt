package com.contactskotlin.data.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.opentadam.BuildConfig
import com.opentadam.ui.registration.api.RemoteService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class RemoteModule {

    @Provides
    @Singleton
    fun provideGson(): Gson =
            GsonBuilder()
                    .setLenient()
                    .create()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BuildConfig.REST_SERVER[0])
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        addInterceptor { chain ->
            val original = chain.request()
                    .newBuilder()
                    .addHeader("Hive-Profile", BuildConfig.HiveProfile).build()

            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder().build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                    .url(url)

            val request = requestBuilder.build()
            chain.proceed(request)
        }

    }.build()

    @Provides
    @Singleton
    fun provideRemoteContactsService(retrofit: Retrofit): RemoteService =
            retrofit.create(RemoteService::class.java)
}