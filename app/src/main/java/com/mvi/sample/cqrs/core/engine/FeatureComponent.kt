package com.mvi.sample.cqrs.core.engine

interface FeatureComponent {
    @FeatureDsl
    suspend fun onReceive(stream: FeatureStream): Any? = Unit
}