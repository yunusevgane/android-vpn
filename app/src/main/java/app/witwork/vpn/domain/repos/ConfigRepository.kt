package com.eskimobile.jetvpn.domain.repos

import com.eskimobile.jetvpn.domain.model.AdsConfig
import io.reactivex.Observable

interface ConfigRepository {
    fun getConfig(): Observable<AdsConfig>
}