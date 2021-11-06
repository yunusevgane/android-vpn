package com.eskimobile.jetvpn.common.eventbus

import com.eskimobile.jetvpn.common.utils.Action.ACTION_REMOVE_SERVER_LIST_FRAGMENT
import com.eskimobile.jetvpn.common.utils.Action.ACTION_SHOULD_SHOW_BANNER

class ActionEvent(val action: String) {
    companion object {
        val removeSearchListFragment = ActionEvent(ACTION_REMOVE_SERVER_LIST_FRAGMENT)
        val shouldShowBanner = ActionEvent(ACTION_SHOULD_SHOW_BANNER)
    }
}