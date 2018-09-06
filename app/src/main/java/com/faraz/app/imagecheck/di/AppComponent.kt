package com.faraz.app.imagecheck.di

import com.faraz.app.imagecheck.ImageCheck
import com.faraz.app.imagecheck.MainVm
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by root on 6/9/18.
 */
@Singleton
@Component(modules = [AppModule::class,AndroidSupportInjectionModule::class,ActivityModule::class])
interface AppComponent {

    val mainVm:MainVm
    fun inject(imageCheck: ImageCheck)
}