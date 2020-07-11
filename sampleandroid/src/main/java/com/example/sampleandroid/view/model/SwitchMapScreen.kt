package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.compose.mutableStateOf
import androidx.compose.stateFor
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.isSuccess
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.compose.setSuccess
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.sample.di.serviceLocator

class SwitchMapScreen(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelScreen() {
    override val title: String = R.string.single_call.resourceToString()

    private val value = resourceStateOf<String?>()
    private val input = mutableStateOf("")

    override fun initialize() {
        value.load(initStatus) {
            api.getString(title)
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

    private fun onClick() {
        status.load {
            val text = input.value
            api.setString(title, text)
            value.setSuccess(text)
        }
    }

    /**
     * this connect each state. like switchMap, map, MediatorLiveData
     * but be careful that. if this screen is not shown. and show again. this will be invoked again.
     * so, use this only for setting ui data. don't call api
     */
    @Composable
    fun initState() {
        stateFor(v1 = value) {
            if (value.isSuccess) {
                input.value = value.data() ?: ""
            }

        }
    }

    @Composable
    override fun view() {
        initState()

        Column {
            Text("key : $title\nvalue : ${value.data()}")
            TextField(input)
            Button(::onClick) {
                Text("update")
            }
        }
    }
}