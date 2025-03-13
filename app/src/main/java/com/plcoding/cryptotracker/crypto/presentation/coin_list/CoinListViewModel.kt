package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.chart.DataPoint
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * ViewModel responsible for managing the state of the coin list UI.
 * It fetches coin data from [CoinDataSource] and updates the UI state accordingly.
 *
 * @property coinDataSource The data source providing coin information.
 */
class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    // Backing property for UI state
    private val _state = MutableStateFlow(CoinListState())

    /**
     * Publicly exposed state as a state flow, ensuring immutability.
     * Uses [stateIn] to keep the flow active while subscribed.
     */
    val state: StateFlow<CoinListState> = _state
        .onStart { loadCoins() } // Load coins when the state starts
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L), // Keep active while subscribed, with a timeout of 5 seconds.
            CoinListState()
        )

    /**
     * A channel for emitting `CoinListEvent` instances and exposing them as a Flow.
     * The `_events` channel is used internally to send events, while `events` provides
     * a public Flow for consumers to collect these events.
     */
    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()


    /**
     * Handles UI actions.
     *
     * @param action The user action to be handled.
     */
    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
        }
    }

    /**
     * Fetches coin data from [coinDataSource] and updates the state.
     * Displays loading state while fetching and handles success or error responses.
     */
    private fun loadCoins() {
        viewModelScope.launch {
            // Set loading state to true before fetching data
            _state.update { it.copy(isLoading = true) }

            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = coins.map { it.toCoinUi() } // Convert domain models to UI models
                        )
                    }
                }
                .onError { error ->
                    // Set loading state to false in case of an error
                    _state.update { it.copy(isLoading = false) }
                    // Send error event to view
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

    /**
     * Selects a coin from the list and fetches its historical data.
     *
     * This function updates the selected coin state and fetches the coin's price history
     * for the last 5 days asynchronously. If the fetch is successful, the history is printed;
     * otherwise, an error event is sent.
     *
     * @param coinUi The selected coin UI model.
     */
    private fun selectCoin(coinUi: CoinUi) {
        // Update the UI state with the selected coin
        _state.update { it.copy(selectedCoin = coinUi) }

        // Launch a coroutine within the ViewModel's scope
        viewModelScope.launch {
            coinDataSource
                .getCoinHistory(
                    coinId = coinUi.id, // Fetch historical data for the selected coin
                    start = ZonedDateTime.now().minusDays(5), // Start date: 5 days ago
                    end = ZonedDateTime.now() // End date: Current date
                )
                .onSuccess { history ->

                    // Sort the historical data points by dateTime in ascending order
                    val dataPoints = history
                        .sortedBy { it.dateTime }
                        .map {
                            DataPoint(
                                x = it.dateTime.hour.toFloat(), // Extract the hour component as the x-axis value
                                y = it.priceUsd.toFloat(), // Convert price to float for y-axis value
                                xLabel = DateTimeFormatter
                                    .ofPattern("ha\nM/d") // Format dateTime to display hour (AM/PM) and month/day
                                    .format(it.dateTime)
                            )
                        }

                    // Update the state by setting the transformed data points in the selectedCoin
                    _state.update {
                        it.copy(selectedCoin = it.selectedCoin?.copy(coinPriceHistory = dataPoints))
                    }
                }
                .onError { error ->
                    // Send an error event to notify observers
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }

}
