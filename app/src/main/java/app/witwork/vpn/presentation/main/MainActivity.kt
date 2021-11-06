package com.eskimobile.jetvpn.presentation.main;

import android.content.Context
import android.content.Intent
import com.eskimobile.jetvpn.common.base.BaseActivity
import com.eskimobile.jetvpn.common.base.BaseFragment

class MainActivity : BaseActivity() {
    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            context?.startActivity(intent)
        }
    }

    override fun injectFragment(): BaseFragment<*, *> {
        return MainFragment()
    }

    fun showSnackBar(message: String) {
        val fragment = supportFragmentManager
            .fragments.firstOrNull { it is MainFragment }

        (fragment as? MainFragment)?.showSnackBar(message)
    }

    fun showAdMobIfNeeded(premium: Boolean) {
        val fragment = supportFragmentManager
            .fragments.firstOrNull { it is MainFragment }
        (fragment as? MainFragment)?.showAdMobIfNeeded(premium)
    }

    fun showAdIfVpnConnected() {
        val fragment = supportFragmentManager
            .fragments.firstOrNull { it is MainFragment }
        (fragment as? MainFragment)?.showAdIfVpnConnected()
    }



}
