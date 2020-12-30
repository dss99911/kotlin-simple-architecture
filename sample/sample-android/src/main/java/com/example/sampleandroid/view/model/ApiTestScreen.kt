package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.sample.viewmodel.ApiTestViewModel

@Composable
fun ApiTestScreen(model: ApiTestViewModel) {
    Screen(model) {
        Column {
            Text("Success")
        }
    }
}