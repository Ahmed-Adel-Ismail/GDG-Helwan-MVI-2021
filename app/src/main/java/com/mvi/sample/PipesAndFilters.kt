@file:Suppress("FunctionName")

package com.mvi.sample

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

@DslMarker
annotation class FeatureDsl


class StreamsFeatureViewModel(
    override val commands: MutableLiveData<StreamData> = MutableLiveData(),
    override val queries: MutableLiveData<StreamData> = MutableLiveData(),
    override val commandsJobs: MutableMap<KClass<*>, Job?> = mutableMapOf(),
    override val queriesJobs: MutableMap<KClass<*>, Job?> = mutableMapOf()
) : ViewModel(), FeatureViewModel {
    override fun onCleared() {
        super.onCleared()
        commandsJobs.values.forEach { it?.cancel() }
        queriesJobs.values.forEach { it?.cancel() }
    }
}

@FeatureDsl
fun LifecycleOwner.feature(
    mainDispatcher: CoroutineContext = Dispatchers.Main,
    backgroundDispatcher: CoroutineContext = Dispatchers.IO,
    builder: FeatureBuilder<StreamsFeatureViewModel>.() -> Unit
) = FeatureBuilder(
    lifecycleOwner = this,
    viewModel = viewModelFactory(StreamsFeatureViewModel::class),
    mainDispatcher = mainDispatcher,
    backgroundDispatcher = backgroundDispatcher
)

private fun <T> LifecycleOwner.viewModelFactory(viewModelType: KClass<T>) where T : ViewModel, T : FeatureViewModel =
    when (this) {
        is AppCompatActivity -> ViewModelProvider(this)[viewModelType.java]
        is Fragment -> ViewModelProvider(this)[viewModelType.java]
        else -> viewModelType.java.newInstance()
    }


class FeatureBuilder<T> internal constructor(
    val lifecycleOwner: LifecycleOwner?,
    val viewModel: T,
    val components: MutableList<Lazy<FeatureComponent>> = mutableListOf(),
    val mainDispatcher: CoroutineContext = Dispatchers.IO,
    val backgroundDispatcher: CoroutineContext = Dispatchers.IO
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
            override suspend fun onReceive(streams: FeatureStream) {
                componentImplementation(streams)
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

interface FeatureViewModel : FeatureComponent {
    val commands: MutableLiveData<StreamData>
    val queries: MutableLiveData<StreamData>
    val commandsJobs: MutableMap<KClass<*>, Job?>
    val queriesJobs: MutableMap<KClass<*>, Job?>
}

class FeatureStream(
    val viewModel: FeatureViewModel,
    val streamData: StreamData
) {

    @FeatureDsl
    fun cancel(beforeStop: (() -> Unit)? = null) {
        beforeStop?.invoke()
        throw CancelStreamException
    }

    @FeatureDsl
    inline fun <reified T> postCommand(data: T? = null) {
        viewModel.commands.postValue(StreamData(data))
    }

    @FeatureDsl
    inline fun <reified T> postQuery(data: T? = null) {
        viewModel.queries.postValue(StreamData(data))
    }

    @FeatureDsl
    inline fun <reified T> onReceive(handler: (T) -> Unit): FeatureStream {
        val data = streamData.getNullableData<T>() ?: return this
        handler(data)
        return this
    }

    private object CancelStreamException : RuntimeException()

}


interface FeatureComponent {
    @FeatureDsl
    suspend fun onReceive(streams: FeatureStream): Any? = Unit
}

@FeatureDsl
suspend fun List<Lazy<FeatureComponent>>.onReceive(streams: FeatureStream) =
    forEach { it.value.onReceive(streams) }

@FeatureDsl
inline fun <reified T> StreamData(data: T? = null) = StreamData(T::class, data)


data class StreamData(
    val type: KClass<*>,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    inline fun <reified T> getNullableData(): T? = data?.takeIf { it is T }?.let { it as T }

    fun updateTimestamp() = copy(timestamp = System.currentTimeMillis())
}


