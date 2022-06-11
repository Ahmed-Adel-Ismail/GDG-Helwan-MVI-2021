package com.mvi.sample.adapters.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvi.core.entities.LocationInfo
import com.mvi.core.entities.PointOfInterest
import com.mvi.core.ports.PresentationPortMvvm
import com.mvi.core.ports.onDetectUserLocation
import com.mvi.sample.adapters.emitInto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PresentationAdapterMvvm(
    val progressStream: MutableStateFlow<Boolean?> = MutableStateFlow(null),
    val userLocationStream: MutableStateFlow<LocationInfo?> = MutableStateFlow(null),
    val nearbyPointsOfInterestStream: MutableStateFlow<List<PointOfInterest>> = MutableStateFlow(listOf()),
    val errorsStream: MutableSharedFlow<Throwable> = MutableSharedFlow(onBufferOverflow = DROP_OLDEST),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(), PresentationPortMvvm {

    override var progressing by emitInto(progressStream)
    override var locationOnMap by emitInto(userLocationStream)
    override var nearbyPointsOfInterest by emitInto(nearbyPointsOfInterestStream)
    override var errors by emitInto(errorsStream)


    init {
        detectUserLocation()
    }

    fun detectUserLocation() {
        viewModelScope.launch(dispatcher) {
            onDetectUserLocation()
        }
    }


}

