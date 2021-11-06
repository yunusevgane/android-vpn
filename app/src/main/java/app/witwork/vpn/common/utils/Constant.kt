package com.eskimobile.jetvpn.common.utils

object FirebaseConstant {
    const val SERVERS = "Servers"
    const val USERS = "users"
    const val ERROR = "errors"
    const val ANONYMOUS = "anonymous"
    const val CONFIGS = "configs"
}

object ProductSku {
    const val GOLD_MONTHLY = "gold_monthly"
    const val GOLD_YEARLY = "gold_yearly"
    val SUBS_SKUS = listOf(GOLD_MONTHLY, GOLD_YEARLY)
}

object Feature {
    const val FEATURE_ALLOW_ADMOB = true
    const val FEATURE_SHOW_ACTION_FROM_NOTIFICATION = true
    const val FEATURE_CANCEL_SUBSCRIPTION = false
    const val IS_ADMIN = false
    const val FEATURE_SYNC_ERROR = false;
}

object SharePrefs {
    const val KEY_USER = "User"
    const val KEY_SERVER = "Server"
    const val KEY_PREMIUM = "KEY_PREMIUM"
    const val KEY_SHOULD_SHOW_BANNER = "KEY_SHOULD_SHOW_BANNER"
}

object Action {
    const val ACTION_REMOVE_SERVER_LIST_FRAGMENT = "ACTION_REMOVE_SERVER_LIST_FRAGMENT"
    const val ACTION_SHOULD_SHOW_BANNER = "ACTION_SHOULD_SHOW_BANNER"
}

enum class AnonymousType {
    LAUNCH, PURCHASE, TRAFFIC
}