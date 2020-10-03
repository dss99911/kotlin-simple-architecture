package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
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
            Row {
                SampleTextField("Input Git hub search keyword", model.input)
                Button(R.string.call.resourceToString()) { model.onClickCall() }
            }

            ScrollableColumn(+model.repoList, weight(1f)) {
                Text(it.toString())
            }

        }
    }
}