package com.eskimobile.jetvpn.domain.repos

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails

interface BillingRepository {
    fun startConnections(clientListener: ClientListener)

    fun endConnections()

    fun makePurchase(activity: Activity?, skuDetails: SkuDetails, skuDetailsList: SkuDetails?)

    fun querySkuDetailsAsync()

}

interface ClientListener {
    fun skuDetailResult(skuDetailsList: List<SkuDetails>)

    fun acknowledgedPurchase(purchase: Purchase?, isNewRequest: Boolean = false)
}
