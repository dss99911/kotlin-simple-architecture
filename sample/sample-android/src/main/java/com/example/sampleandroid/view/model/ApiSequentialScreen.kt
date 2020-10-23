package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.weight
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiSequentialViewModel

@Composable
fun ApiSequentialScreen(model: ApiSequentialViewModel) {
    Column {
        SampleTextField("Input value1", model.input1)
        SampleTextField("Input value2", model.input2)
        SampleTextField("Input value3", model.input3)

        Button(R.string.update.resourceToString(), onClick = model::onClick)

        ScrollableColumn(list = +model.textList, modifier = weight(1f)) {
            Text(it)
        }
    }
}