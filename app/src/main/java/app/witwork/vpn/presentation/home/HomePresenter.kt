package com.eskimobile.jetvpn.presentation.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Base64
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.domain.model.Server
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import timber.log.Timber
import javax.inject.Inject

class HomePresenter @Inject constructor() : BasePresenter<HomeView>() {
    companion object {
        const val ACTION_CONNECTION_STATE = "connectionState"
        const val EXTRA_STATE = "state"
        const val EXTRA_DOWNLOAD = "download"
        const val EXTRA_UPLOAD = "upload"
    }

    @Inject
    lateinit var contextApp: Context

    @Inject
    lateinit var userRepository: UserRepository

    val isConnected: Boolean
        get() {
            return OpenVPNService.getStatus() == "CONNECTED"
        }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            handleBroadcastReceiver(intent)
        }
    }

    fun startVpn(serverDraft: Server): Boolean {
        val data: ByteArray
        try {
            data = Base64.decode(serverDraft.ovpn, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        OpenVpnApi.startVpn(contextApp, data, serverDraft.country, "witvpn", "witvpn")
        return true
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        LocalBroadcastManager.getInstance(contextApp).registerReceiver(broadcastReceiver, IntentFilter(ACTION_CONNECTION_STATE))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        LocalBroadcastManager.getInstance(contextApp).unregisterReceiver(broadcastReceiver)
    }

    fun handleBroadcastReceiver(intent: Intent?) {
        intent?.getStringExtra(EXTRA_STATE)?.also {
            Timber.i("handleBroadcastReceiver: $it")
            if (it == "CONNECTED") {
                view?.onConnected()
            }

            if (it == "DISCONNECTED") {
                view?.onDisconnected()
            }
        }
        intent?.also {
            val upload = it.getStringExtra(EXTRA_UPLOAD)
            val download = it.getStringExtra(EXTRA_DOWNLOAD)
            view?.onUpdateConnectionStatus(upload, download)
        }
    }

    private var byteInProcess = 0L
    private var byteOutProcess = 0L
    fun syncDataIfNeed() {
        val byteIn = OpenVPNService.getByteIn()
        val byteOut = OpenVPNService.getByteOut()
        if (byteInProcess == byteIn && byteOutProcess == byteOut) {
            return
        }
        byteInProcess = byteIn
        byteOutProcess = byteOut
        userRepository.syncTraffic(byteOut, byteIn)
            .compose(completableTransformer())
            .subscribe({
                byteInProcess = 0L
                byteOutProcess = 0L
                Timber.d("syncDataIfNeed successfully")
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

    fun syncError(message: String?) {
        userRepository.syncError(message)
    }
}