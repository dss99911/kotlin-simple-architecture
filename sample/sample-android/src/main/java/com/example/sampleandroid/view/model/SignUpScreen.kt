package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(model: SignUpViewModel) {
    Screen(model) {
        Column {
            SampleTextField("Id", model.inputId)
            SampleTextField("Name", model.inputName)
            SampleTextField("Password", model.inputPassword)
            Button("Sign up") { model.onClickSignUp() }
            Button("Google") { model.onClickGoogle() }
            Button("Facebook") { model.onClickFacebook() }
        }
    }
}