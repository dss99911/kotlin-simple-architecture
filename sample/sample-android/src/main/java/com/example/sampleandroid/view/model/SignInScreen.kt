package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.SignInViewModel

@Composable
fun SignInScreen(model: SignInViewModel) {
    Screen(model) {
        ScrollableColumn {
            SampleTextField("Id", model.inputId)
            SampleTextField("Password", model.inputPassword)
            Button("Sign In") { model.onClickSignIn() }
            Button("Sign Up") { model.onClickSignUp() }
        }
    }
}