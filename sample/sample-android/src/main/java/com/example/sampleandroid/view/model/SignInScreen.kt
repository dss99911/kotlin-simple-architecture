package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.compose.launchInComposition
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.collectEvent
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SignInViewModel
import kotlinx.coroutines.launch
import java.net.ConnectException

class SignInScreen(private val model: SignInViewModel = SignInViewModel()) : ModelScreen(model) {
    override val title: String = R.string.single_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            if (+model.user == null) {
                LogInView()
            } else {
                LogOutView()
            }
        }
    }

    @Composable
    fun LogInView() {
        Text("Id")
        TextField(model.inputId)
        Text("Password")
        TextField(model.inputPassword)
        Button(model::onClickSignIn) {
            Text("Sign In")
        }

        Button({ SignUpScreen({ model.onSignedUp() }).push() }) {
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