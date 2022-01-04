package com.mvi.sample.login.cqrs

import com.mvi.sample.FeatureComponent
import com.mvi.sample.FeatureStream

class LoginDataSources(private val server: Server = Server()) : FeatureComponent {
    override suspend fun onReceive(streams: FeatureStream) =
        streams.onReceive<Login.Command.OnRequest> {
            streams.postQuery(
                runCatching {
                    Login.Query.OnResponse(
                        data = it.data.copy(
                            token = server.requestLogin(it.data.userName, it.data.password).token
                        )
                    )
                }.getOrElse { error ->
                    Login.Query.OnResponse(
                        data = it.data.copy(
                            error = error
                        )
                    )
                }
            )
        }

}


class Server {
    suspend fun requestLogin(userName: String?, password: String?) = LoginResponse("TOKEN")

    data class LoginResponse(val token: String)
}