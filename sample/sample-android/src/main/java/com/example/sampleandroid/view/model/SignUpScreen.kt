package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignUpViewModel

class SignUpScreen(private val model: SignUpViewModel = SignUpViewModel()) : ModelScreen(model) {
    override val title: String = R.string.sign_up.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            SampleTextField("Id", model.inputId)
            SampleTextField("Name", model.inputName)
            SampleTextField("Password", model.inputPassword)
            Button("Sign up") {
                model.onClickSignUp()
            }

            Button("Google") {
                model.onClickGoogle()
            }
            Button("Facebook") {
                model.onClickFacebook()
            }
        }
    }
}