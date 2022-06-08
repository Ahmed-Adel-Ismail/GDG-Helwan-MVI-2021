package com.mvi.sample.cqrs.core.business

import com.mvi.sample.cqrs.core.engine.FeatureComponent
import com.mvi.sample.cqrs.core.engine.FeatureStream
import com.mvi.sample.cqrs.core.engine.SingleAccessValue


sealed class Authentication {
    abstract val data: Data

    sealed class Command : Authentication() {
        class SignUp(override val data: Data) : Command()
        class SignIn(override val data: Data) : Command()
    }

    sealed class Query : Authentication() {
        class SignUp(override val data: Data) : Query()
        class SignIn(override val data: Data) : Query()
    }

    data class Data(
        val userName: String? = null,
        val password: String? = null,
        val token: String? = null,
        val error: SingleAccessValue<Throwable>? = null
    )


}


class SignUpValidator : FeatureComponent {

    override suspend fun onReceive(stream: FeatureStream) = with(stream) {
        onReceive<Authentication.Command.SignUp> {
            if (isInvalidCredentials(it.data)) cancel {
                postQuery(
                    Authentication.Query.SignUp(
                        data = it.data.copy(
                            error = SingleAccessValue(
                                IllegalArgumentException("user name or password is not valid for signup")
                            )
                        )
                    )
                )
            }
        }

    }

}

class SignInValidator : FeatureComponent {

    override suspend fun onReceive(stream: FeatureStream) = with(stream) {
        onReceive<Authentication.Command.SignIn> {
            if (isInvalidCredentials(it.data)) cancel {
                postQuery(
                    Authentication.Query.SignIn(
                        data = it.data.copy(
                            error = SingleAccessValue(
                                IllegalArgumentException("user name or password is not valid for signIn")
                            )
                        )
                    )
                )
            }
        }
    }
}


private fun isInvalidCredentials(data: Authentication.Data) =
    data.userName.isNullOrEmpty() || data.password.isNullOrEmpty()




