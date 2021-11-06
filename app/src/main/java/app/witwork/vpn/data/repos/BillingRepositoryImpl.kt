package com.eskimobile.jetvpn.data.repos

import android.app.Activity
import android.content.Context
import androidx.annotation.WorkerThread
import com.eskimobile.jetvpn.common.utils.ProductSku
import com.eskimobile.jetvpn.domain.repos.BillingRepository
import com.eskimobile.jetvpn.domain.repos.ClientListener
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.android.billingclient.api.*
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.observableTransformer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor() : BillingRepository, PurchasesUpdatedListener, BillingClientStateListener {
    @Inject
    lateinit var contextApp: Context

    @Inject
    lateinit var userRepository: UserRepository

    private val compositeDisposable = CompositeDisposable()
    private lateinit var playStoreBillingClient: BillingClient

    private var clientListener: ClientListener? = null
    private val TRIGGER = Object()

    override fun startConnections(clientListener: ClientListener) {
        playStoreBillingClient = BillingClient.newBuilder(contextApp)
            .enablePendingPurchases() // required or app will crash
            .setListener(this)
            .build()
        connect()
        this.clientListener = clientListener
    }

    override fun endConnections() {
        playStoreBillingClient.endConnection()
        compositeDisposable.dispose()
    }

    override fun makePurchase(activity: Activity?, skuDetails: SkuDetails, oldSkuDetails: SkuDetails?) {
        Observable
            .fromCallable {
                val flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                if (oldSkuDetails != null) {
                    val purchaseToken = userRepository.get().blockingFirst().purchaseToken
                    if (purchaseToken != null) {
                        Timber.i("========> makePurchase: upgrade or downgrade")
                        flowParams.setOldSku(oldSkuDetails.sku, purchaseToken)
                    }
                }
                return@fromCallable flowParams
            }
            .compose(observableTransformer())
            .subscribe({ flowParams ->
                activity?.let {
                    playStoreBillingClient.launchBillingFlow(it, flowParams.build())
                }
            }, Timber::e)
            .addToCompositeDisposable(compositeDisposable)
    }


    override fun querySkuDetailsAsync() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(ProductSku.SUBS_SKUS)
            .setType(BillingClient.SkuType.SUBS)
            .build()

        playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    skuDetailsList?.let {
                        clientListener?.skuDetailResult(it)
                        queryPurchasesAsync()
                    }
                }
            }
        }
    }

    //------------------------------PurchasesUpdatedListener----------------------------

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // will handle server verification, consumables, and updating the local cache
                purchases?.apply { processPurchases(this, true) }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // item already owned? call queryPurchasesAsync to verify and process all such items
                Timber.d(billingResult.debugMessage)
                queryPurchasesAsync()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> connect()
            else -> Timber.d(billingResult.debugMessage)
        }
    }

    //------------------------------BillingClientStateListener----------------------------
    override fun onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected")
        connect()
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                Timber.d("onBillingSetupFinished successfully")
                querySkuDetailsAsync()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                //Some apps may choose to make decisions based on this knowledge.
                Timber.d(billingResult.debugMessage)
            }
            else -> {
                //do nothing. Someone else will connect it through retry policy.
                //May choose to send to server though
                Timber.d(billingResult.debugMessage)
            }
        }
    }

    //------------------------------Private method----------------------------

    private fun connect(): Boolean {
        if (playStoreBillingClient.isReady) {
            return false
        }
        Timber.d("connecting...")
        playStoreBillingClient.startConnection(this)
        return true
    }

    private fun queryPurchasesAsync() {
        if (isSubscriptionSupported()) {
            playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)
                ?.purchasesList?.apply { processPurchases(this) }
        }
    }

    private fun processPurchases(purchasesResult: MutableList<Purchase>, isNewRequest: Boolean = false) {
        Observable
            .fromCallable { return@fromCallable purchasesResult }
            .flatMap(this::acknowledgeNonConsumablePurchasesAsync)
            .flatMap(this::getPurchase)
            .flatMap(this::syncPurchase)
            .compose(observableTransformer())
            .subscribe({ clientListener?.acknowledgedPurchase(purchase = it.firstOrNull(), isNewRequest = isNewRequest) }, Timber::e)
            .addToCompositeDisposable(compositeDisposable)
    }

    @WorkerThread
    private fun acknowledgeNonConsumablePurchasesAsync(purchasesResult: MutableList<Purchase>): Observable<Any> {
        return Observable.create { emitter ->
            val purchase = purchasesResult.firstOrNull { !it.isAcknowledged }

            if (purchase == null) {
                emitter.onNext(TRIGGER)
                emitter.onComplete()
            } else {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
                    when (billingResult.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            Timber.i("acknowledgePurchase: OK ---> $purchase")
                            emitter.onNext(TRIGGER)
                            emitter.onComplete()
                        }
                        else -> {
                            Timber.d("acknowledgeNonConsumablePurchasesAsync response is ${billingResult.debugMessage}")
                        }
                    }
                }
            }
        }
    }

    @WorkerThread
    private fun getPurchase(trigger: Any): Observable<List<Purchase>> {
        return Observable.fromCallable {
            return@fromCallable playStoreBillingClient.queryPurchases(BillingClient.SkuType.SUBS)?.purchasesList ?: listOf<Purchase>()
        }
    }

    @WorkerThread
    private fun syncPurchase(purchases: List<Purchase>): Observable<List<Purchase>> {
        return userRepository.syncPurchase(purchases)
    }

    private fun isSubscriptionSupported(): Boolean {
        val billingResult = playStoreBillingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)

        return when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                connect()
                false
            }
            BillingClient.BillingResponseCode.OK -> true
            else -> {
                Timber.w("isSubscriptionSupported() error: ${billingResult.debugMessage}")
                false
            }
        }
    }
}