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
import kim.jeonghyeon.sample.viewmodel.ApiExternalViewModel

class ApiExternalScreen(private val model: ApiExternalViewModel = ApiExternalViewModel()) : ModelScreen(model) {
    override val title: String = R.string.external_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            VerticalListView(+model.repoList, weight(1f)) {
                Text(it.toString())
            }
            Row {
                TextField(model.input)
                Button(onClick = model::onClickCall) {
                    Text(R.string.call.resourceToString())
                }
            }
        }
    }
}