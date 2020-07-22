package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
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
            VerticalListView(list = +model.list, modifier = weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }

            Row { Text(model.KEY1); TextField(model.input1) }
            Row { Text(model.KEY2); TextField(model.input2) }
            Row { Text(model.KEY3); TextField(model.input3) }

            Button(model::onClick) {
                Text("update")
            }
        }
    }
}