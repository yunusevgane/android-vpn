package com.eskimobile.jetvpn.presentation.splash;

import com.eskimobile.jetvpn.common.base.BaseActivity
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.steve.utilities.core.connectivity.base.ConnectivityProvider

class SplashActivity : BaseActivity() {

    override fun injectFragment(): BaseFragment<*, *> {
        return SplashFragment()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        //nothing
    }
}
