package com.eskimobile.jetvpn.presentation.servers.tab

import android.os.Bundle
import android.view.View
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.MyApp
import com.eskimobile.jetvpn.common.base.BaseFragment
import com.eskimobile.jetvpn.common.base.BasePresenter
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.common.dialog.ConfirmDialogFragment
import com.eskimobile.jetvpn.common.eventbus.ChangeTabEvent
import com.eskimobile.jetvpn.common.eventbus.ServerEvent
import com.eskimobile.jetvpn.common.utils.Feature
import com.eskimobile.jetvpn.common.utils.SharePrefs
import com.eskimobile.jetvpn.common.utils.getBooleanPref
import com.eskimobile.jetvpn.common.utils.postEvent
import com.eskimobile.jetvpn.domain.model.Server
import com.eskimobile.jetvpn.presentation.servers.ServerListView
import de.blinkt.openvpn.core.OpenVPNService
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.layout_loading.*
import javax.inject.Inject

class TabFragment : BaseFragment<TabView, TabPresenter>(), TabView, ServerListView {
    companion object {
        private const val EXTRA_SCREEN = "EXTRA_SCREEN"
        const val ALL_LOCATION = "ALL_LOCATION"
        const val RECOMMENDED = "RECOMMENDED"

        fun newInstance(tab: String): TabFragment {
            val bundle = Bundle()
                .apply {
                    putString(EXTRA_SCREEN, tab)
                }

            return TabFragment()
                .apply {
                    arguments = bundle
                }
        }
    }

    @Inject
    lateinit var presenter: TabPresenter

    private var tabAdapter: TabAdapter? = null

    private val screen: String by lazy {
        return@lazy arguments?.getString(EXTRA_SCREEN) ?: ALL_LOCATION
    }

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<TabView>? {
        return presenter
    }

    override fun viewIF(): TabView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_tab
    }

    override fun initView() {
        val serverDraft = Server.getDraft()
        tabAdapter = TabAdapter(serverDraft) { server ->
            when (true) {
                Feature.IS_ADMIN ->{
                    postEvent(ServerEvent.init(server, false))
                }
                !getBooleanPref(SharePrefs.KEY_PREMIUM) && server?.premium == true -> {
                    postEvent(ChangeTabEvent.premium)
                }
                server != serverDraft && OpenVPNService.getStatus() == "CONNECTED" -> {
                    context?.let {
                        ConfirmDialogFragment.changeVpn(it, childFragmentManager) {
                            postEvent(ServerEvent.init(server, true))
                        }
                    }
                }
                else -> {
                    postEvent(ServerEvent.init(server, false))
                }
            }
        }

        recyclerView
            .apply {
                adapter = tabAdapter
            }
    }

    override fun showProgressDialog(isShow: Boolean) {
        viewLoading?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onLoadDataSuccess(index: Int) {
        if (index != -1) {
            recyclerView.layoutManager?.scrollToPosition(index + 1)
        }
    }

    override fun onGetServersSuccess(servers: List<Server>?) {
        tabAdapter?.servers = servers?.filter {
            if (screen == RECOMMENDED) {
                return@filter it.recommend == true
            }
            return@filter true
        } ?: listOf()
    }
}
