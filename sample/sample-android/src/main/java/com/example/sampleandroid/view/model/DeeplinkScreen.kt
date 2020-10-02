package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiSingleViewModel
import kim.jeonghyeon.sample.viewmodel.DeeplinkViewModel

class DeeplinkScreen(private val model: DeeplinkViewModel = DeeplinkViewModel()) : ModelScreen(model) {
    override val title: String = R.string.deeplink.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            Button(model::onClickClientDeeplink) {
                Text("Deeplink on client")
            }

            Button(model::onClickServerDeeplink) {
                Text("Deeplink from server")
            }

            Button(model::onClickGoToHome) {
                Text("Deeplink to home")
            }

            Button(model::onClickGoToSignInThenGoHome) {
                Text("Deeplink to signIn then home")
            }

            Button(model::onClickGoogleUrl) {
                Text("link to google")
            }

            OutlinedTextField(model.deeplinkSubRequest, { Text("Input parameter") })
            Text("result value : ${+model.deeplinkSubResult}")

            Button(model::onClickNavigateByDeeplinkOnly) {
                Text("navigate to screen by deeplink only")
            }
        }
    }
}