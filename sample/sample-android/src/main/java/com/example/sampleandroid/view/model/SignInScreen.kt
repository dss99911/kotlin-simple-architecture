package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignInViewModel

class SignInScreen(private val model: SignInViewModel = SignInViewModel()) :
    ModelScreen(model) {
    override val title: String = R.string.sign_in.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        ScrollableColumn {
            if (+model.user == null) {
                LogInView()
            } else {
                LogOutView()
            }
        }
    }

    @Composable
    fun LogInView() {
        OutlinedTextField(model.inputId, label = { Text("Id") })
        OutlinedTextField(model.inputPassword, label = { Text("Password") })
        Button(model::onClickSignIn) {
            Text("Sign In")
        }

        Button({ SignUpScreen().push() }) {
            Text("Sign Up")
        }
    }

    @Composable
    fun LogOutView() {
        val userDetail = model.user.asValue()!!
        Text("Id : ${userDetail.id}")
        Text("Name : ${userDetail.name}")
        Button(model::onClickLogOut) {
            Text("Log Out")
        }
    }
}