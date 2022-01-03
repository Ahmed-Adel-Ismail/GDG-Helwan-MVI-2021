package com.mvi.sample.login.cqrs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mvi.sample.Pipe
import com.mvi.sample.R
import com.mvi.sample.login.MainActivity

class LoginActivity : AppCompatActivity() {


    private val errorMessageView: TextView by lazy { TODO() }
    private val progressBar: ProgressBar by lazy { TODO() }
    private val loginButton: View by lazy { TODO() }
    private val userName: EditText by lazy { TODO() }
    private val password: EditText by lazy { TODO() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Pipe(LoginViewModel::class)
            .addRepository(::LoginRepository)
            .observe(::render)

    }

    private fun render(stream: Pipe.Stream) {
        val viewState = stream.getData<Login.Data>() ?: return
        progressBar.isVisible = viewState.progressBarVisible

        if (viewState.error != null) {
            errorMessageView.text = viewState.error.toString()
            stream.post(Login.Command.OnErrorDisplayed(viewState))
        } else {
            errorMessageView.text = null
        }

        if (viewState.userToken != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            progressBar.isVisible = true
            stream.post(
                Login.Command.OnRequest(
                    viewState.copy(
                        userName = userName.text?.toString(),
                        password = password.text?.toString()
                    )
                )
            )
        }
    }


}


//
//    // @Composable
//    fun view(viewState: LoginViewState) {
//
//        progressBar.isVisible = viewState.progressBarVisible
//
//        if(viewState.errorMessage != null){
//            errorMessageView.text = viewState.errorMessage.toString()
//            intents.value = LoginIntents.ErrorDisplayed()
//        }else{
//            errorMessageView.text = null
//        }
//
//        if (viewState.userToken != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//
//        loginButton.setOnClickListener {
//            intents.value = LoginIntents.LoginClicked(
//                userName = userName.text?.toString(),
//                password = password.text?.toString()
//            )
//        }
//    }
//
//    fun model(intent: LoginIntents) {
//        when (intent) {
//            is LoginIntents.LoginClicked -> onLoginClicked(intent)
//            is LoginIntents.ErrorDisplayed -> onErrorDisplayed(intent)
//        }
//    }
//
//
//    private fun onLoginClicked(intent: LoginIntents.LoginClicked) {
//        val enableProgressViewState = LoginViewState(
//            progressBarVisible = true,
//            errorMessage = null,
//            userToken = null
//        )
//
//        viewStates.value = enableProgressViewState
//
//        try{
//            val token = userLogin(UserCredentials(intent.userName, intent.password))
//            val successViewState = LoginViewState(
//                progressBarVisible = false,
//                errorMessage = null,
//                userToken = token
//            )
//            viewStates.value = successViewState
//        }catch (throwable: Throwable){
//            val errorViewState = LoginViewState(
//                progressBarVisible = false,
//                errorMessage = throwable,
//                userToken = null
//            )
//            viewStates.value = errorViewState
//        }
//
//
//
//    }
//
//    private fun onErrorDisplayed(intent: LoginIntents.ErrorDisplayed) {
//        // sleep 3 seconds in background
//        GlobalScope.launch(Dispatchers.IO) {
//            delay(3000)
//            val neutralViewState = LoginViewState(
//                progressBarVisible = false,
//                errorMessage = null,
//                userToken = null
//            )
//            viewStates.postValue(neutralViewState)
//        }
//
//    }
//
//
//}






