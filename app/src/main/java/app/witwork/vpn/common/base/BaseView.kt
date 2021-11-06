package com.eskimobile.jetvpn.common.base

interface BaseView {
    fun showProgressDialog(isShow: Boolean)
    fun showError(throwable: Throwable)
}