package com.plcoding.cryptotracker.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// https://youtu.be/njchj9d_Lf8?si=YnXMR-ug0qKSSZfA

/**
 * A composable function that observes a [Flow] of events and triggers the provided [onEvent] callback
 * whenever a new event is emitted. The collection of events starts when the composable enters the STARTED
 * state of the lifecycle and stops when it leaves this state.
 *
 * @param T The type of event being observed.
 * @param events The [Flow] emitting events that need to be observed.
 * @param key1 An optional key used to control recomposition.
 * @param key2 Another optional key used to control recomposition.
 * @param onEvent A callback function invoked with each emitted event.
 */
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle, key1, key2) {
        // Observes events only when the lifecycle is in the STARTED state
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                // Collects events and invokes the provided callback
                events.collect(onEvent)
            }
        }
    }
}
