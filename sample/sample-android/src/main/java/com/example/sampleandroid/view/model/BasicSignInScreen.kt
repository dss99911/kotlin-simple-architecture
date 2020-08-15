package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.BasicSignInViewModel

class BasicSignInScreen(private val model: BasicSignInViewModel = BasicSignInViewModel()) :
    ModelScreen(model) {
    override val title: String = R.string.basic_sign_in.resourceToString()

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

        Button({ BasicSignUpScreen({ model.onSignedUp() }).push() }) {
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