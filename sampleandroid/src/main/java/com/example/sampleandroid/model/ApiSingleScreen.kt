package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.compose.mutableStateOf
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.compose.setSuccess
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSingleScreen(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelScreen() {
    override val title: String = R.string.single_call.resourceToString()

    private val value = resourceStateOf<String?>()
    private val input = mutableStateOf("")

    override fun initialize() {
        value.load(initStatus) {
            api.getString(title).also {
                //todo this should be processed same way with switchMap.
                // stateFor can not be used for this case.
                // am I sure that this architecture is proper for compose?
                // my architecture is just same way different one is writing ui in kotlin.
                // what is the benefit to use compose?
                input.value = it ?: ""
            }
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

    private fun onClick() {
        status.load {
            api.setString(title, input.value)
            value.setSuccess(input.value)
        }
    }

    @Composable
    override fun view() {

        Column {
            Text("key : $title\nvalue : ${value.data()}")
            TextField(input)
            Button(::onClick) {
                Text("update")
            }
        }
    }
}