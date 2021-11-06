package com.eskimobile.jetvpn.presentation.other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.presentation.main.MainActivity
import com.steve.utilities.core.connectivity.base.ConnectivityProvider
import com.steve.utilities.core.connectivity.base.hasInternet
import kotlinx.android.synthetic.main.activity_network_error.*

class NetworkErrorActivity : AppCompatActivity() {
    private val provider: ConnectivityProvider by lazy { ConnectivityProvider.createProvider(this) }

    companion object {
        fun start(context: Context?) {
            val intent = Intent(context, NetworkErrorActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_error)

        btnRefresh.setOnClickListener {
            val hasIntener = provider.getNetworkState().hasInternet()
            if (hasIntener) {
                MainActivity.start(this)
            }
        }
    }
}