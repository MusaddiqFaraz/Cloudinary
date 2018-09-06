package com.faraz.app.imagecheck

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by root on 6/9/18.
 */
object RetrofitFactory {

    val retrofit: Retrofit? = null
    val BASE_URL = "http://res.cloudinary.com/"

    inline fun<reified T> getRetrofitClient(baseUrl : String) : T {



        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)

                // Logging Interceptor
                .addNetworkInterceptor(
                        HttpLoggingInterceptor(
                                HttpLoggingInterceptor.Logger {
                                    Log.i("APIINTERFACE", ": $it")
                                }
                        ).setLevel(HttpLoggingInterceptor.Level.BASIC))

                .build()

        return Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(T::class.java)

    }
}