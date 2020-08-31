package com.example.sampleandroid.view.view

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import com.example.sampleandroid.activity.OtherDeeplinkActivity
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.compose.widget.SpacerH
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.androidlibrary.extension.startActivity
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.compose.R
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class DeeplinkScreen(private val viewModel: DeeplinkViewModel = DeeplinkViewModel()) : ViewScreen(viewModel) {
    override val title: String = R.string.deeplink.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {

        Column {
            Text("Deeplink data : ${+viewModel.deeplinkData}")
            Text("Deeplink Url : ${+viewModel.deeplinkUrl}")

            OutlinedTextField(viewModel.input, { Text("Input parameter value")})

            SpacerH(30.dp)

            Button(viewModel::onClickDeeplinkGenerate) {
                Text("create Deeplink Url")
            }

            Button({ startActivity<OtherDeeplinkActivity>() }) {
                Text("go to deeplink page")
            }
        }
    }
}

class DeeplinkViewModel : BaseViewModel() {
    val deeplinkData = MutableStateFlow("")
    val deeplinkUrl = MutableStateFlow("")
    val input = MutableStateFlow("")

    fun onClickDeeplinkGenerate() {
        val data = URLEncoder.encode(input.value, StandardCharsets.UTF_8.name())
        deeplinkUrl.value =
            "https://kotlinsimplearchitecture.page.link/?link=https%3A%2F%2Fkotlinsimplearchitecture.jeonghyeon.kim%2Fother%3Fparam%3D${data}&apn=kim.jeonghyeon.sample.compose"
    }
}