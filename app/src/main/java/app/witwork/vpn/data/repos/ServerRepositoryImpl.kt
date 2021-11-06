package com.eskimobile.jetvpn.data.repos

import com.eskimobile.jetvpn.common.utils.FirebaseConstant
import com.eskimobile.jetvpn.common.utils.rxGet
import com.eskimobile.jetvpn.domain.model.Server
import com.eskimobile.jetvpn.domain.repos.ServerRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.Observable
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor() : ServerRepository {
    override fun getServers(): Observable<List<Server>> {
        return Firebase.firestore
            .collection(FirebaseConstant.SERVERS)
            .whereEqualTo("status", true)
            .orderBy("premium", Query.Direction.ASCENDING)
            .rxGet()
            .map { documents ->
                val servers = mutableListOf<Server>()
                documents.mapTo(servers) {
                    return@mapTo Server.fromFirebase(it)
                }
                return@map servers
            }

    }
}