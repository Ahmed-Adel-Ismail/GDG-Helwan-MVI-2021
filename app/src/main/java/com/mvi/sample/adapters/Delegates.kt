package com.mvi.sample.adapters

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.Delegates

fun <T> emitInto(flow: MutableStateFlow<T>) = Delegates.observable<T?>(null) { _, _, value ->
    flow.tryEmit(value ?: return@observable)
}

fun <T> emitInto(flow: MutableSharedFlow<T>) = Delegates.observable<T?>(null) { _, _, value ->
    flow.tryEmit(value ?: return@observable)
}