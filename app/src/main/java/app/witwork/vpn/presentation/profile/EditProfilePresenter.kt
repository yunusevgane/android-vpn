package com.eskimobile.jetvpn.presentation.profile

import android.content.Context
import android.content.Intent
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.domain.model.User
import com.eskimobile.jetvpn.domain.repos.BillingRepository
import com.eskimobile.jetvpn.domain.repos.UserRepository
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.steve.utilities.core.extensions.addToCompositeDisposable
import com.steve.utilities.core.extensions.completableTransformer
import com.steve.utilities.core.extensions.observableTransformer
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class EditProfilePresenter @Inject constructor() : BasePresenter<EditProfileView>() {
    companion object {
        const val RC_GET_TOKEN = 999
    }

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var contextApp: Context

    private val options: GoogleSignInOptions by lazy {
        return@lazy GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(contextApp.getString(R.string.default_web_client_id))
            .requestServerAuthCode(contextApp.getString(R.string.default_web_client_id), true)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        userRepository.listener()
            .onErrorReturn { return@onErrorReturn User.stub }
            .compose(observableTransformer())
            .subscribe({
                if (it.isLogin)
                    view?.onGetCurrentUserSuccess(it)
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

    fun logout() {
        userRepository.signOut()
            .compose(completableTransformer())
            .subscribe({
                view?.onLogoutSuccess()
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }

    fun getAccessToken(fragment: EditProfileFragment) {
        val intent = GoogleSignIn.getClient(fragment.activity!!, options).signInIntent
        fragment.startActivityForResult(intent, RC_GET_TOKEN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != RC_GET_TOKEN) {
            return
        }

        Observable
            .fromCallable {
                val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/androidpublisher"
//                val scope = "oauth2:https://www.googleapis.com/auth/userinfo.profile"

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                return@fromCallable GoogleAuthUtil.getToken(contextApp, account.account, scope)
            }
            .compose(observableTransformer())
            .subscribe({
                Timber.i("Access token: $it")
            }, Timber::e)
            .addToCompositeDisposable(disposable)
    }
}