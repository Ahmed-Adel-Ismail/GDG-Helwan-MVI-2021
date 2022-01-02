package com.mvi.sample.login.cqrs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mvi.sample.Pipe
import com.mvi.sample.PipeData
import com.mvi.sample.login.UserCredentials
import com.mvi.sample.login.UserToken
import com.mvi.sample.login.userLogin
import com.mvi.sample.withType

class LoginViewModel(
    override val intents: MutableLiveData<PipeData> = MutableLiveData(),
    override val viewStates: MutableLiveData<PipeData> = MutableLiveData()
) : ViewModel(), Pipe.ViewModelFilter {


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


