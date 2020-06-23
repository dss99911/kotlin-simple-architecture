package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import com.example.sampleandroid.library.composeSuccess
import com.example.sampleandroid.library.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.getString
import kim.jeonghyeon.di.serviceLocator
import kim.jeonghyeon.sample.api.Item
import kim.jeonghyeon.sample.api.SimpleApi

class ApiSingleScreen(val api: SimpleApi = serviceLocator.simpleApi) : ModelScreen() {
    override val title: String
        get() = R.string.single_call.getString()

    override val view: @Composable() () -> Unit
        get() = {
            resourceStateOf<Item>().load(initStatus) {
                api.getToken()
            }.composeSuccess {
                Text(it.name)
            }
            //todo parallels api call. but show ui together.
            //todo sequence api call. but show ui together.
            //todo write log for error.
        }
}