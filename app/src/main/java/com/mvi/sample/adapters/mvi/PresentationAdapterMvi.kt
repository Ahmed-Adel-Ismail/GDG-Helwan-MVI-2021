package com.mvi.sample.adapters.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.core.ports.Cancellable
import com.mvi.core.ports.PresentationPortMvi
import com.mvi.core.ports.PresentationPortMvi.State
import com.mvi.core.ports.onDetectUserLocation
import com.mvi.sample.adapters.emitInto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PresentationAdapterMvi(
    val intentsStream: MutableSharedFlow<Intents?> = MutableStateFlow(Intents.OnDetectUserLocation()),
    val viewStatesStream: MutableStateFlow<State?> = MutableStateFlow(null),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), PresentationPortMvi {

    override var cancellable: Cancellable? = null
    override var state by emitInto(viewStatesStream)

    init {
        viewModelScope.launch(dispatcher) {
            intentsStream.asSharedFlow().filterNotNull().collect { intent ->
                when (intent) {
                    is Intents.OnDetectUserLocation -> onDetectUserLocation(intent.state)
                    // handle more intents here
                    else -> throw UnsupportedOperationException("intent not handled")
                }
            }
        }
    }

    override fun onCleared() {
        cancellable?.cancel()
    }
}