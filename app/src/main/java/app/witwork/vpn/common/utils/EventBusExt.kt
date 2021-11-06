package com.eskimobile.jetvpn.common.utils

import org.greenrobot.eventbus.EventBus

fun Any.registerEventBus() {
    if (EventBus.getDefault().isRegistered(this))
        return
    EventBus.getDefault().register(this)
}

fun Any.unregisterEventBus() {
    if (EventBus.getDefault().isRegistered(this)) {
        EventBus.getDefault().unregister(this)
    }
}

fun Any.postEvent(event: Any){
    EventBus.getDefault().post(event)
}