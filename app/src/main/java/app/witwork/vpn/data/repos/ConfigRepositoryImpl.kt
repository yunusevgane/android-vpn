package com.eskimobile.jetvpn.data.repos

import com.eskimobile.jetvpn.common.utils.FirebaseConstant
import com.eskimobile.jetvpn.common.utils.rxGet
import com.eskimobile.jetvpn.domain.model.AdsConfig
import com.eskimobile.jetvpn.domain.repos.ConfigRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Observable
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor() : ConfigRepository {
    override fun getConfig(): Observable<AdsConfig> {
        return Firebase.firestore
            .collection(FirebaseConstant.CONFIGS)
            .rxGet()
            .map {
                val ads = it.documents.firstOrNull()?.data?.get("ads") as? MutableList<*>
                val map = ads?.firstOrNull() as MutableMap<*, *>
                val google = map["google"] as MutableMap<*, *>
                return@map AdsConfig.fromFirebase(google)
            }
    }
}