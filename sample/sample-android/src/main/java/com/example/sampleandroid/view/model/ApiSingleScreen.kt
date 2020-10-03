package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel

class ApiSingleScreen(private val model: ApiSingleViewModel = ApiSingleViewModel()) : ModelScreen(model) {
    override val title: String = R.string.single_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            Text("current value : ${+model.result}")
            SampleTextField("Input value", model.input)
            Button("update") {
                model.onClick()
            }
        }
    }
}