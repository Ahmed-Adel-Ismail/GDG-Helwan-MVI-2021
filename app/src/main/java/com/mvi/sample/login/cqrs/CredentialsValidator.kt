package com.mvi.sample.login.cqrs

import com.mvi.sample.FeatureComponent
import com.mvi.sample.FeatureStream

class CredentialsValidator : FeatureComponent {

    override suspend fun onReceive(streams: FeatureStream) {
        streams.onReceive<Login.Command.OnRequest> {
            if (it.data.userName.isNullOrEmpty() || it.data.password.isNullOrEmpty()) {
                streams.postQuery(
                    Login.Query.OnResponse(
                        data = it.data.copy(
                            error = IllegalArgumentException("user name or password is not valid")
                        )
                    )
                )
            }
        }
    }
}