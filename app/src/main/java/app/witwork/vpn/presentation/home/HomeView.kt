package com.eskimobile.jetvpn.presentation.home

import com.eskimobile.jetvpn.common.base.BaseView

interface HomeView : BaseView {
    fun onConnected()
    fun onDisconnected()
    fun onUpdateConnectionStatus(upload: String? = null, download: String? = null)
}