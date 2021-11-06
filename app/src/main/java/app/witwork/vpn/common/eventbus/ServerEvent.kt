package com.eskimobile.jetvpn.common.eventbus

import com.eskimobile.jetvpn.domain.model.Server

data class ServerEvent private constructor(val value: Server?, val change: Boolean) {
    companion object {
        fun init(value: Server?, change: Boolean) = ServerEvent(value, change)
    }
}