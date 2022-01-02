package com.mvi.sample.login.cqrs

import com.mvi.sample.PipeData
import com.mvi.sample.PipeFilter
import com.mvi.sample.mapNotNull

class LoginRepository(
    val dataSource : Server = Server()
) : PipeFilter {
    override fun invoke(pipeData: PipeData) = pipeData
        .mapNotNull<Login.Command.OnRequest, Login.Query.OnResponse> {
            dataSource.requestLogin(it.state.userName,)
        }
}


class Server {
    suspend fun requestLogin(userName: String?, password: String?) =
}