package com.mvi.sample.cqrs.core.engine

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

interface FeatureViewModel : FeatureComponent {
    val commands: MutableLiveData<StreamData>
    val queries: MutableLiveData<StreamData>
    val commandsJobs: MutableMap<KClass<*>, Job?>
    val queriesJobs: MutableMap<KClass<*>, Job?>
}