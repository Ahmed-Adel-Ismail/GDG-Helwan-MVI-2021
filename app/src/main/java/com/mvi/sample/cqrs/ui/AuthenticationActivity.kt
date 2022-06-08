package com.mvi.sample.cqrs.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.mvi.sample.R
import com.mvi.sample.cqrs.core.business.Authentication
import com.mvi.sample.cqrs.core.business.SignInValidator
import com.mvi.sample.cqrs.core.business.SignUpValidator
import com.mvi.sample.cqrs.core.engine.feature
import com.mvi.sample.cqrs.data.remote.RemoteDataSources
import com.mvi.sample.login.MainActivity

class AuthenticationActivity : AppCompatActivity() {


    private val errorMessageView: TextView by lazy { TODO() }
    private val progressBar: ProgressBar by lazy { TODO() }
    private val signInButton: View by lazy { TODO() }
    private val signUpButton: View by lazy { TODO() }
    private val userName: EditText by lazy { TODO() }
    private val password: EditText by lazy { TODO() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // should be done in the default visibility of the view
        progressBar.isVisible = true

        feature {
            with component ::SignUpValidator
            with component ::SignInValidator
            with component ::RemoteDataSources
            render<Authentication.Data> { stream, data ->
                progressBar.isVisible = false

                errorMessageView.text = data.error?.toString()

                if (data.token != null) startMainActivity()

                signInButton.setOnClickListener {
                    progressBar.isVisible = true
                    stream.postCommand(
                        Authentication.Command.SignIn(
                            data.copy(
                                userName = userName.text?.toString(),
                                password = password.text?.toString()
                            )
                        )
                    )
                }

                signUpButton.setOnClickListener {
                    progressBar.isVisible = true
                    stream.postCommand(
                        Authentication.Command.SignUp(
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


    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}







