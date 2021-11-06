package com.eskimobile.jetvpn.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.steve.utilities.core.connectivity.base.ConnectivityProvider
import com.steve.utilities.core.connectivity.base.hasInternet

abstract class BaseActivity : AppCompatActivity(), ConnectivityProvider.ConnectivityStateListener {
    private val provider: ConnectivityProvider by lazy { ConnectivityProvider.createProvider(this) }
    private var currentFragment: BaseFragment<*, *>? = null

    val hasInternet: Boolean
        get() {
            return provider.getNetworkState().hasInternet()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        provider.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        provider.removeListener(this)
    }

    private fun initFragment() {
        currentFragment = injectFragment()

        currentFragment?.let {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, it)
                .commit()
        }
    }

    abstract fun injectFragment(): BaseFragment<*, *>

    override fun onBackPressed() {
        if (currentFragment?.onBackPressed() == true)
            return
        super.onBackPressed()
    }

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
//        val hasInternet = state.hasInternet()
//        if (!hasInternet) {
//            NetworkErrorActivity.start(this)
//        }
    }
}
