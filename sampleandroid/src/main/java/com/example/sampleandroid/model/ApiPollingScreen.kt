package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiPollingScreen(private val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.polling.resourceToString()

    private val result by lazy { resourceStateOf<Unit>() }

    override fun initialize() {
        result.load(initStatus) {
            val token = api.getToken()

            polling(5, 1000, 3000) {
                api.submitPost(token, Item(1, "name$it"))
            }
        }
    }

    @Composable
    override fun view() {
        Text(result.data().toString())
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}