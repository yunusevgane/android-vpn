package com.eskimobile.jetvpn.common.utils

import android.content.Context
import com.eskimobile.jetvpn.common.MyApp

private const val SharePref_DB = "myPref"

fun Any?.putStringPref(key: String, value: String) {
    MyApp.self.applicationContext?.getSharedPreferences(SharePref_DB, Context.MODE_PRIVATE)
        ?.edit()
        ?.putString(key, value)
        ?.apply()
}

fun Any?.getStringPref(key: String, default: String? = null): String? {
    return MyApp.self.applicationContext.getSharedPreferences(SharePref_DB, Context.MODE_PRIVATE)
        ?.getString(key, default)
}

fun Any?.putBooleanPref(key: String, value: Boolean) {
    MyApp.self.applicationContext?.getSharedPreferences(SharePref_DB, Context.MODE_PRIVATE)
        ?.edit()
        ?.putBoolean(key, value)
        ?.apply()
}

fun Any?.clearAllPref() {
    MyApp.self.applicationContext?.getSharedPreferences(SharePref_DB, Context.MODE_PRIVATE)
        ?.edit()
        ?.clear()
        ?.apply()
}

fun Any?.getBooleanPref(key: String): Boolean {
    return MyApp.self.applicationContext?.getSharedPreferences(SharePref_DB, Context.MODE_PRIVATE)
        ?.getBoolean(key, false) ?: false
}