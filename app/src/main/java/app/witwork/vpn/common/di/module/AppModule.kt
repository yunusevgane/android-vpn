package com.eskimobile.jetvpn.common.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    fun provideContextApp(application: Application): Context = application.applicationContext

}