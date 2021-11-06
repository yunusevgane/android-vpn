package com.eskimobile.jetvpn.presentation.premium

import com.eskimobile.jetvpn.common.base.BaseView

interface PremiumView : BaseView {
    fun onAcknowledgedPurchase(index: Int, newRequest: Boolean)
    fun shouldShowAdMod(premium: Boolean)
    fun updatePrice(price1: String?, price2: String?, save: Int)
}