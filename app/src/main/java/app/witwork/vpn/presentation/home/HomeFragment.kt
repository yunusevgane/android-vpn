package com.eskimobile.jetvpn.presentation.home

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.eventbus.ActionEvent
import com.eskimobile.jetvpn.common.eventbus.ServerEvent
import com.eskimobile.jetvpn.common.utils.*
import com.eskimobile.jetvpn.common.widget.bottomnav.BottomNavBar
import com.eskimobile.jetvpn.domain.model.Server
import com.eskimobile.jetvpn.presentation.main.MainActivity
import com.eskimobile.jetvpn.presentation.main.MainFragment
import com.eskimobile.jetvpn.presentation.servers.ServerListFragment
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import de.blinkt.openvpn.DisconnectVPNActivity
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.eventbus.VpnErrorEvent
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_traffic.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BaseFragment<HomeView, HomePresenter>(), HomeView, MainFragment.OnTabChanged {
    companion object {
        private const val STATE_IDLE = 0
        private const val STATE_CONNECTING = 1
        private const val STATE_CONNECTED = 2
        private const val RQ_START_VPN_PROFILE = 99
        var interstitialCount = 0

    }

    @Inject
    lateinit var presenter: HomePresenter
    private var animator: ValueAnimator? = null
    private var state: Int = STATE_IDLE

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        initBtnConnect(presenter.isConnected)
        this.registerEventBus()
    }

    override fun onPause() {
        super.onPause()
        this.unregisterEventBus()
    }

    override fun presenter(): BasePresenter<HomeView>? {
        return presenter
    }

    override fun viewIF(): HomeView? {
        return this
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_home
    }


    private var mInterstitialAd: InterstitialAd? = null
    private var showInterstitial = 1;

    fun ads2() {

        if (mInterstitialAd != null) {

            if(interstitialCount == showInterstitial){
                mInterstitialAd?.show((context as AppCompatActivity))
                interstitialCount--
            } else {
                interstitialCount++
                openServerList()
            }
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ads()
                openServerList()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {

            }

            override fun onAdShowedFullScreenContent() {

                mInterstitialAd = null;
            }
        }
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



    override fun initView() {
        ads()
        btnServers.setOnClickListener {

            //openServerList()

            val premium = context.getBooleanPref(SharePrefs.KEY_PREMIUM)
            if(!premium){
                ads2()
            } else {
                openServerList()
            }

        }
        btnConnect.setOnClickListener {
            when (true) {
                !hasInternet && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> showNetworkError()
                state == STATE_CONNECTING -> {
                    //do nothing
                }
                presenter.isConnected,
                state == STATE_CONNECTED -> {
                    DisconnectVPNActivity.start(context)
                }
                else -> prepareStartVpn()
            }
        }
    }

    override fun initData() {
        val serverDraf = Server.getDraft()
        updateServerButton(serverDraf)
        onUpdateConnectionStatus()
        initChart()
        initBtnConnect(presenter.isConnected)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            RQ_START_VPN_PROFILE -> startVpn(500L)
        }
    }

    fun removeServerListFragmentIfNeeded() {
        childFragmentManager.findFragmentByTag(ServerListFragment::class.java.name)
            ?.let {
                childFragmentManager
                    .beginTransaction()
                    .remove(it)
                    .commit()
            }
    }

    /**
     * MainFragment.OnTabChanged
     */
    override fun onChange(tabIndex: Int) {
        if (tabIndex != BottomNavBar.TAB_HOME) {
            return
        }

        childFragmentManager.findFragmentByTag(ServerListFragment::class.java.name)
            ?.let {
                activity?.updateColorStatusBar(R.color.colorNavBottomBackground)
            }
            ?: run {
                activity?.updateColorStatusBar(R.color.colorPrimary)
            }
    }

    //region #Private method

    private fun initChart() {
       // chartUpload.init(intArrayOf(5, 30, 100, 65, 80))
       // chartDownload.init(intArrayOf(5, 30, 65, 50, 100))
        val upload = arrayOf<Int>(30, 100, 65, 80)
        chartUpload.init(upload)
        val download = arrayOf<Int>(5, 30, 65, 50, 100)
        chartDownload.init(download)
    }

    private fun initBtnConnect(connected: Boolean) {
        if (connected) {
            tvState.text = getString(R.string.disconnect)
            viewProgress.visibility = View.VISIBLE
            animator?.cancel()
            viewProgress.layoutParams = (viewProgress.layoutParams as FrameLayout.LayoutParams).apply {
                width = FrameLayout.LayoutParams.MATCH_PARENT
            }
            updateLottie(STATE_CONNECTED)
            tvUpload.text = OpenVPNService.getUpload()
            tvDownload.text = OpenVPNService.getDownload()
        } else {
            if (state == STATE_CONNECTING) {
                return
            }
            tvState.text = getString(R.string.connect)
            viewProgress.visibility = View.INVISIBLE
            updateLottie(STATE_IDLE)
            presenter.syncDataIfNeed()
        }
    }

    private fun prepareStartVpn() {
        VpnService.prepare(context)
            ?.let {
                startActivityForResult(it, RQ_START_VPN_PROFILE)
            }
            ?: run {
                startVpn()
            }
    }

    private fun startVpn(startDelay: Long = 0L) {
        val serverDraft = Server.getDraft() ?: run {
            openServerList()
            return
        }

        val result = presenter.startVpn(serverDraft)

        if (result) {
            lottieLogo.playAnimation()
            fakeProgress(startDelay)
        } else {
            context?.showToast(getString(R.string.something_wrong))
        }
    }

    private fun stopVpn() {
        OpenVPNThread.stop()
        initBtnConnect(false)
    }

    private fun fakeProgress(startDelay: Long = 0L) {
        updateLottie(STATE_CONNECTING)
        viewProgress.visibility = View.VISIBLE
        val layoutParam: FrameLayout.LayoutParams =
            viewProgress.layoutParams as FrameLayout.LayoutParams
        val originWidth = viewProgress.width
        animator = ValueAnimator.ofFloat(0f, 100f)
            .apply {
                duration = 5000L
                interpolator = DecelerateInterpolator()
                setStartDelay(startDelay)
                addUpdateListener {
                    val value = it.animatedValue as Float
                    val process = originWidth.times(value) / 100
                    layoutParam.width = process.toInt()
                    tvState.text = getString(R.string.connecting, "${value.toInt()}%")
                    viewProgress.layoutParams = layoutParam
                    if (viewProgress.visibility == View.INVISIBLE) {
                        viewProgress.visibility = View.VISIBLE
                    }
                }

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        if (!presenter.isConnected) {
                            tvState.text = getString(R.string.waiting)
                        }
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }

                })
            }
        animator?.start()
    }

    private fun updateLottie(state: Int) {
        this.state = state
        when (state) {
            STATE_IDLE,/*-> lottieLogo.setAnimation(R.raw.ic_logo_connect)*/
            STATE_CONNECTING -> lottieLogo.apply {
                setAnimation(R.raw.ic_logo_connect)
                repeatMode = LottieDrawable.RESTART
                playAnimation()
            }
            STATE_CONNECTED -> lottieLogo.apply {
                setAnimation(R.raw.ic_logo_connected)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
        }
    }

    private fun openServerList() {
        val fragment = ServerListFragment()
        childFragmentManager
            .beginTransaction()
            .add(R.id.container, fragment, ServerListFragment::class.java.name)
            .commit()
    }

    private fun updateServerButton(value: Server? = null) {
        btnServers.apply {
            setFlag(Util.getResId(value?.countryCode) ?: R.drawable.ic_globe)
            setTitle(value?.country ?: getString(R.string.select_the_fastest_server))
            setDescription(value?.state)
            value?.saveDraf()
        }
    }

    //endregion

    /**
     * EventBus
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onVpnInfoEvent(event: ServerEvent) {
        if (event.change) {
            stopVpn()
        }
        removeServerListFragmentIfNeeded()
        updateServerButton(event.value)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onActionEvent(event: ActionEvent) {
        when (event.action) {
            Action.ACTION_REMOVE_SERVER_LIST_FRAGMENT -> removeServerListFragmentIfNeeded()
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onVpnErrorEvent(event: VpnErrorEvent){
        if(Feature.FEATURE_SYNC_ERROR){
            Timber.i(event.message)
            presenter.syncError(event.message)
        }
    }

    //---------------------------HomeView-----------------------
    override fun onConnected() {
        (activity as? MainActivity)?.showAdIfVpnConnected()
        context.showToast(getString(R.string.connected))
        initBtnConnect(true)
    }

    override fun onDisconnected() {
        animator?.cancel()
        updateLottie(STATE_IDLE)
        initBtnConnect(false)
    }

    override fun onUpdateConnectionStatus(upload: String?, download: String?) {
        tvUpload.text = upload ?: "0 B"
        tvDownload.text = download ?: "0 B"
    }
}
