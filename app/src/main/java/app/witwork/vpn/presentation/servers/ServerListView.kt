package com.eskimobile.jetvpn.presentation.servers

import com.eskimobile.jetvpn.common.base.BaseView
import com.eskimobile.jetvpn.domain.model.Server

interface ServerListView : BaseView {
    fun onGetServersSuccess(servers: List<Server>?)
}