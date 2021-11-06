package com.eskimobile.jetvpn.presentation.servers

import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.utils.observableTransformer
import com.eskimobile.jetvpn.domain.repos.ServerRepository
import com.steve.utilities.core.extensions.addToCompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class ServerListPresenter @Inject constructor() : BasePresenter<ServerListView>() {
    @Inject
    lateinit var serverRepository: ServerRepository

    fun getServers() {
        serverRepository
            .getServers()
            .compose(observableTransformer(view))
            .subscribe({
                view?.onGetServersSuccess(it)
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }
}