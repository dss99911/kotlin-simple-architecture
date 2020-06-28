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
import kotlinx.coroutines.async

class ApiParallelScreen(private val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String = R.string.multiple_parallel_call.resourceToString()

    private val result by lazy { resourceStateOf<Unit>() }

    override fun initialize() {
        result.load(initStatus) {
            val token = api.getToken()

            val result1 = async { api.submitPost(token, Item(1, "name1")) }
            val result2 = async { api.submitPost(token, Item(2, "name2")) }
            val result3 = async { api.submitPost(token, Item(2, "name3")) }
            result1.await()
            result2.await()
            result3.await()
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