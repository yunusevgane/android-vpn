package com.eskimobile.jetvpn.common.eventbus

data class ChangeTabEvent(val tabIndex: Int){
    companion object{
        val premium = ChangeTabEvent(1)
    }
}