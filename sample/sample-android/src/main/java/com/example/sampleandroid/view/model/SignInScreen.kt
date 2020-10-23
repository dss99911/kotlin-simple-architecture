package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignInViewModel

@Composable
fun SignInScreen(model: SignInViewModel) {
    ScrollableColumn {
        SampleTextField("Id", model.inputId)
        SampleTextField("Password", model.inputPassword)
        Button("Sign In", onClick = model::onClickSignIn)
        Button("Sign Up",onClick = model::onClickSignUp)
    }
}