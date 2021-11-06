package com.eskimobile.jetvpn.common;

import android.app.Application
import com.eskimobile.jetvpn.BuildConfig
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.di.component.DaggerAppComponent
import com.eskimobile.jetvpn.common.utils.Feature
import com.google.android.gms.ads.MobileAds
import com.onesignal.OneSignal
import timber.log.Timber

class MyApp : Application() {
    companion object {
        lateinit var self: MyApp
            private set
    }

    val appComponent: AppComponent by lazy {
        return@lazy DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        self = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        if (Feature.FEATURE_ALLOW_ADMOB) {
            MobileAds.initialize(this)
        }

        initOneSignal()
    }

    private fun initOneSignal() {
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}