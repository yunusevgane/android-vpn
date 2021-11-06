package com.eskimobile.jetvpn.presentation.main

import com.eskimobile.jetvpn.common.base.BaseView
import com.eskimobile.jetvpn.domain.model.AdsConfig

interface MainView : BaseView {
    fun onGetConfigsSuccess(adsConfig: AdsConfig?)
}