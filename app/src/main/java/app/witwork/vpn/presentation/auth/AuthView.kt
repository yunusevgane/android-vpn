package com.eskimobile.jetvpn.presentation.auth

import com.eskimobile.jetvpn.common.base.BaseView
import com.eskimobile.jetvpn.domain.model.User

interface AuthView : BaseView {
    fun onSignInSuccess(user: User?)
    fun onSignUpSuccess(user: User)
}