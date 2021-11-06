package com.eskimobile.jetvpn.presentation.premium

import android.app.Activity
import android.content.Context
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.domain.repos.BillingRepository
import com.eskimobile.jetvpn.domain.repos.ClientListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

class PremiumPresenter @Inject constructor() : BasePresenter<PremiumView>(), ClientListener {
    @Inject
    lateinit var contextApp: Context

    @Inject
    lateinit var billingRepository: BillingRepository

    private var skuDetailsList = listOf<SkuDetails>()

    override fun onCreate() {
        super.onCreate()
        billingRepository.startConnections(this)
    }

    fun getPremium(activity: Activity?, monthly: Boolean, action: Int) {
        if (skuDetailsList.isEmpty()) {
            return
        }
        val index = if (monthly) 0 else 1
        val skuDetails = skuDetailsList[index]

        var oldSku: SkuDetails? = null
        if (action != PremiumFragment.ACTION_GET) {
            oldSku = skuDetailsList.first { it.sku != skuDetails.sku }
        }

        billingRepository.makePurchase(activity, skuDetails, oldSku)
    }

    override fun onDestroy() {
        super.onDestroy()
        billingRepository.endConnections()
    }

    override fun skuDetailResult(skuDetailsList: List<SkuDetails>) {
        Timber.i("skuDetailResult: ${skuDetailsList.size}")
        this.skuDetailsList = skuDetailsList
        if(skuDetailsList.size == 2){
            val price1 = skuDetailsList[0].price
            val price2 = skuDetailsList[1].price
            val save = skuDetailsList[0].originalPriceAmountMicros * 100f/ skuDetailsList[1].originalPriceAmountMicros
            view?.updatePrice(price1, price2, save.roundToInt())
        }
    }

    override fun acknowledgedPurchase(purchase: Purchase?, isNewRequest: Boolean) {
        val index = this.skuDetailsList.indexOfFirst { skuDetails -> skuDetails.sku == purchase?.sku }
        val isPremium = if (index != -1) {
            view?.onAcknowledgedPurchase(index, isNewRequest)
            true
        } else {
            false
        }
        view?.shouldShowAdMod(isPremium)
    }
}