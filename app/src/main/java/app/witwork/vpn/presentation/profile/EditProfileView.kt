package com.eskimobile.jetvpn.presentation.profile

import com.eskimobile.jetvpn.common.base.BaseView
import com.eskimobile.jetvpn.domain.model.User

interface EditProfileView : BaseView {
    fun onUpgradeSuccess()
    fun onLogoutSuccess()
    fun onGetCurrentUserSuccess(user: User?)
}