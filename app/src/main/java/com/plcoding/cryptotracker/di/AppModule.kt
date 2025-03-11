package com.plcoding.cryptotracker.di

import com.plcoding.cryptotracker.core.data.networking.HttpClientFactory
import com.plcoding.cryptotracker.crypto.data.networking.RemoteCoinDataSource
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.coin_list.CoinListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Dependency Injection module using Koin.
 * This module provides dependencies required for the CryptoTracker app.
 */
val appModule = module {

    /**
     * Provides a singleton instance of an HTTP client using the CIO engine.
     * This client is used for networking in the application.
     */
    single { HttpClientFactory.create(CIO.create()) }

    /**
     * Provides a singleton instance of [RemoteCoinDataSource] and binds it to the [CoinDataSource] interface.
     * This allows dependency injection to use [CoinDataSource] wherever needed.
     */
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()

    /**
     * Provides a ViewModel instance of [CoinListViewModel] using Koin's ViewModel DSL.
     */
    viewModelOf(::CoinListViewModel)

}
