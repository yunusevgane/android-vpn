package com.eskimobile.jetvpn.domain.model

import com.eskimobile.jetvpn.common.utils.SharePrefs
import com.eskimobile.jetvpn.common.utils.getStringPref
import com.eskimobile.jetvpn.common.utils.putStringPref
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson

data class Server(
    val id: String,
    val country: String?,
    val ovpn: String?,
    val ipAddress: String?,
    val premium: Boolean?,
    val recommend: Boolean?,
    val state: String?,
    val countryCode: String?
) {
    companion object {
        fun fromFirebase(snapshot: QueryDocumentSnapshot): Server {
            return Server(
                id = snapshot.id,
                country = snapshot.data["country"] as? String,
                ovpn = snapshot.data["ovpn"] as? String,
                ipAddress = snapshot.data["ipAddress"] as? String,
                premium = snapshot.data["premium"] as? Boolean,
                recommend = snapshot.data["recommend"] as? Boolean,
                state = snapshot.data["state"] as? String,
                countryCode = snapshot.data["countryCode"] as? String
            )
        }

        fun getDraft(): Server? {
            val gson = Gson()
            return getStringPref(SharePrefs.KEY_SERVER)
                ?.let {
                    return gson.fromJson<Server>(it, Server::class.java)
                } ?: run {
                return null
            }
        }
    }

    fun saveDraf() {
        val gson = Gson()
        val json = gson.toJson(this)
        putStringPref(SharePrefs.KEY_SERVER, json)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Server) {
            return other.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}