package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
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
            OutlinedTextField(model.inputId,{ Text("Id") })
            OutlinedTextField(model.inputName,{ Text("Name") })
            OutlinedTextField(model.inputPassword, { Text("Password") })
            Button(model::onClickSignUp) {
                Text("Sign up")
            }

            Button(model::onClickGoogle) {
                Text("Google")
            }
            Button(model::onClickFacebook) {
                Text("Facebook")
            }
        }
    }
}