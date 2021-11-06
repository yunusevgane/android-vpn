package com.eskimobile.jetvpn.common.di.component

import android.app.Application
import com.eskimobile.jetvpn.common.di.module.AppModule
import com.eskimobile.jetvpn.common.di.module.RepositoryModule
import com.eskimobile.jetvpn.presentation.auth.AuthFragment
import com.eskimobile.jetvpn.presentation.auth.ForgotPasswordFragment
import com.eskimobile.jetvpn.presentation.home.HomeFragment
import com.eskimobile.jetvpn.presentation.main.MainFragment
import com.eskimobile.jetvpn.presentation.password.ChangePasswordFragment
import com.eskimobile.jetvpn.presentation.other.policy.PrivatePolicyFragment
import com.eskimobile.jetvpn.presentation.premium.PremiumFragment
import com.eskimobile.jetvpn.presentation.profile.EditProfileFragment
import com.eskimobile.jetvpn.presentation.servers.ServerListFragment
import com.eskimobile.jetvpn.presentation.servers.tab.TabFragment
import com.eskimobile.jetvpn.presentation.splash.SplashFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {
    fun inject(splashFragment: SplashFragment)
    fun inject(editProfileFragment: EditProfileFragment)
    fun inject(mainFragment: MainFragment)
    fun inject(premiumFragment: PremiumFragment)
    fun inject(homeFragment: HomeFragment)
    fun inject(serverListFragment: ServerListFragment)
    fun inject(tabFragment: TabFragment)
    fun inject(authFragment: AuthFragment)
    fun inject(forgotPasswordFragment: ForgotPasswordFragment)
    fun inject(privatePolicyFragment: PrivatePolicyFragment)
    fun inject(changePasswordFragment: ChangePasswordFragment)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}