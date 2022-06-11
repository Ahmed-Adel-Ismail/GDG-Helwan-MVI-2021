package com.mvi.sample.adapters.mvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mvi.core.ports.PresentationPortMvp
import com.mvi.core.ports.PresentationPortMvpView
import com.mvi.core.ports.onDetectUserLocation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PresentationAdapterMvp @JvmOverloads constructor(
    override var view: PresentationPortMvpView,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), PresentationPortMvp {

    private var detectUserJob: Job? = null

    fun detectUserLocation() {
        detectUserJob?.cancel()
        detectUserJob = viewModelScope.launch(dispatcher) {
            onDetectUserLocation()
        }
    }

    override fun onCleared() {
        detectUserJob?.cancel()
    }

}

class PresentationAdapterMvpFactory(private val view: PresentationPortMvpView) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PresentationAdapterMvp(view) as T
    }

}