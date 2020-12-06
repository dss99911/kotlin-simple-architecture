package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiHeaderViewModel

@Composable
fun ApiHeaderScreen(model: ApiHeaderViewModel) {
    Screen(model) {
        Column {
            Text("current header : ${+model.result}")
            SampleTextField("Input custom header", model.input)
            Button("change header") { model.onClick() }
        }
    }
}