package com.mvi.sample.cqrs.core.engine

import kotlin.reflect.KClass

data class StreamData(
    val type: KClass<*>,
    val data: Any? = null,
    val timestamp: Long = System.currentTimeMillis(),
) {
    inline fun <reified T> getNullableData(): T? = data?.takeIf { it is T }?.let { it as T }

    fun updateTimestamp() = copy(timestamp = System.currentTimeMillis())
}