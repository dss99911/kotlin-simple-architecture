package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.coroutine.polling
import kim.jeonghyeon.sample.api.Post
import kim.jeonghyeon.sample.api.SimpleApi
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiPollingScreen(private val viewModel: ApiPollingViewModel = ApiPollingViewModel()) : ModelScreen(viewModel) {
    override val title: String = R.string.polling.resourceToString()

    @Composable
    override fun view() {
        Column {
            Text("fail count ${+viewModel.count}")
            if (viewModel.status.asValue().isSuccess()) {
                Text("result ${+viewModel.result}")
            }

        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}

class ApiPollingViewModel(private val api: SimpleApi = serviceLocator.simpleApi) : BaseViewModel() {
    val result = MutableStateFlow("")
    val count = MutableStateFlow(0)

    override fun onInitialized() {
        result.load(status) {
            val token = api.getToken("id", "pw")

            polling(5, 1000, 3000) {
                count.value = it
                api.submitPost(token, Post(1, "name$it"))
                it.toString()//show count
            }
        }
    }
}