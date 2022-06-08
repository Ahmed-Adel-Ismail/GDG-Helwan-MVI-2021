package com.mvi.sample.cqrs.core.engine

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class FeatureBuilder<T> internal constructor(
    val lifecycleOwner: LifecycleOwner?,
    val viewModel: T,
    val mainDispatcher: CoroutineContext,
    val backgroundDispatcher: CoroutineContext,
    val components: MutableList<Lazy<FeatureComponent>> = mutableListOf()
) where T : ViewModel, T : FeatureViewModel {

    @FeatureDsl
    val with = this

    @FeatureDsl
    infix fun component(component: FeatureComponent): FeatureBuilder<T> {
        components += lazy { component }
        return this
    }

    @FeatureDsl
    infix fun component(componentImplementation: suspend FeatureComponent.(FeatureStream) -> Unit): FeatureBuilder<T> {
        return component(object : FeatureComponent {
            override suspend fun onReceive(stream: FeatureStream) {
                componentImplementation(stream)
            }
        })
    }

    @FeatureDsl
    infix fun component(componentFactory: () -> FeatureComponent): FeatureBuilder<T> {
        components += lazy(componentFactory)
        return this
    }

    @FeatureDsl
    inline infix fun <reified T> render(noinline onBuild: (FeatureStream, T) -> Unit) {

        if (lifecycleOwner != null) {
            viewModel.commands.observe(lifecycleOwner) { onCommand(it, onBuild) }
            viewModel.queries.observe(lifecycleOwner) { onQuery(it, onBuild) }
        } else {
            viewModel.commands.observeForever { onCommand(it, onBuild) }
            viewModel.queries.observeForever { onQuery(it, onBuild) }
        }

    }


    inline fun <reified T> onCommand(
        data: StreamData,
        noinline onUpdate: (FeatureStream, T) -> Unit
    ) {
        val stream = FeatureStream(viewModel, data)
        viewModel.commandsJobs[data.type]?.cancel()
        viewModel.commandsJobs[data.type] = viewModel.viewModelScope.launch(backgroundDispatcher) {
            runCatching {
                withContext(mainDispatcher) { invokeOnUpdate(data, stream, onUpdate) }
                viewModel.onReceive(stream)
                components.onReceive(stream)
            }.onFailure { it.printStackTrace() }
        }
    }

    inline fun <reified T> invokeOnUpdate(
        data: StreamData,
        stream: FeatureStream,
        onUpdate: (FeatureStream, T) -> Unit
    ) {
        data.getNullableData<T>()?.also { onUpdate(stream, it) }
    }

    inline fun <reified T> onQuery(
        data: StreamData,
        noinline onUpdate: (FeatureStream, T) -> Unit
    ) {
        val stream = FeatureStream(viewModel, data)
        viewModel.queriesJobs[data.type]?.cancel()
        viewModel.queriesJobs[data.type] = viewModel.viewModelScope.launch(backgroundDispatcher) {
            runCatching {
                components.onReceive(stream)
                viewModel.onReceive(stream)
                withContext(mainDispatcher) { invokeOnUpdate(data, stream, onUpdate) }
            }.onFailure { it.printStackTrace() }
        }
    }
}

@FeatureDsl
suspend fun List<Lazy<FeatureComponent>>.onReceive(streams: FeatureStream) =
    forEach { it.value.onReceive(streams) }
