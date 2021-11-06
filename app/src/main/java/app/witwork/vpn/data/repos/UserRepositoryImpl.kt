package com.eskimobile.jetvpn.data.repos

import android.content.Context
import androidx.annotation.WorkerThread
import com.eskimobile.jetvpn.common.utils.*
import com.eskimobile.jetvpn.domain.model.User
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.android.billingclient.api.Purchase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.steve.utilities.core.extensions.checkDisposed
import com.steve.utilities.core.extensions.toStringWithPattern
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor() : UserRepository {
    @Inject
    lateinit var contextApp: Context

    override fun signIn(email: String, password: String): Observable<User> {
        return Firebase.auth
            .rxSignInWithEmailAndPassword(email, password)
            .flatMap {
                return@flatMap Firebase.firestore
                    .collection(FirebaseConstant.USERS)
                    .document(email)
                    .rxGet()
            }
            .map { return@map User.fromFirebase(it) }
    }

    override fun signUp(email: String, password: String): Completable {
        return Firebase.auth.rxCreateUserWithEmailAndPassword(email, password)
            .flatMapCompletable {
                val data = hashMapOf(
                    "email" to email,
                    "createAt" to Calendar.getInstance().toStringWithPattern(),
                    "deviceInfo" to Util.getDeviceInfo(contextApp)
                )
                return@flatMapCompletable Firebase.firestore
                    .collection(FirebaseConstant.USERS)
                    .document(email)
                    .rxSet(data)
            }
            .doOnComplete {
                sendVerificationEmail()
            }
    }

    override fun signOut(): Completable {
        return Firebase.auth.rxSignOut()
    }

    override fun forgotPassword(email: String): Completable {
        return Firebase.auth.rxSendPasswordResetEmail(email)
    }

    override fun changePassword(password: String): Completable {
        return Firebase.auth.rxChangePassword(password)
    }

    override fun syncTraffic(byteOut: Long, byteIn: Long): Completable {
        syncAnonymous(type = AnonymousType.TRAFFIC, byteOut = byteOut, byteIn = byteIn)
        val currentUser = User.getDraft() ?: run {
            return Completable.complete()
        }
        return initSyncTraffic(FirebaseConstant.USERS, currentUser.email, byteOut, byteIn)
    }

    override fun syncPurchase(purchases: List<Purchase>): Observable<List<Purchase>> {
        syncAnonymous(type = AnonymousType.PURCHASE, purchases = purchases)
        val currentUser = User.getDraft()
        if (currentUser?.email == null) {
            Timber.i("syncPurchase failed")
            return Observable.just(purchases)
        }

        val purchase = purchases.firstOrNull { it.isAcknowledged }

        if (purchase == null) {
            val data = mapOf(
                "premium" to FieldValue.delete()
            )

            return Observable.fromCallable {
                Firebase.firestore
                    .collection(FirebaseConstant.USERS)
                    .document(currentUser.email)
                    .update(data)
                return@fromCallable emptyList<Purchase>()
            }
        }

        val retMap: Map<String, Any> = Gson().fromJson(
            purchase.originalJson, object : TypeToken<HashMap<String?, Any?>?>() {}.type
        )
        val data = mapOf(
            "premium" to retMap
        )

        return Observable.fromCallable {
            Firebase.firestore
                .collection(FirebaseConstant.USERS)
                .document(currentUser.email)
                .set(data, SetOptions.merge())
            return@fromCallable listOf(purchase)
        }
    }

    override fun syncUserLogin(): Completable {
        syncAnonymous(type = AnonymousType.LAUNCH)
        val currentUser = User.getDraft() ?: run {
            return Completable.complete()
        }
        val data = hashMapOf(
            "lastLogin" to Calendar.getInstance().toStringWithPattern()
        )
        Timber.i("currentUser.email: ${currentUser.email}")

        return Firebase.firestore
            .collection(FirebaseConstant.USERS)
            .document(currentUser.email)
            .rxSetWithMerge(data)
    }

    override fun get(): Observable<User> {
        val currentUser = User.getDraft() ?: run {
            return Observable.error(Throwable("Not found"))
        }
        return Firebase.firestore
            .collection(FirebaseConstant.USERS)
            .document(currentUser.email)
            .rxGet()
            .map { return@map User.fromFirebase(it) }
    }

    override fun listener(): Observable<User> {
        val currentUser = User.getDraft() ?: run {
            return Observable.error(Throwable("Not found"))
        }
        return Observable.create { emitter ->
            Firebase.firestore
                .collection(FirebaseConstant.USERS)
                .document(currentUser.email)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        emitter.checkDisposed()?.onError(error)
                        return@addSnapshotListener
                    }
                    if (value != null && value.exists()) {
                        val user = User.fromFirebase(value)
                        emitter.checkDisposed()?.onNext(user)
                    }
                }
        }
    }

    override fun syncError(message: String?) {
        val userId = User.getDraft()?.email ?: Util.anonymousId

        val data = hashMapOf(
            "message" to message,
            "createAt" to Calendar.getInstance().toStringWithPattern()
        )

        Firebase.firestore
            .collection(FirebaseConstant.ERROR)
            .document(userId)
            .set(data)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    Timber.i("syncError #success")
                }
            }
    }

    @WorkerThread
    private fun syncAnonymous(type: AnonymousType, purchases: List<Purchase>? = null, byteOut: Long = 0L, byteIn: Long = 0L) {
        val anonymousId = Util.anonymousId
        fun sync(map: MutableMap<String, Any>) {
            Firebase.firestore
                .collection(FirebaseConstant.ANONYMOUS)
                .document(anonymousId)
                .rxGet()
                .flatMap {
                    val createdAt = it.getString("createAt")
                    if (createdAt == null) {
                        map["createAt"] = Calendar.getInstance().toStringWithPattern()
                    }
                    return@flatMap Firebase.firestore
                        .collection(FirebaseConstant.ANONYMOUS)
                        .document(anonymousId)
                        .rxSetWithMerge2(map)
                }
                .subscribe({}, Timber::e)
        }

        val map = mutableMapOf<String, Any>()
        when (type) {
            AnonymousType.LAUNCH -> {
                map["lastLogin"] = Calendar.getInstance().toStringWithPattern()
                map["deviceInfo"] = Util.getDeviceInfo(contextApp)
                sync(map)
            }
            AnonymousType.PURCHASE -> {
                val purchase = purchases?.firstOrNull()
                map["purchase"] = if (purchase == null) {
                    FieldValue.delete()
                } else {
                    val retMap: Map<String, Any> = Gson().fromJson(
                        purchase.originalJson, object : TypeToken<HashMap<String?, Any?>?>() {}.type
                    )
                    retMap
                }
                sync(map)
            }
            AnonymousType.TRAFFIC -> {
                initSyncTraffic(FirebaseConstant.ANONYMOUS, anonymousId, byteOut, byteIn)
                    .subscribe({
                        Timber.i("syncAnonymous:TRAFFIC success")
                    }, Timber::e)
            }
        }
    }

    private fun initSyncTraffic(collection: String, documentId: String, byteOut: Long, byteIn: Long): Completable {
        return Firebase.firestore
            .collection(collection)
            .document(documentId)
            .rxGet()
            .flatMapCompletable { snapshot ->
                val trafficServer = snapshot.get("traffic") as? Map<*, *>
                val uploadServer = trafficServer?.get("upload") as? Long ?: 0L
                val downloadServer = trafficServer?.get("download") as? Long ?: 0L

                val upload = uploadServer.plus(byteOut)
                val download = downloadServer.plus(byteIn)

                val data = mapOf(
                    "upload" to upload,
                    "download" to download,
                    "updateAt" to Calendar.getInstance().toStringWithPattern()
                )

                val traffic = mapOf(
                    "traffic" to data
                )
                Timber.d("syncTraffic: $traffic")

                return@flatMapCompletable Firebase.firestore
                    .collection(collection)
                    .document(documentId)
                    .rxSetWithMerge(traffic)
            }
    }

    private fun sendVerificationEmail() {
        Timber.i("sendVerificationEmail: called")
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Timber.i("sendVerificationEmail: success")
                } else {
                    Timber.i("sendVerificationEmail: error")
                }
            }
    }
}