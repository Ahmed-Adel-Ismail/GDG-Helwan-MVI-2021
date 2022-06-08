@file:Suppress("FunctionName")

package com.mvi.sample.cqrs.core.engine

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mvi.sample.cqrs.core.engine.FeatureBuilder
import com.mvi.sample.cqrs.core.engine.FeatureDsl
import com.mvi.sample.cqrs.core.engine.FeatureViewModel
import com.mvi.sample.cqrs.core.engine.StreamData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass


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
).apply(builder)

private fun <T> LifecycleOwner.viewModelFactory(viewModelType: KClass<T>) where T : ViewModel, T : FeatureViewModel =
    when (this) {
        is AppCompatActivity -> ViewModelProvider(this)[viewModelType.java]
        is Fragment -> ViewModelProvider(this)[viewModelType.java]
        else -> viewModelType.java.newInstance()
    }

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

