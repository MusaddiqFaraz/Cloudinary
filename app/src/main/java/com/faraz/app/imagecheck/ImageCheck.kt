package com.faraz.app.imagecheck

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.v4.app.Fragment
import com.cloudinary.android.MediaManager
import com.faraz.app.imagecheck.di.AppComponent
import com.faraz.app.imagecheck.di.AppModule
import com.faraz.app.imagecheck.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by root on 6/9/18.
 */
class ImageCheck: Application() , HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @set:VisibleForTesting
    lateinit var component: AppComponent

    override fun activityInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()


        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)



        MediaManager.init(this)

    }
}

val Context.component: AppComponent
    get() = (applicationContext as ImageCheck).component

val Fragment.component: AppComponent
    get() = activity!!.component