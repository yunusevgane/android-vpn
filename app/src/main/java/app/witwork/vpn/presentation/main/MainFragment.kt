package com.eskimobile.jetvpn.presentation.main

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.work.WorkManager
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.eventbus.ActionEvent
import com.eskimobile.jetvpn.common.eventbus.ChangeTabEvent
import com.eskimobile.jetvpn.common.utils.*
import com.eskimobile.jetvpn.common.widget.bottomnav.BottomNavBar
import com.eskimobile.jetvpn.domain.model.AdsConfig
import com.eskimobile.jetvpn.domain.model.User
import com.eskimobile.jetvpn.presentation.auth.AuthFragment
import com.eskimobile.jetvpn.presentation.home.HomeFragment
import com.eskimobile.jetvpn.presentation.premium.PremiumFragment
import com.eskimobile.jetvpn.presentation.profile.EditProfileFragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_bottom_navigation_custom.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class MainFragment : BaseFragment<MainView, MainPresenter>(), MainView {
    private val fragments = listOf(
        HomeFragment(),
        PremiumFragment(),
        EditProfileFragment()
    )

    @Inject
    lateinit var presenter: MainPresenter

    private var showAds: InterstitialAd? = null

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<MainView>? {
        return presenter
    }

    override fun viewIF(): MainView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_main
    }

    override fun initView() {
        context?.let {
            WorkManager.getInstance(it).cancelAllWork()
        }
        viewPager
            .apply {
                adapter = ViewPagerAdapter(childFragmentManager)
                offscreenPageLimit = 3
            }
        bottomNavBar.listener = object : BottomNavBar.OnTabChangedListener {
            override fun changed(tabIndex: Int): Boolean {
                val currentUser = User.getDraft()
                if (!hasInternet) {
                    showNetworkError()
                }

                if (tabIndex == BottomNavBar.TAB_PROFILE && (currentUser == null || !currentUser.isLogin)) {
                    context?.startActivity(AuthFragment::class.java)
                    return false
                }
                viewPager.setCurrentItem(tabIndex, false)
                fragments.forEach { (it as? OnTabChanged)?.onChange(tabIndex) }
                return true
            }

            override fun reSelected(tabIndex: Int) {
                if (tabIndex == BottomNavBar.TAB_HOME) {
                    (fragments[0] as? HomeFragment)?.removeServerListFragmentIfNeeded()
                }
            }
        }
    }

    override fun initData() {
        presenter.syncLogin()
        val premium = getBooleanPref(SharePrefs.KEY_PREMIUM)
        if (!premium) {
            presenter.getConfigs()
        }
    }

    override fun onResume() {
        super.onResume()
        registerEventBus()
        showAdsContainerIfNeeded()
    }

    override fun onPause() {
        super.onPause()
        unregisterEventBus()
    }

    override fun onGetConfigsSuccess(adsConfig: AdsConfig?) {
        //init banner
        val adView = AdView(context)
            .apply {
                adSize = AdSize.BANNER
                adUnitId = adsConfig?.banner
                setup()
                adListener = object : AdListener() {
                    override fun onAdLeftApplication() {
                        context.putBooleanPref(SharePrefs.KEY_SHOULD_SHOW_BANNER, false)
                        adsContainer.visibility = View.GONE
                        AdsWorker.execute(context)
                    }
                }
            }
        adsContainer.removeAllViews()
        adsContainer.addView(adView)

        //init interstitialAd
        showAds = InterstitialAd(context)
            .apply {
                setup(adsConfig?.show, showIfLoaded = false, refreshIfClosed = true)
            }
    }

    fun showSnackBar(message: String) {
        snackBar
            .apply {
                setMessage(message)
            }
            .toggle()
    }

    fun showAdIfVpnConnected(){
        showAds?.showIfNeeded()
    }

    fun showAdMobIfNeeded(premium: Boolean) {
        if (!Feature.FEATURE_ALLOW_ADMOB) {
            return
        }

        if (premium) {
            adsContainer.visibility = View.GONE
            return
        }
        adsContainer.visibility = View.VISIBLE
    }

    inner class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return fragments[position] as Fragment
        }

        override fun getCount(): Int {
            return fragments.count()
        }
    }

    interface OnTabChanged {
        fun onChange(tabIndex: Int)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeTabEvent(event: ChangeTabEvent) {
        bottomNavBar.currentTabSelected = event.tabIndex
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onActionEvent(event: ActionEvent) {
        if (event.action == Action.ACTION_SHOULD_SHOW_BANNER) {
            val premium = context?.getBooleanPref(SharePrefs.KEY_PREMIUM)
            if (premium == false && adsContainer.visibility == View.GONE) {
                adsContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun showAdsContainerIfNeeded() {
        val shouldShow = context.getBooleanPref(SharePrefs.KEY_SHOULD_SHOW_BANNER)
        val premium = context.getBooleanPref(SharePrefs.KEY_PREMIUM)
        if (shouldShow && adsContainer.visibility == View.GONE && !premium) {
            adsContainer.visibility = View.VISIBLE
        }
    }

}
