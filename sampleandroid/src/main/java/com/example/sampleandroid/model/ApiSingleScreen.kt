package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.isSuccess
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSingleScreen(private val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.single_call.resourceToString()

    private val token by lazy { resourceStateOf<String>() }
    private val result by lazy { resourceStateOf<String>() }

    override fun initialize() {
        token.load(initStatus) {
            api.getToken()
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

    private fun onClick() {
        result.load(status) {
            api.submitPost(token.data(), Item(1, "name"))
            "success"
        }
    }

    @Composable
    override fun view() {
        Column {
            Button(::onClick) {
                Text(token.data())
            }

            if (result.isSuccess) {
                Text(result.data())
            }
        }
    }
}