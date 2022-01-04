@file:Suppress("FunctionName")

package com.mvi.sample

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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
    private val lifecycleOwner: LifecycleOwner?,
    private val viewModel: T,
    private val components: MutableList<Lazy<FeatureComponent>> = mutableListOf(),
    private val mainDispatcher: CoroutineContext = Dispatchers.IO,
    private val backgroundDispatcher: CoroutineContext = Dispatchers.IO
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
    infix fun render(onBuild: (FeatureStream) -> Unit) {

        if (lifecycleOwner != null) {
            viewModel.commands.observe(lifecycleOwner) { onCommand(it, onBuild) }
            viewModel.queries.observe(lifecycleOwner) { onQuery(it, onBuild) }
        } else {
            viewModel.commands.observeForever { onCommand(it, onBuild) }
            viewModel.queries.observeForever { onQuery(it, onBuild) }
        }


    }


    private fun onCommand(data: StreamData, onUpdate: (FeatureStream) -> Unit) {
        val stream = FeatureStream(viewModel.commands, viewModel.queries, data)
        viewModel.commandsJobs[data.type]?.cancel()
        viewModel.commandsJobs[data.type] = viewModel.viewModelScope.launch(backgroundDispatcher) {
            runCatching {
                launch(mainDispatcher) { onUpdate(stream) }
                launch { viewModel.onReceive(stream) }
                launch { components.onReceiveAsync(this, stream) }
            }.onFailure { it.printStackTrace() }
        }
    }

    private fun onQuery(data: StreamData, onUpdate: (FeatureStream) -> Unit) {
        val stream = FeatureStream(viewModel.commands, viewModel.queries, data)
        viewModel.queriesJobs[data.type]?.cancel()
        viewModel.queriesJobs[data.type] = viewModel.viewModelScope.launch(backgroundDispatcher) {
            runCatching {
                launch { components.onReceiveAsync(this, stream) }
                launch { viewModel.onReceive(stream) }
                launch(mainDispatcher) { onUpdate(stream) }
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
    val commands: MutableLiveData<StreamData>,
    val queries: MutableLiveData<StreamData>,
    val streamData: StreamData
) {

    @FeatureDsl
    inline fun <reified T> postCommand(data: T? = null) {
        commands.postValue(StreamData(data))
    }

    @FeatureDsl
    inline fun <reified T> postQuery(data: T? = null) {
        queries.postValue(StreamData(data))
    }

    @FeatureDsl
    inline fun <reified T> onReceive(handler: (T) -> Unit): FeatureStream {
        val data = streamData.getNullableData<T>() ?: return this
        handler(data)
        return this
    }

}


interface FeatureComponent {
    @FeatureDsl
    suspend fun onReceive(streams: FeatureStream) = Unit
}

@FeatureDsl
fun List<Lazy<FeatureComponent>>.onReceiveAsync(
    coroutineScope: CoroutineScope,
    streams: FeatureStream
) =
    forEach {
        coroutineScope.launch { it.value.onReceive(streams) }
    }

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


