package com.eskimobile.jetvpn.domain.repos

import com.eskimobile.jetvpn.domain.model.User
import com.android.billingclient.api.Purchase
import io.reactivex.Completable
import io.reactivex.Observable

interface UserRepository {
    fun signIn(email: String, password: String): Observable<User>

    fun signUp(email: String, password: String): Completable

    fun signOut(): Completable

    fun forgotPassword(email: String): Completable

    fun changePassword(password: String): Completable

    fun syncTraffic(byteOut: Long, byteIn: Long): Completable

    fun syncPurchase(purchases: List<Purchase>): Observable<List<Purchase>>

    fun syncUserLogin(): Completable

    fun get(): Observable<User>

    fun listener(): Observable<User>

    fun syncError(message: String?)
}