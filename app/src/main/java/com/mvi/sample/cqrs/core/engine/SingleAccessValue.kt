package com.mvi.sample.cqrs.core.engine

data class SingleAccessValue<T>(private var value: T? = null) : () -> T? {
    val isNotNull: Boolean get() = value != null
    override fun invoke(): T? = value?.also { value = null }
}