package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.BasicSignUpViewModel

class BasicSignUpScreen(val onSignedUp: () -> Unit, private val model: BasicSignUpViewModel = BasicSignUpViewModel(onSignedUp)) : ModelScreen(model) {
    override val title: String = R.string.basic_sign_up.resourceToString()

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