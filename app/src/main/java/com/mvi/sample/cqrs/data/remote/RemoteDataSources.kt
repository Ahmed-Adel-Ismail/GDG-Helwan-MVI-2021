package com.mvi.sample.cqrs.data.remote

import com.mvi.sample.cqrs.core.business.Authentication
import com.mvi.sample.cqrs.core.engine.FeatureComponent
import com.mvi.sample.cqrs.core.engine.FeatureStream
import com.mvi.sample.cqrs.core.engine.SingleAccessValue

/**
 * this is the only dependency in required in the pipe, and it is an interface that can be mocked in
 * testing
 */
class RemoteDataSources(private val retrofitService: RetrofitService = RetrofitService()) :
    FeatureComponent {
    override suspend fun onReceive(stream: FeatureStream) = with(stream) {
        onReceive<Authentication.Command.SignIn> {
            postQuery(
                runCatching {
                    Authentication.Query.SignIn(
                        data = it.data.copy(
                            token = retrofitService.requestSignIn(
                                it.data.userName,
                                it.data.password
                            ).token
                        )
                    )
                }.getOrElse { error ->
                    Authentication.Query.SignIn(
                        data = it.data.copy(
                            error = SingleAccessValue(error)
                        )
                    )
                }
            )
        }

        onReceive<Authentication.Command.SignUp> {
            postQuery(
                runCatching {
                    Authentication.Query.SignUp(
                        data = it.data.copy(
                            token = retrofitService.requestSignUp(
                                it.data.userName,
                                it.data.password
                            ).token
                        )
                    )
                }.getOrElse { error ->
                    Authentication.Query.SignUp(
                        data = it.data.copy(
                            error = SingleAccessValue(error)
                        )
                    )
                }
            )
        }

    }

}

