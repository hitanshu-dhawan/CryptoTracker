package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
     * Handles UI actions.
     *
     * @param action The user action to be handled.
     */
    fun onAction(action: CoinListAction) {
        when (action) {
            is CoinListAction.OnCoinClick -> {
                // TODO
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
                }
        }
    }

}
