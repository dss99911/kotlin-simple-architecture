package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel

class ApiSingleScreen(private val viewModel: ApiSingleViewModel = ApiSingleViewModel()) : ModelScreen(viewModel) {
    override val title: String = R.string.single_call.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            Text("key : $title\nvalue : ${+viewModel.result}")
            TextField(viewModel.input)
            Button(viewModel::onClick) {
                Text("update")
            }
        }
    }
}

//class ApiSingleViewModel(private val api: PreferenceApi = serviceLocator.preferenceApi) : BaseViewModel() {
//    val KEY = "someKey"
//
//    val result = MutableStateFlow("")
//    val input = MutableStateFlow("")
//        .withSource(result) { value = it }
//
//
//    override fun onInitialized() {
//        result.load(initStatus) {
//            api.getString(KEY) ?: ""
//        }
//    }
//
//    fun onClick() {
//        result.load(status) {
//            val text = input.value
//            api.setString(KEY, text)
//            text
//        }
//    }
//}