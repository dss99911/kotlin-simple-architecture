package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiBindingViewModel

@Composable
fun ApiBindingScreen(model: ApiBindingViewModel) {
    Screen(model) {
        ScrollableColumn {
            Text("Result : ${+model.result}")
            Button("Bind 2 Api") { model.onClickBind2Api() }
            Button("Bind 3 Api") { model.onClickBind3Api() }
            Button("Bind Response to Parameter") { model.onClickBindResposneToParameter() }
            Button("Bind Response's Field to Parameter") { model.onClickBindResposneFieldToParameter() }
            Button("Handle Error") { model.onClickHandleError() }
            Button("Bind Api with Auth") { model.onClickBindApiAuthRequired() }
        }
    }
}