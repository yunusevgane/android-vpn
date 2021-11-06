package com.eskimobile.jetvpn.common.di.module

import com.eskimobile.jetvpn.data.repos.BillingRepositoryImpl
import com.eskimobile.jetvpn.data.repos.ConfigRepositoryImpl
import com.eskimobile.jetvpn.data.repos.ServerRepositoryImpl
import com.eskimobile.jetvpn.data.repos.UserRepositoryImpl
import com.eskimobile.jetvpn.domain.repos.BillingRepository
import com.eskimobile.jetvpn.domain.repos.ConfigRepository
import com.eskimobile.jetvpn.domain.repos.ServerRepository
import com.eskimobile.jetvpn.domain.repos.UserRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindBillingRepository(billingRepositoryImpl: BillingRepositoryImpl): BillingRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindServerRepository(serverRepositoryImpl: ServerRepositoryImpl): ServerRepository

    @Binds
    abstract fun bindConfigRepository(configRepositoryImpl: ConfigRepositoryImpl): ConfigRepository
}