package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignUpViewModel

@Composable
fun SignUpScreen(model: SignUpViewModel) {
    Column {
        SampleTextField("Id", model.inputId)
        SampleTextField("Name", model.inputName)
        SampleTextField("Password", model.inputPassword)
        Button("Sign up", onClick = model::onClickSignUp)
        Button("Google", onClick = model::onClickGoogle)
        Button("Facebook", onClick = model::onClickFacebook)
    }
}