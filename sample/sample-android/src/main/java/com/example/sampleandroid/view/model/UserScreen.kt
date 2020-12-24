package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.asValue
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.UserViewModel
import kim.jeonghyeon.util.log

@Composable
fun UserScreen(model: UserViewModel) {
    Screen(model) {
        ScrollableColumn {
            val userDetail = model.user.asValue() ?: return@ScrollableColumn
            Text("Id : ${userDetail.id}")
            Text("Name : ${userDetail.name}")
            Button("Log Out") { model.onClickLogOut() }
        }
    }
}

// TODO reactive way.
//@Composable
//fun UserScreen2(model: UserViewModel2) {
//    Screen(model) {
//        ScrollableColumn {
//            val userDetail = model.user.asValue() ?: return@ScrollableColumn
//            Text("Id : ${userDetail.id}")
//            Text("Name : ${userDetail.name}")
//            Button("Log Out", model.click)
//        }
//    }
//}