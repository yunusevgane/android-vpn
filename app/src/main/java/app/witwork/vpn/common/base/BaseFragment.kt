package com.eskimobile.jetvpn.common.base;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eskimobile.jetvpn.common.MyApp
import com.eskimobile.jetvpn.common.di.component.AppComponent
import com.eskimobile.jetvpn.presentation.main.MainActivity
import com.eskimobile.jetvpn.presentation.other.NetworkErrorActivity

abstract class BaseFragment<V : BaseView?, P : BasePresenter<V>?> : Fragment(),
    BaseView {
    private val idLayoutRes: Int
        get() {
            return getLayoutRes()
        }

    private val basePresenter: BasePresenter<V>?
        get() {
            return presenter()
        }

    protected val hasInternet: Boolean
        get() {
            return (activity as? BaseActivity)?.hasInternet ?: false
        }

    abstract fun inject(appComponent: AppComponent)
    abstract fun getLayoutRes(): Int
    abstract fun initView()
    open fun initData() {}
    abstract fun presenter(): BasePresenter<V>?
    abstract fun viewIF(): V?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(MyApp.self.appComponent)
        initPresenter()
    }

    private fun initPresenter() {
        basePresenter?.let {
            it.view = viewIF()
            lifecycle.addObserver(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(idLayoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    override fun showProgressDialog(isShow: Boolean) {
    }

    override fun showError(throwable: Throwable) {

    }

    fun onBackPressed(): Boolean {
        return false
    }

    fun showNetworkError() {
        NetworkErrorActivity.start(context)
    }

    fun showSnackbarInMain(message: String) {
        (activity as? MainActivity)?.showSnackBar(message)
    }
}