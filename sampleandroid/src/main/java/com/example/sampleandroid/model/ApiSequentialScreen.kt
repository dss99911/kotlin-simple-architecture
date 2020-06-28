package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSequentialScreen(private val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.multiple_sequential_call.resourceToString()


    private val result by lazy { resourceStateOf<String>() }

    override fun initialize() {
        result.load(initStatus) {
            api.submitPost(api.getToken(), Item(1,"name"))
            "success"
        }

    }

    @Composable
    override fun view() {
        Text(result.data())
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}