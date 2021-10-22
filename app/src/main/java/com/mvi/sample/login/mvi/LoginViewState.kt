package com.mvi.sample.login.mvi

import com.mvi.sample.login.UserToken

/**
 * UI Model / ViewState
 */
data class LoginViewState(
    val progressBarVisible: Boolean,
    val errorMessage: Throwable?,
    val userToken: UserToken?
)