package com.eskimobile.jetvpn.presentation.other.policy

import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import javax.inject.Inject

class PrivatePolicyFragment : BaseFragment<PrivatePolicyView, PrivatePolicyPresenter>(), PrivatePolicyView {

    @Inject
    lateinit var presenter: PrivatePolicyPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<PrivatePolicyView>? {
        return presenter
    }

    override fun viewIF(): PrivatePolicyView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_private_policy
    }

    override fun initView() {
    }
}
