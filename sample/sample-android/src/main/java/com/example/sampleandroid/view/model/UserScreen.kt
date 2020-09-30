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
import kim.jeonghyeon.sample.viewmodel.UserViewModel

class UserScreen(private val model: UserViewModel = UserViewModel()) :
    ModelScreen(model) {

    override val title: String = R.string.user.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        ScrollableColumn {
            val userDetail = model.user.asValue()?:return@ScrollableColumn
            Text("Id : ${userDetail.id}")
            Text("Name : ${userDetail.name}")
            Button(model::onClickLogOut) {
                Text("Log Out")
            }
        }
    }
}