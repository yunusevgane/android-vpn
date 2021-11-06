package com.eskimobile.jetvpn.common.utils

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd

fun InterstitialAd.setup(adUnitId: String?, showIfLoaded: Boolean = false, refreshIfClosed : Boolean = false) {
    this.adUnitId = adUnitId
    this.loadAd(AdRequest.Builder().build())
    this.adListener = object : AdListener() {
        override fun onAdLoaded() {
            if (showIfLoaded) {
                this@setup.show()
            }
        }

        override fun onAdClosed() {
            super.onAdClosed()
            if(refreshIfClosed){
                this@setup.loadAd(AdRequest.Builder().build())
            }
        }
    }
}

fun InterstitialAd.showIfNeeded() {
    if (this.isLoaded) {
        this.show()
    }
}

fun AdView.setup() {
    this.loadAd(AdRequest.Builder().build())
}