package com.mvi.sample.login.cqrs

import com.mvi.sample.login.UserToken



sealed class Login {
    abstract val state: Data

    sealed class Command : Login() {
        class OnRequest(override val state: Data) : Command()
        class OnErrorDisplayed(override val state: Data) : Command()
    }

    sealed class Query : Login() {
        class OnResponse(override val state: Data) : Command()
    }

    data class Data(
        val userName: String? = null,
        val password: String? = null,
        val progressBarVisible: Boolean = false,
        val error: Throwable? = null,
        val userToken: UserToken? = null
    )


}



