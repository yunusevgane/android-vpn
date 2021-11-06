package com.eskimobile.jetvpn.domain.model

import com.eskimobile.jetvpn.common.utils.SharePrefs
import com.eskimobile.jetvpn.common.utils.getStringPref
import com.eskimobile.jetvpn.common.utils.putStringPref
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson

data class User(val email: String, val password: String) {
    companion object {
        fun getDraft(): User? {
            val gson = Gson()
            return getStringPref(SharePrefs.KEY_USER)
                ?.let {
                    val user = gson.fromJson<User>(it, User::class.java)
                    return if (user.isLogin) {
                        user
                    } else {
                        null
                    }
                } ?: run {
                return null
            }
        }

        val stub = User("stub", "stub")

        fun fromFirebase(snapshot: DocumentSnapshot): User {
            return User(email = snapshot.getString("email") ?: "", password = snapshot.getString("password") ?: "")
                .apply {
                    val premium = snapshot.get("premium") as? Map<*, *> ?: return@apply
                    this.orderId = premium["orderId"] as? String
                    this.packageName = premium["packageName"] as? String
                    this.productId = premium["productId"] as? String
                    this.purchaseTime = (premium["purchaseTime"] as? Double)?.toLong()
                    this.purchaseToken = premium["purchaseToken"] as? String
                }
        }

    }

    var orderId: String? = null
    var packageName: String? = null
    var productId: String? = null
    var purchaseTime: Long? = null
    var purchaseToken: String? = null

    fun saveDraft() {
        val gson = Gson()
        val json = gson.toJson(this)
        putStringPref(SharePrefs.KEY_USER, json)
    }

    val isLogin: Boolean
        get() {
            return email.isNotBlank() && email != "stub" && email != "sub"
        }
}