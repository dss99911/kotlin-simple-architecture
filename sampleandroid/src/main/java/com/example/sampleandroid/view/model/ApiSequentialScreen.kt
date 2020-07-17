package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.BaseViewModel
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.api.PreferenceApi
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class ApiSequentialScreen(private val viewModel: ApiSequentialViewModel = ApiSequentialViewModel()) : ModelScreen(viewModel) {
    override val title: String = R.string.multiple_sequential_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            VerticalListView(list = +viewModel.list, modifier = weight(1f)) {
                Text("key : ${it.first}, value : ${it.second}")
            }

            Row { Text(viewModel.KEY1); TextField(viewModel.input1) }
            Row { Text(viewModel.KEY2); TextField(viewModel.input2) }
            Row { Text(viewModel.KEY3); TextField(viewModel.input3) }

            Button(viewModel::onClick) {
                Text("update")
            }
        }
    }
}

class ApiSequentialViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : BaseViewModel() {
    val KEY1 = "key1"
    val KEY2 = "key2"
    val KEY3 = "key3"

    val list = MutableStateFlow(listOf<Pair<String, String?>>())
    val input1 = MutableStateFlow("")
    val input2 = MutableStateFlow("")
    val input3 = MutableStateFlow("")

    override fun onInitialized() {
        list.load(initStatus) {
            listOf(
                Pair(KEY1, api.getString(KEY1)).also { input1.value = it.second ?: "" },
                Pair(KEY2, api.getString(KEY2)).also { input2.value = it.second ?: "" },
                Pair(KEY3, api.getString(KEY3)).also { input3.value = it.second ?: "" }
            )
        }
    }

    fun onClick() {
        list.load(status) {
            api.setString(KEY1, input1.value)
            api.setString(KEY2, input2.value)
            api.setString(KEY3, input3.value)
            listOf(
                Pair(KEY1, input1.value),
                Pair(KEY2, input2.value),
                Pair(KEY3, input3.value)
            )
        }
    }

}