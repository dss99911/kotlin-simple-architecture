package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiPollingViewModel

class ApiPollingScreen(private val model: ApiPollingViewModel = ApiPollingViewModel()) : ModelScreen(model) {
    override val title: String = R.string.polling.resourceToString()

    @Composable
    override fun view() {
        Column {
            Text("fail count ${+model.count}")
            if (model.status.asValue().isSuccess()) {
                Text("result ${+model.result}")
            }
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}