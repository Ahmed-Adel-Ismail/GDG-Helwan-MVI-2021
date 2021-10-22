package com.mvi.sample.login

import android.accounts.NetworkErrorException
import kotlinx.coroutines.delay


// domain code

// entities

data class UserCredentials(val userName: String? = null, val password: String? = null)

data class UserToken(val value: String? = null)


// use cases

@Throws(NetworkErrorException::class, IllegalArgumentException::class)
fun userLogin(userCredentials: UserCredentials) : UserToken{
    return UserToken("XisuoD20ljm3Und-Id3")
}

