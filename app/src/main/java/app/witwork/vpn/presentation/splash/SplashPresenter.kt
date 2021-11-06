package com.eskimobile.jetvpn.presentation.splash

import com.eskimobile.jetvpn.common.base.BasePresenter
import com.steve.utilities.core.extensions.addToCompositeDisposable
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashPresenter @Inject constructor() : BasePresenter<SplashView>() {
    fun sync() {
        Observable.just(true)
            .delay(500, TimeUnit.MILLISECONDS)
            .subscribe(this::handleSyncSuccess, this::handleError)
            .addToCompositeDisposable(disposable)
    }

    private fun handleSyncSuccess(boolean: Boolean) {
        view?.goToMainScreen()
    }
}