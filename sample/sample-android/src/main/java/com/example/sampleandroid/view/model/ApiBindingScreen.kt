package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiBindingViewModel


class ApiBindingScreen(private val model: ApiBindingViewModel = ApiBindingViewModel()) :
    ModelScreen(model) {

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