package com.eskimobile.jetvpn.presentation.password

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.utils.Util
import com.eskimobile.jetvpn.common.utils.startActivity
import com.eskimobile.jetvpn.presentation.auth.AuthFragment
import kotlinx.android.synthetic.main.fragment_auth.tvBottomAction
import kotlinx.android.synthetic.main.fragment_change_password.*
import javax.inject.Inject

class ChangePasswordFragment : BaseFragment<ChangePasswordView, ChangePasswordPresenter>(), ChangePasswordView {

    @Inject
    lateinit var presenter: ChangePasswordPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<ChangePasswordView>? {
        return presenter
    }

    override fun viewIF(): ChangePasswordView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_change_password
    }

    override fun initView() {
        inputNewPassword.onTextChanged = {
            toggleButton()
        }
        inputConfirmNewPassword.onTextChanged = {
            toggleButton()
        }

        tvBottomAction.apply {
            setOnClickListener(this@ChangePasswordFragment::handleResetPassword)
            toggleButton()
        }
    }

    override fun onChangePasswordSuccess() {
        val bundle = Bundle()
            .apply {
                putBoolean("FROM_CHANGE_PASSWORD", true)
            }
        context?.startActivity(AuthFragment::class.java, bundle, Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    private fun handleResetPassword(view: View?) {
        val newPassword = inputNewPassword.text
        val confirmNewPassword = inputConfirmNewPassword.text

        if (!Util.validatePassword(newPassword, confirmNewPassword)) {
            inputNewPassword.error = true
            snackBar.show(getString(R.string.message_password_doesnt_match))
            return
        }
        snackBar.hide()
        presenter.changePassword(newPassword)
    }

    private fun toggleButton() {
        val newPassword = inputNewPassword.text
        val confirmNewPassword = inputConfirmNewPassword.text
        val disableButton = newPassword.isEmpty() || confirmNewPassword.isEmpty()
        tvBottomAction.isActivated = !disableButton
        tvBottomAction.isClickable = !disableButton
    }
}
