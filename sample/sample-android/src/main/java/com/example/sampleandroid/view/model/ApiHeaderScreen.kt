package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiHeaderViewModel

class ApiHeaderScreen(private val model: ApiHeaderViewModel = ApiHeaderViewModel()) : ModelScreen(model) {
    override val title: String = R.string.header_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            Text("current header : ${+model.result}")
            SampleTextField("Input custom header", model.input)
            Button("change header") {
                model.onClick()
            }
        }
    }
}