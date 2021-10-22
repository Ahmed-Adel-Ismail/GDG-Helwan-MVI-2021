package com.mvi.sample.login.mvi

/**
 * Intents
 */
sealed class LoginIntents {
    class LoginClicked(val userName: String?, val password: String?) : LoginIntents()
    class ErrorDisplayed : LoginIntents()
}