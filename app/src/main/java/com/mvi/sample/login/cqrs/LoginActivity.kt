package com.mvi.sample.login.cqrs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mvi.sample.FeatureStream
import com.mvi.sample.R
import com.mvi.sample.feature
import com.mvi.sample.login.MainActivity

class LoginActivity : AppCompatActivity() {


    private val errorMessageView: TextView by lazy { TODO() }
    private val progressBar: ProgressBar by lazy { TODO() }
    private val loginButton: View by lazy { TODO() }
    private val userName: EditText by lazy { TODO() }
    private val password: EditText by lazy { TODO() }

    private val server: Server by lazy { Server() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        feature {
            with component ::CredentialsValidator
            with component ::LoginDataSources
            render<Login.Data> { stream, data -> updateViews(data, stream) }
        }


        feature {

            with component { stream ->
                stream.onReceive<Login.Command.OnRequest> {
                    if (it.data.userName.isNullOrEmpty() || it.data.password.isNullOrEmpty()) {
                        stream.cancel {
                            stream.postQuery(
                                Login.Query.OnResponse(
                                    data = it.data.copy(
                                        error = IllegalArgumentException("user name or password is not valid")
                                    )
                                )
                            )
                        }
                    }
                }
            }


            with component { stream ->
                stream.onReceive<Login.Command.OnRequest> {
                    stream.postQuery(
                        runCatching {
                            Login.Query.OnResponse(
                                data = it.data.copy(
                                    token = server.requestLogin(
                                        it.data.userName,
                                        it.data.password
                                    ).token
                                )
                            )
                        }.getOrElse { error ->
                            Login.Query.OnResponse(
                                data = it.data.copy(
                                    error = error
                                )
                            )
                        }
                    )
                }
            }


            render<Login.Data> { stream, data ->

                progressBar.isVisible = data.progressBarVisible

                errorMessageView.text = if (data.error != null) data.error.toString() else null

                if (data.token != null) startMainActivity()

                loginButton.setOnClickListener {
                    progressBar.isVisible = true
                    stream.postCommand(
                        Login.Command.OnRequest(
                            data.copy(
                                userName = userName.text?.toString(),
                                password = password.text?.toString()
                            )
                        )
                    )
                }
            }

        }


    }

    private fun updateViews(data: Login.Data, stream: FeatureStream) {
        progressBar.isVisible = data.progressBarVisible

        errorMessageView.text = if (data.error != null) data.error.toString() else null

        if (data.token != null) startMainActivity()

        loginButton.setOnClickListener {
            progressBar.isVisible = true
            stream.postCommand(
                Login.Command.OnRequest(
                    data.copy(
                        userName = userName.text?.toString(),
                        password = password.text?.toString()
                    )
                )
            )
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}







