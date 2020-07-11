package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.compose.mutableStateOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.compose.setSuccess
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.sample.di.serviceLocator

class ApiSequentialScreen(private val api: PreferenceApi = serviceLocator.preferenceApi) : ModelScreen() {
    override val title: String = R.string.multiple_sequential_call.resourceToString()

    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"


    private val list by lazy { resourceStateOf<List<Pair<String, String?>>>() }
    private val input1 by lazy { mutableStateOf("") }
    private val input2 by lazy { mutableStateOf("") }
    private val input3 by lazy { mutableStateOf("") }

    override fun initialize() {
        list.load(initStatus) {
            listOf(
                Pair(KEY1, api.getString(KEY1)).also { input1.value = it.second ?: "" },
                Pair(KEY2, api.getString(KEY2)).also { input2.value = it.second ?: "" },
                Pair(KEY3, api.getString(KEY3)).also { input3.value = it.second ?: "" }
            )
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

    private fun onClick() {
        status.load {
            api.setString(KEY1, input1.value)
            api.setString(KEY2, input2.value)
            api.setString(KEY3, input3.value)
            list.setSuccess(
                listOf(
                    Pair(KEY1, input1.value),
                    Pair(KEY2, input2.value),
                    Pair(KEY3, input3.value)
                )
            )
        }
    }

    @Composable
    override fun view() {
        Column {
            VerticalListView(list = list.data(), modifier = Modifier.weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }

            Row { Text(KEY1); TextField(input1) }
            Row { Text(KEY2); TextField(input2) }
            Row { Text(KEY3); TextField(input3) }

            Button(::onClick) {
                Text("update")
            }
        }
    }
}