package com.eskimobile.jetvpn.presentation.main

import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.domain.repos.ConfigRepository
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import com.steve.utilities.core.extensions.observableTransformer
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor() : BasePresenter<MainView>() {
    @Inject
    lateinit var configRepository: ConfigRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun getConfigs() {
        configRepository.getConfig()
            .compose(observableTransformer())
            .subscribe({
                view?.onGetConfigsSuccess(it)
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

    fun syncLogin() {
        userRepository.syncUserLogin()
            .compose(completableTransformer())
            .subscribe({},Timber::e)
            .addToCompositeDisposable(disposable)
    }
}