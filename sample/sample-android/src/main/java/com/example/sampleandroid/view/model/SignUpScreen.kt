package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignUpViewModel

class SignUpScreen(val onSignedUp: () -> Unit, private val model: SignUpViewModel = SignUpViewModel(onSignedUp)) : ModelScreen(model) {
    override val title: String = R.string.sign_up.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            Text("Id")
            TextField(model.inputId)
            Text("Name")
            TextField(model.inputName)
            Text("Password")
            TextField(model.inputPassword)
            Button(model::onClickSignUp) {
                Text("Sign up")
            }
        }
    }
}