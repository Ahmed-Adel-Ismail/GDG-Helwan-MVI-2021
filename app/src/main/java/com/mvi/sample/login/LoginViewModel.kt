package com.mvi.sample.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    val progressVisibility = MutableLiveData(false)
    val errorMessage = MutableLiveData<Throwable>()
    val userToken = MutableLiveData<UserToken>()

    fun login(userName: String?, password: String?) {
        try {
            progressVisibility.value = true
            userToken.value = userLogin(UserCredentials(userName, password))
        } catch (error: Throwable) {
            progressVisibility.value = false
            errorMessage.value = error
        }
    }
}
