package com.eskimobile.jetvpn.presentation.splash

import android.animation.Animator
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.presentation.main.MainActivity
import kotlinx.android.synthetic.main.fragment_splash.*
import javax.inject.Inject

class SplashFragment : BaseFragment<SplashView, SplashPresenter>(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    private var animListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(p0: Animator?) {
        }

        override fun onAnimationEnd(p0: Animator?) {
            presenter.sync()
        }

        override fun onAnimationCancel(p0: Animator?) {
        }

        override fun onAnimationStart(p0: Animator?) {
        }

    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<SplashView>? {
        return presenter
    }

    override fun viewIF(): SplashView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_splash
    }

    override fun initView() {
        lottieLogo.addAnimatorListener(animListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lottieLogo.removeAnimatorListener(animListener)
    }

    override fun goToMainScreen() {
        MainActivity.start(context)
    }
}
