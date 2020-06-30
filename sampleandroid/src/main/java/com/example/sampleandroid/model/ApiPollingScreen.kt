package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiPollingScreen(private val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.polling.resourceToString()

    private val result by lazy { resourceStateOf<String>() }

    override fun initialize() {
        result.load(initStatus) {
            val token = api.getToken("id", "pw")

            polling(5, 1000, 3000) {
                api.submitPost(token, Post(1, "name$it"))
                it.toString()//show count
            }
        }
    }

    @Composable
    override fun view() {
        Text("fail count " + result.data().toString())
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}