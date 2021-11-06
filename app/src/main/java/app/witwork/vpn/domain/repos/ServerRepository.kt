package com.eskimobile.jetvpn.domain.repos

import com.eskimobile.jetvpn.domain.model.Server
import io.reactivex.Observable

interface ServerRepository {
    fun getServers(): Observable<List<Server>>
}