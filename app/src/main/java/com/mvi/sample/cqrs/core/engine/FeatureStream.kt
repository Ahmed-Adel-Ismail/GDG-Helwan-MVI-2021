package com.mvi.sample.cqrs.core.engine

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

@FeatureDsl
inline fun <reified T> StreamData(data: T? = null) = StreamData(T::class, data)
