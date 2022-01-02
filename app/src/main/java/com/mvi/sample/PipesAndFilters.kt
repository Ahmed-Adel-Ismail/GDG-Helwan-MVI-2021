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

// integration module (android)

@PipeDsl
fun <T> AppCompatActivity.Pipe(
    viewModelType: KClass<T>,
    backgroundDispatcher: CoroutineContext = Dispatchers.IO
) where T : ViewModel, T : Pipe.ViewModelFilter = Pipe(
    hostActivity = this,
    viewModel = viewModelType as KClass<ViewModel>,
    backgroundDispatcher = backgroundDispatcher
)

@PipeDsl
fun <T> Fragment.Pipe(
    viewModelType: KClass<T>,
    backgroundDispatcher: CoroutineContext = Dispatchers.IO
) where T : ViewModel, T : Pipe.ViewModelFilter = Pipe(
    hostFragment = this,
    viewModel = viewModelType as KClass<ViewModel>,
    backgroundDispatcher = backgroundDispatcher
)


class Pipe internal constructor(
    private val hostActivity: AppCompatActivity? = null,
    private val hostFragment: Fragment? = null,
    private var viewModel: KClass<ViewModel>? = null,
    private val backgroundDispatcher: CoroutineContext = Dispatchers.IO
) {

    private var businessRules = mutableListOf<() -> PipeFilter>()
    private var repositories = mutableListOf<() -> PipeFilter>()

    @PipeDsl
    fun addBusinessRule(businessRuleFactory: () -> PipeFilter): Pipe {
        businessRules += businessRuleFactory
        return this
    }

    @PipeDsl
    fun addRepository(repositoryFactory: () -> PipeFilter): Pipe {
        repositories += repositoryFactory
        return this
    }

    @PipeDsl
    fun observe(onUpdate: (Stream) -> Unit) {
        val (viewModel, lifecycleOwner, scope) = assertBuilderComplete(hostActivity, hostFragment)

        val jobs = mutableMapOf<KClass<*>, Job?>()
        viewModel.intents.observe(lifecycleOwner) { pipeData ->
            jobs[pipeData.type]?.cancel()
            jobs[pipeData.type] = scope.launch(backgroundDispatcher) {
                viewModel.mapNonNull(pipeData)
                    .let { businessRules.lazyInvoke(it) }
                    .let { repositories.lazyInvoke(it) }
                    .let { businessRules.lazyInvoke(it) }
                    .let { viewModel.mapNonNull(it) }
                    .let(::PipeData)
                    .let(viewModel.viewStates::postValue)
            }
        }

        viewModel.viewStates.observe(lifecycleOwner) {
            onUpdate(Stream(viewModel.intents, it))
        }

    }

    private fun assertBuilderComplete(
        hostActivity: AppCompatActivity?,
        hostFragment: Fragment?
    ): Triple<ViewModelFilter, LifecycleOwner, CoroutineScope> {

        val lifecycleOwner: LifecycleOwner
        val finalViewModelType =
            viewModel ?: throw IllegalStateException("viewModel must not be null")

        val viewModel = when {
            hostActivity != null -> {
                lifecycleOwner = hostActivity
                ViewModelProvider(hostActivity)[finalViewModelType.java] as ViewModelFilter
            }
            hostFragment != null -> {
                lifecycleOwner = hostFragment
                ViewModelProvider(hostFragment)[finalViewModelType.java] as ViewModelFilter
            }
            else -> throw UnsupportedOperationException("hostActivity or hostFragment must not be null")
        }
        return Triple(viewModel, lifecycleOwner, (viewModel as ViewModel).viewModelScope)
    }


    class Stream(val liveData: MutableLiveData<PipeData>, val pipeData: PipeData) {

        @PipeDsl
        inline fun <reified T> post(data: T? = null) {
            liveData.postValue(PipeData(data))
        }

        @PipeDsl
        inline fun <reified T> onReceive(handler: (T) -> Unit) {
            val data = pipeData.getNullableData<T>() ?: return
            handler(data)
        }
    }

    interface ViewModelFilter : PipeFilter {
        val intents: MutableLiveData<PipeData>
        val viewStates: MutableLiveData<PipeData>
    }

}


// core module (kotlin)

@DslMarker
annotation class PipeDsl


interface PipeFilter {

    @PipeDsl
    suspend fun mapNonNull(pipeData: PipeData): PipeData = pipeData
}

private suspend fun List<() -> PipeFilter>.lazyInvoke(initialParameter: PipeData) =
    fold(initialParameter) { first, second -> second().mapNonNull(first) }

@PipeDsl
inline fun <reified T> PipeData(data: T? = null) = PipeData(T::class, data)

@PipeDsl
inline fun <reified T, reified R> PipeData.mapNotNull(mapper: (T) -> R): PipeData {
    val data = getNullableData<T>() ?: return this
    return PipeData(mapper(data))
}

data class PipeData(
    val type: KClass<*>,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    inline fun <reified T> getNullableData(): T? = data?.takeIf { it is T }?.let { it as T }

    fun updateTimestamp() = copy(timestamp = System.currentTimeMillis())
}


@PipeDsl
inline fun <reified T> withPipeData(data: T? = null) = PipeData(T::class, data)


