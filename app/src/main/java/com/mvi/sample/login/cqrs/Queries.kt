package com.mvi.sample.login.cqrs

sealed class Query {

}

sealed class LoginQuery {
    data class AuthenticationResponse(val token: String? = null, val error: Throwable? = null) :
        LoginQuery()
}
