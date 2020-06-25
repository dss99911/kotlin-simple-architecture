package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import com.example.sampleandroid.R
import com.example.sampleandroid.library.resourceStateOf
import com.example.sampleandroid.library.success
import com.example.sampleandroid.library.successValue
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSingleScreen(val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.single_call.resourceToString()

    private val item = resourceStateOf<Item>()
    private val result = resourceStateOf<String>()

    override fun initialize() {

        item.load(initStatus) {
            api.getToken()
        }

    }

    private fun onClick() {
        result.load(status) {
            api.submitPost("dd", item.successValue)
            "success"
        }
    }

    @Composable
    override fun view() {
        item.success {
            Column {
                Button(::onClick) {
                    Text(it.name)
                }
                result.success {
                    Text(it)
                }
            }
        }

    }
}