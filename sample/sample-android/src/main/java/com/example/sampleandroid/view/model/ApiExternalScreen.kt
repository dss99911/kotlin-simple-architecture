package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.weight
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiExternalViewModel

@Composable
fun ApiExternalScreen(model: ApiExternalViewModel) {
    Column {
        Row {
            SampleTextField("Input Git hub search keyword", model.input)
            Button(R.string.call.resourceToString(), onClick = model::onClickCall)
        }

        ScrollableColumn(+model.repoList, weight(1f)) {
            Text(it.toString())
        }

    }
}