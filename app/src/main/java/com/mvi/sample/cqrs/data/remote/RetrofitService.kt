package com.mvi.sample.cqrs.data.remote

import com.mvi.sample.cqrs.data.remote.models.LoginResponse

class RetrofitService {
    suspend fun requestSignIn(userName: String?, password: String?) = LoginResponse("TOKEN")
    suspend fun requestSignUp(userName: String?, password: String?) = LoginResponse("TOKEN")
}