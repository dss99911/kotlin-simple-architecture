package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
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
            OutlinedTextField(model.input, { Text("Input value") })
            Button(model::onClick) {
                Text("update")
            }
            Text("current value : ${+model.result}")
        }
    }
}