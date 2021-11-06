package com.eskimobile.jetvpn.presentation.auth

import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.utils.*
import com.eskimobile.jetvpn.common.widget.MyClickSpan
import com.eskimobile.jetvpn.domain.model.User
import com.eskimobile.jetvpn.presentation.main.MainActivity
import com.eskimobile.jetvpn.presentation.other.policy.PrivatePolicyFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import kotlinx.android.synthetic.main.layout_loading.*
import javax.inject.Inject

class AuthFragment : BaseFragment<AuthView, AuthPresenter>(), AuthView, View.OnClickListener {

    @Inject
    lateinit var presenter: AuthPresenter

    private var durringLogin = true
        set(value) {
            field = value
            toggleMode()
        }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<AuthView>? {
        return presenter
    }

    override fun viewIF(): AuthView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_auth
    }

    override fun initView() {
        toolbar.apply {
            onBtnRightClicked = {
                durringLogin = !durringLogin
            }
        }

        tvBottomAction.apply {
            setOnClickListener(this@AuthFragment)
            toggleButton()
        }

        inputAddress.onTextChanged = {
            toggleButton()
        }
        inputPassword.onTextChanged = {
            toggleButton()
        }
        inputConfirmPassword.onTextChanged = {
            toggleButton()
        }

        tvForgotPassword.setOnClickListener(this)
        tvPrivatePolicy.apply {
            val input = tvPrivatePolicy.text
            val spannable = SpannableString(input)
                .apply {
                    val textHighLight = "private policy"
                    val indexOf = input.indexOf(textHighLight)
                    val color = ContextCompat.getColor(context, R.color.colorAccent)
                    setSpan(ForegroundColorSpan(color), indexOf, indexOf + textHighLight.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(object : MyClickSpan() {
                        override fun onClick(p0: View) {
                            context?.startActivity(PrivatePolicyFragment::class.java)
                        }
                    }, indexOf, indexOf + textHighLight.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            this.text = spannable
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun initData() {
        val fromChangePassword = arguments?.getBoolean("FROM_CHANGE_PASSWORD") ?: false
        if (fromChangePassword) {
            snackBar_Notice.toggle()
            inputAddress.onClick(null)
        }
    }

    override fun onPause() {
        super.onPause()
        clearFocus()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tvBottomAction -> {
                clearFocus()
                val email = inputAddress.text
                val password = inputPassword.text
                val confirmPassword = inputConfirmPassword.text
                if (durringLogin) {
                    handleSignIn(email, password)
                } else {
                    handleSignUp(email, password, confirmPassword)
                }
            }
            tvForgotPassword -> {
                context?.startActivity(ForgotPasswordFragment::class.java)
            }
        }
    }

    //----------------------- AuthView -----------------------------

    override fun showProgressDialog(isShow: Boolean) {
        viewLoading.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onSignInSuccess(user: User?) {
        context?.showToast("Sign in successfully!")
        user?.saveDraft()
        MainActivity.start(context)
    }

    override fun onSignUpSuccess(user: User) {
        context?.showToast("Sign up successfully!")
        user.saveDraft()
        MainActivity.start(context)
    }

    override fun showError(throwable: Throwable) {
        super.showError(throwable)
        snackBar.show(throwable.localizedMessage)
    }

    //----------------------- Private method -----------------------------

    private fun toggleButton() {
        val email = inputAddress.text
        val password = inputPassword.text
        val disableButton = email.isEmpty() || password.isEmpty()
        tvBottomAction.isActivated = !disableButton
        tvBottomAction.isClickable = !disableButton
    }

    private fun toggleMode() {
        clearFocus()
        resetInput()
        snackBar.visibility = View.INVISIBLE

        if (durringLogin) {
            toolbar.title = getString(R.string.login)
            toolbar.rightText = getString(R.string.sign_up)
            tvBottomAction.text = getString(R.string.login)
            tvForgotPassword.visibility = View.VISIBLE

            inputConfirmPassword.visibility = View.GONE
            tvPrivatePolicy.visibility = View.GONE
        } else {
            toolbar.title = getString(R.string.sign_up)
            toolbar.rightText = getString(R.string.login)
            tvBottomAction.text = getString(R.string.sign_up)
            inputConfirmPassword.visibility = View.VISIBLE
            tvPrivatePolicy.visibility = View.VISIBLE

            tvForgotPassword.visibility = View.GONE
        }
    }

    private fun handleSignUp(email: String, password: String, confirmPassword: String) {
        if (!Util.validateEmail(email)) {
            inputAddress.error = true
            snackBar.show(getString(R.string.message_invalid_email))
            return
        }

        if (!Util.validatePassword(password, confirmPassword)) {
            inputPassword.error = true
            snackBar.show(getString(R.string.message_password_doesnt_match))
            return
        }
        snackBar.hide()
        presenter.signUp(email, password)
    }

    private fun handleSignIn(email: String, password: String) {
        if (!Util.validateEmail(email)) {
            inputAddress.error = true
            snackBar.show(getString(R.string.message_invalid_email))
            return
        }
        snackBar.hide()
        presenter.signIn(email, password)
    }

    private fun clearFocus() {
        activity?.hideSoftKeyboard()
        inputPassword.clearFocus()
        inputAddress.clearFocus()
        inputConfirmPassword.clearFocus()
    }

    private fun resetInput() {
        inputAddress.reset()
        inputPassword.reset()
        inputConfirmPassword.reset()
    }
}
