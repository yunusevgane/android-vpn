package com.eskimobile.jetvpn.presentation.auth

import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.utils.completableTransformer
import com.eskimobile.jetvpn.common.utils.observableTransformer
import com.eskimobile.jetvpn.domain.model.User
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class AuthPresenter @Inject constructor() : BasePresenter<AuthView>() {
    @Inject
    lateinit var userRepository: UserRepository

    fun signIn(email: String, password: String) {
        userRepository.signIn(email, password)
            .compose(observableTransformer(view))
            .subscribe({
                view?.onSignInSuccess(it)
            }, this::handleError)
            .addToCompositeDisposable(disposable)
    }

    fun signUp(email: String, password: String) {
        userRepository.signUp(email, password)
            .compose(completableTransformer(view))
            .subscribe({
                Timber.e("sigunUp successfully")
                val user = User(email, password)
                view?.onSignUpSuccess(user)
            }, this::handleError)
            .addToCompositeDisposable(disposable)
    }

}