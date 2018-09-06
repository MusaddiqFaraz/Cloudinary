package com.faraz.app.imagecheck.di

import android.content.Context
import com.cloudinary.android.MediaManager
import com.faraz.app.imagecheck.ApiInterface
import com.faraz.app.imagecheck.RetrofitFactory
import com.faraz.app.imagecheck.RetrofitFactory.getRetrofitClient
import com.faraz.app.imagecheck.data.AppRxSchedulers
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by root on 6/9/18.
 */
@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun providesAppContext() = context

    @Provides
    @Singleton
    fun getNetworkModule() : ApiInterface = getRetrofitClient(RetrofitFactory.BASE_URL)

    @Provides
    @Singleton
    fun getAppRxSchedulers() : AppRxSchedulers = AppRxSchedulers(Schedulers.io(),Schedulers.computation(),
            AndroidSchedulers.mainThread())

}