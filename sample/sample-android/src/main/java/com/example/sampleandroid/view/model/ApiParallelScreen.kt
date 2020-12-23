package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiParallelViewModel
import kim.jeonghyeon.sample.viewmodel.ApiParallelViewModel2

@Composable
fun ApiParallelScreen(model: ApiParallelViewModel) {
    Screen(model) {
        Column {
            SampleTextField("Input value1", model.input1)
            SampleTextField("Input value2", model.input2)
            SampleTextField("Input value3", model.input3)

            Button(R.string.update.resourceToString()) { model.onClick() }

            ScrollableColumn(list = +model.list, modifier = Modifier.weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }
        }
    }
}

@Composable
fun ApiParallelScreen2(model: ApiParallelViewModel2) {
    Screen(model) {
        Column {
            SampleTextField("Input value1", model.input1)
            SampleTextField("Input value2", model.input2)
            SampleTextField("Input value3", model.input3)

            Button(R.string.update.resourceToString(), model.click)

            ScrollableColumn(list = +model.list, modifier = Modifier.weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }
        }
    }
}