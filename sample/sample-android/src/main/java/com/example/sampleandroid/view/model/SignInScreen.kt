package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.SignInViewModel
import kim.jeonghyeon.sample.viewmodel.SignInViewModel2

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

@Composable
fun SignInScreen2(model: SignInViewModel2) {
    Screen(model) {
        ScrollableColumn {
            SampleTextField("Id", model.inputId)
            SampleTextField("Password", model.inputPassword)
            Button("Sign In", model.clickSignIn)
            Button("Sign Up", model.clickSignUp)
        }
    }
}