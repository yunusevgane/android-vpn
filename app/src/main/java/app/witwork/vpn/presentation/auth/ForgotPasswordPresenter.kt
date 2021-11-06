package com.eskimobile.jetvpn.presentation.auth

import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import timber.log.Timber
import javax.inject.Inject

class ForgotPasswordPresenter @Inject constructor() : BasePresenter<ForgotPasswordView>() {
    @Inject
    lateinit var userRepository: UserRepository

    fun forgotPassword(email: String) {
        userRepository.forgotPassword(email)
            .compose(completableTransformer())
            .subscribe({
                view?.onForgotPasswordSuccess()
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

}