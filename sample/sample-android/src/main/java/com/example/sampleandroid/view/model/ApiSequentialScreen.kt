package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiSequentialViewModel

class ApiSequentialScreen(private val model: ApiSequentialViewModel = ApiSequentialViewModel()) :
    ModelScreen(model) {
    override val title: String = R.string.multiple_sequential_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            OutlinedTextField(model.input1, label = { Text("Input value1")})
            OutlinedTextField(model.input2, label = { Text("Input value2")})
            OutlinedTextField(model.input3, label = { Text("Input value3")})

            Button(model::onClick) {
                Text(R.string.update.resourceToString())
            }

            ScrollableColumn(list = +model.textList, modifier = weight(1f)) {
                Text(it)
            }
        }
    }
}

