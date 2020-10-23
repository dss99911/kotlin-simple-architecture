package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.asValue
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.UserViewModel

@Composable
fun UserScreen(model: UserViewModel) {
    ScrollableColumn {
        val userDetail = model.user.asValue() ?: return@ScrollableColumn
        Text("Id : ${userDetail.id}")
        Text("Name : ${userDetail.name}")
        Button("Log Out", onClick = model::onClickLogOut)
    }
}