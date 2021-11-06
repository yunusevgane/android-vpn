package com.eskimobile.jetvpn.common.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.steve.utilities.core.extensions.checkDisposed
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

//region #RxFirebaseAuth

fun FirebaseAuth.rxSignInWithEmailAndPassword(email: String, password: String): Observable<AuthResult> {
    return Observable.create { emitter ->
        this.signInWithEmailAndPassword(email, password)
            .rxObsAddOnCompleteListener(emitter)
    }
}

fun FirebaseAuth.rxCreateUserWithEmailAndPassword(email: String, password: String): Observable<AuthResult> {
    return Observable.create { emitter ->
        this.createUserWithEmailAndPassword(email, password)
            .rxObsAddOnCompleteListener(emitter)
    }
}

fun FirebaseAuth.rxSignOut(): Completable {
    return Completable.create {
        this.signOut()
        it.onComplete()
    }
}

fun FirebaseAuth.rxSendPasswordResetEmail(email: String): Completable {
    return Completable.create { emitter ->
        this.sendPasswordResetEmail(email)
            .rxCompleteAddOnCompleteListener(emitter)
    }
}

fun FirebaseAuth.rxChangePassword(password: String): Completable {
    return Completable.create { emitter ->
        this.currentUser?.updatePassword(password)
            ?.rxCompleteAddOnCompleteListener(emitter)
            ?: emitter.onError(Throwable("Something error..."))
    }
}
//endregion

//region #RxFirebaseFirestore
fun CollectionReference.rxAdd(data: Any): Observable<DocumentReference> {
    return Observable.create { emitter ->
        this.add(data)
            .rxObsAddOnCompleteListener(emitter)
    }
}

fun CollectionReference.rxGet(): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        this.get()
            .rxObsAddOnCompleteListener(emitter)
    }
}

fun Query.rxGet(): Observable<QuerySnapshot> {
    return Observable.create { emitter ->
        this.get().rxObsAddOnCompleteListener(emitter)
    }
}

fun DocumentReference.rxSet(data: Any): Completable {
    return Completable.create { emitter ->
        this.set(data)
            .rxCompleteAddOnCompleteListener(emitter)
    }
}

fun DocumentReference.rxSetWithMerge(data: Any): Completable {
    return Completable.create { emitter ->
        this.set(data, SetOptions.merge())
            .rxCompleteAddOnCompleteListener(emitter)
    }
}

fun DocumentReference.rxSetWithMerge2(data: Any): Observable<Boolean> {
    return Observable.create { emitter ->
        this.set(data, SetOptions.merge())
            .rxCompleteAddOnCompleteListener2(emitter)
    }
}

fun DocumentReference.rxGet(): Observable<DocumentSnapshot> {
    return Observable.create { emitter ->
        this.get()
            .rxObsAddOnCompleteListener(emitter)
    }
}


//endregion

private fun <T> Task<T>.rxObsAddOnCompleteListener(emitter: ObservableEmitter<T>) {
    this
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result?.let {
                    emitter.checkDisposed()?.onNext(it)
                    emitter.checkDisposed()?.onComplete()
                }
            } else {
                emitter.checkDisposed()?.onError(task.exception ?: Throwable("Something error..."))
            }
        }
}

private fun <T> Task<T>.rxCompleteAddOnCompleteListener(emitter: CompletableEmitter) {
    this
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(task.exception ?: Throwable("Something error..."))
            }
        }
}

private fun <T> Task<T>.rxCompleteAddOnCompleteListener2(emitter: ObservableEmitter<Boolean>) {
    this
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emitter.checkDisposed()?.onNext(true)
                emitter.checkDisposed()?.onComplete()
            } else {
                emitter.checkDisposed()?.onError(task.exception ?: Throwable("Something error..."))
            }
        }
}
