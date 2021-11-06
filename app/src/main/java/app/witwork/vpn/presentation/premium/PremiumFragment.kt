package com.eskimobile.jetvpn.presentation.premium

import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.utils.SharePrefs
import com.eskimobile.jetvpn.common.utils.putBooleanPref
import com.eskimobile.jetvpn.common.utils.updateColorStatusBar
import com.eskimobile.jetvpn.common.widget.bottomnav.BottomNavBar
import com.eskimobile.jetvpn.presentation.main.MainActivity
import com.eskimobile.jetvpn.presentation.main.MainFragment
import kotlinx.android.synthetic.main.fragment_premium.*
import javax.inject.Inject

class PremiumFragment : BaseFragment<PremiumView, PremiumPresenter>(), PremiumView, MainFragment.OnTabChanged {
    companion object {
        const val ACTION_GET = 0
        const val ACTION_UPGRADE = 1
        const val ACTION_DOWNGRADE = 2
    }

    @Inject
    lateinit var presenter: PremiumPresenter

    private var action = ACTION_GET

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<PremiumView>? {
        return presenter
    }

    override fun viewIF(): PremiumView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_premium
    }

    override fun initView() {
        toggleButton(false)
        sivMonthly.setOnClickListener { toggleButton(false) }
        sivYearly.setOnClickListener { toggleButton(true) }
        btnGetPremium.apply {
            setOnClickListener { presenter.getPremium(activity, sivMonthly.isSelected, action) }
            togglePremiumButton()
        }
    }

    override fun onAcknowledgedPurchase(index: Int, newRequest: Boolean) {
        putBooleanPref(SharePrefs.KEY_PREMIUM, true)
        if (index == 0) {
            sivMonthly.purchase()
            sivYearly.initWith(getString(R.string.gold_yearly))
            btnGetPremium.text = getString(R.string.upgrade)
            action = ACTION_UPGRADE
        }

        if (index == 1) {
            sivMonthly.initWith(getString(R.string.gold_monthly))
            sivYearly.purchase()
            btnGetPremium.text = getString(R.string.downgrade)
            action = ACTION_DOWNGRADE
        }

        if (newRequest) {
            val message = if (index == 0) getString(R.string.message_get_premium_success)
            else getString(R.string.message_upgrade_premium_success)
            showSnackbarInMain(message)
        }
        togglePremiumButton()
    }

    override fun shouldShowAdMod(premium: Boolean) {
        putBooleanPref(SharePrefs.KEY_PREMIUM, premium)
        (activity as? MainActivity)?.showAdMobIfNeeded(premium)
    }

    override fun updatePrice(price1: String?, price2: String?, save: Int) {
        sivMonthly.setPrice(price1)
        sivYearly.setPrice(price2)
        sivYearly.setDescription("save \$$save% - 2 month free")
    }

    /**
     * MainFragment.OnTabChanged
     */
    override fun onChange(tabIndex: Int) {
        if (tabIndex != BottomNavBar.TAB_PREMIUM) {
            return
        }
        activity?.updateColorStatusBar(R.color.colorBackgroundPremium, true)
    }

    private fun toggleButton(isYearly: Boolean) {
        sivMonthly.isSelected = !isYearly
        sivYearly.isSelected = isYearly
        togglePremiumButton()
    }

    private fun togglePremiumButton() {
        btnGetPremium.isActivated = (sivMonthly.isSelected && sivMonthly.isEnabled) || (sivYearly.isSelected && sivYearly.isEnabled)
        btnGetPremium.isEnabled = btnGetPremium.isActivated
    }

}
