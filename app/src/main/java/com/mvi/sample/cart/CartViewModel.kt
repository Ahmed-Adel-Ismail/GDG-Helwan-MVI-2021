package com.mvi.sample.cart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.sample.cart.pattern.CartIntents
import com.mvi.sample.cart.pattern.CartViewState
import com.mvi.sample.mapIntentToViewState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(
    val intents: MutableLiveData<CartIntents> = MutableLiveData(CartIntents.Initialize),
    val viewStates: MutableLiveData<CartViewState> = MutableLiveData(),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val viewStateMapper: suspend (CartIntents) -> CartViewState = { mapIntentToViewState(it) }
) : ViewModel() {

    private val observer = Observer<CartIntents> {
        cancelOngoingViewStateJob(it)
        updateViewStateWithStoppedProgress(it)
        launchNextViewStateJob(it)
    }

    init {
        intents.observeForever(observer)
    }

    private fun cancelOngoingViewStateJob(intents: CartIntents) = intents.viewState?.progress?.cancel()

    private fun updateViewStateWithStoppedProgress(intents: CartIntents) {
        viewStates.value = intents.viewState?.copy(progress = null, error = null)
    }

    private fun launchNextViewStateJob(intents: CartIntents) {
        viewStates.value = intents.viewState?.copy(progress = nextViewStateJob(intents))
    }

    private fun nextViewStateJob(intents: CartIntents) = viewModelScope.launch(dispatcher) {
        viewStates.postValue(viewStateMapper(intents).copy(progress = null))
    }

    override fun onCleared() = intents.removeObserver(observer)
}

