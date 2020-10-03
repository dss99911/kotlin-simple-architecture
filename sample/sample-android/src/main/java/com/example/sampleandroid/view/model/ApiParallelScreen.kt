package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiParallelViewModel

class ApiParallelScreen(private val model: ApiParallelViewModel = ApiParallelViewModel()) : ModelScreen() {
    override val title: String = R.string.multiple_parallel_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            SampleTextField("Input value1", model.input1)
            SampleTextField("Input value2", model.input2)
            SampleTextField("Input value3", model.input3)

            Button(R.string.update.resourceToString()) {
                model.onClick()
            }

            ScrollableColumn(list = +model.list, modifier = weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }
        }
    }
}