package com.eskimobile.jetvpn.common.widget.bottomnav

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.utils.SharePrefs
import com.eskimobile.jetvpn.common.utils.getBooleanPref
import com.eskimobile.jetvpn.presentation.home.HomeFragment.Companion.interstitialCount
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


class BottomNavBar(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), View.OnClickListener {
    companion object {
        const val TAB_HOME = 0
        const val TAB_PREMIUM = 1
        const val TAB_PROFILE = 2



    }

    private lateinit var tabHome: BottomNavItem
    private lateinit var tabPremium: BottomNavItem
    private lateinit var tabProfile: BottomNavItem
    private var showInterstitial = 1;


    var listener: OnTabChangedListener? = null

    var currentTabSelected = -1
        set(value) {
            val changed = field != value
            if (changed) {
                if (toggle(value)) {
                    field = value
                }
            } else {
                listener?.reSelected(field)
            }
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tabHome = findViewById(R.id.tab_home)
        tabPremium = findViewById(R.id.tab_premium)
        tabProfile = findViewById(R.id.tab_profile)
        ads()
        tabHome.setOnClickListener(this)
        tabPremium.setOnClickListener(this)
        tabProfile.setOnClickListener(this)

        this.currentTabSelected = TAB_HOME
    }


    override fun onClick(p0: View?) {
        this.currentTabSelected = when (p0?.id) {
            R.id.tab_home -> {
                ads2(TAB_HOME)
                TAB_HOME
            }
            R.id.tab_premium -> {
                TAB_PREMIUM
                ads2(TAB_PREMIUM)
            }
            else -> {
                ads2(TAB_PROFILE)
                TAB_PROFILE
            }
        }
    }


    private var mInterstitialAd: InterstitialAd? = null

    fun ads2(p0: Int): Int {

        val premium = context.getBooleanPref(SharePrefs.KEY_PREMIUM)

        if (mInterstitialAd != null && !premium) {

            if(interstitialCount == showInterstitial){
                mInterstitialAd?.show((context as AppCompatActivity))
                interstitialCount--
            } else {
                interstitialCount++
            }
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ads()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            }

            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null;
            }
        }

        return p0
    }

    fun ads(){

        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load((context as AppCompatActivity),(context as AppCompatActivity).getString(R.string.interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }

        })

    }



    private fun toggle(tabSelected: Int): Boolean {
        val change = listener?.changed(tabSelected)

        if (change == false) {
            return false
        }
        tabHome.isSelected = tabSelected == TAB_HOME
        tabPremium.isSelected = tabSelected == TAB_PREMIUM
        tabProfile.isSelected = tabSelected == TAB_PROFILE
        return true
    }

    interface OnTabChangedListener {
        fun changed(tabIndex: Int): Boolean
        fun reSelected(tabIndex: Int)
    }

}