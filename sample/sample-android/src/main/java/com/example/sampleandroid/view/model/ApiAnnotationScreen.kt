package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiAnnotationViewModel

class ApiAnnotationScreen(private val model: ApiAnnotationViewModel = ApiAnnotationViewModel()) : ModelScreen(model) {
    override val title: String = R.string.annotation_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            SampleTextField("Input value", model.input)
            Button("update") {
                model.onClick()
            }
            Text("current value : ${+model.result}")
        }
    }
}