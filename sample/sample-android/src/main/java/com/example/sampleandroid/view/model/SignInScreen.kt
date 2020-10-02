package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignInViewModel

class SignInScreen(private val model: SignInViewModel = SignInViewModel()) : ModelScreen(model) {
    override val title: String = R.string.sign_in.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        ScrollableColumn {
            SampleTextField("Id", model.inputId)
            SampleTextField("Password", model.inputPassword)
            Button("Sign In") {
                model.onClickSignIn()
            }

            Button("Sign Up") {
                push(SignUpScreen()) { result ->
                    model.onSignUpResult(result)
                }
            }
        }
    }
}