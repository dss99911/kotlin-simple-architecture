package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiBindingViewModel
import kim.jeonghyeon.sample.viewmodel.ApiDbViewModel


class ApiBindingScreen(private val model: ApiBindingViewModel = ApiBindingViewModel()) : ModelScreen(model) {

    override val title: String = R.string.api_binding.resourceToString()

    @Composable
    override fun view() {
        Column {
            Text("Success")
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

}