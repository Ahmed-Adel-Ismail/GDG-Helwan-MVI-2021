package com.mvi.sample.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.mvi.sample.R
import com.mvi.sample.login.mvi.LoginIntents
import com.mvi.sample.login.mvi.LoginViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    val intents = MutableLiveData<LoginIntents>()
    val viewStates = MutableLiveData<LoginViewState>()

    private val errorMessageView: TextView by lazy { TODO() }
    private val progressBar: ProgressBar by lazy { TODO() }
    private val loginButton: View by lazy { TODO() }
    private val userName: EditText by lazy { TODO() }
    private val password: EditText by lazy { TODO() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewStates.value = LoginViewState(
            progressBarVisible = false,
            errorMessage = null,
            userToken = null
        )

        viewStates.observe(this) {
            view(it)
        }

        intents.observe(this) {
            model(it)
        }
    }

    // @Composable
    fun view(viewState: LoginViewState) {

        progressBar.isVisible = viewState.progressBarVisible

        if(viewState.errorMessage != null){
            errorMessageView.text = viewState.errorMessage.toString()
            intents.value = LoginIntents.ErrorDisplayed()
        }else{
            errorMessageView.text = null
        }

        if (viewState.userToken != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            intents.value = LoginIntents.LoginClicked(
                userName = userName.text?.toString(),
                password = password.text?.toString()
            )
        }
    }

    fun model(intent: LoginIntents) {
        when (intent) {
            is LoginIntents.LoginClicked -> onLoginClicked(intent)
            is LoginIntents.ErrorDisplayed -> onErrorDisplayed(intent)
        }
    }


    private fun onLoginClicked(intent: LoginIntents.LoginClicked) {
        val enableProgressViewState = LoginViewState(
            progressBarVisible = true,
            errorMessage = null,
            userToken = null
        )

        viewStates.value = enableProgressViewState

        try{
            val token = userLogin(UserCredentials(intent.userName, intent.password))
            val successViewState = LoginViewState(
                progressBarVisible = false,
                errorMessage = null,
                userToken = token
            )
            viewStates.value = successViewState
        }catch (throwable: Throwable){
            val errorViewState = LoginViewState(
                progressBarVisible = false,
                errorMessage = throwable,
                userToken = null
            )
            viewStates.value = errorViewState
        }



    }

    private fun onErrorDisplayed(intent: LoginIntents.ErrorDisplayed) {
        // sleep 3 seconds in background
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000)
            val neutralViewState = LoginViewState(
                progressBarVisible = false,
                errorMessage = null,
                userToken = null
            )
            viewStates.postValue(neutralViewState)
        }

    }


}






