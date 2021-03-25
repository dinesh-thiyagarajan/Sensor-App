package com.dineshworkspace.sensorapp.network

import com.dineshworkspace.sensorapp.AppConstants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @[Provides Singleton]
    fun providesBaseSocket(): Socket {
        val uri = URI.create(AppConstants.BASE_URL)
        val options = IO.Options.builder().build()
        return IO.socket(uri, options)
    }

    var gson = GsonBuilder()
        .setLenient()
        .create()

    @[Provides Singleton]
    fun providesRetrofit(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        httpClient.callTimeout(1, TimeUnit.MINUTES)
        httpClient.connectTimeout(1, TimeUnit.MINUTES)

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(AppConstants.BASE_URL)
            .client(httpClient.build())
            .build()
    }

    @[Provides Singleton]
    fun providesApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @[Provides Singleton]
    fun providesApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

}