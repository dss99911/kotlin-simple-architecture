package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiBindingViewModel

@Composable
fun ApiBindingScreen(model: ApiBindingViewModel) {
    ScrollableColumn {
        Text("Result : ${+model.result}")
        Button("Bind 2 Api", onClick = model::onClickBind2Api)
        Button("Bind 3 Api", onClick = model::onClickBind3Api)
        Button("Bind Response to Parameter", onClick = model::onClickBindResposneToParameter)
        Button("Bind Response's Field to Parameter", onClick = model::onClickBindResposneFieldToParameter)
        Button("Handle Error", onClick = model::onClickHandleError)
        Button("Bind Api with Auth", onClick = model::onClickBindApiAuthRequired)
    }
}