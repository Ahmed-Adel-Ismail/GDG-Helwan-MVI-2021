package com.mvi.sample.login.cqrs




sealed class Login {
    abstract val data: Data

    sealed class Command : Login() {
        class OnRequest(override val data: Data) : Command()
    }

    sealed class Query : Login() {
        class OnResponse(override val data: Data) : Command()
    }

    data class Data(
        val userName: String? = null,
        val password: String? = null,
        val progressBarVisible: Boolean = false,
        val error: Throwable? = null,
        val token: String? = null
    )


}



