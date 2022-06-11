package com.mvi.sample.adapters.mvi

import com.mvi.core.ports.PresentationPortMvi.State

sealed class Intents {

    abstract val state: State

    class OnDetectUserLocation(override val state: State = State()) : Intents()
    class OnMoveToPointOfInterest(override val state: State) : Intents()

}