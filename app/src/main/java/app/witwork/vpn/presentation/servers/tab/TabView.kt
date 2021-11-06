package com.eskimobile.jetvpn.presentation.servers.tab

import com.eskimobile.jetvpn.common.base.BaseView

interface TabView : BaseView {
    fun onLoadDataSuccess(index: Int)
}