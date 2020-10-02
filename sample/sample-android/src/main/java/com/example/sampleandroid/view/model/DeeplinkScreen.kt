package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.DeeplinkViewModel

class DeeplinkScreen(private val model: DeeplinkViewModel = DeeplinkViewModel()) :
    ModelScreen(model) {
    override val title: String = R.string.deeplink.resourceToString()

    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        ScrollableColumn {
            Button("Deeplink on client") {
                model.onClickClientDeeplink()
            }

            Button("Deeplink from server") {
                model.onClickServerDeeplink()
            }

            Button("Deeplink to home") {
                model.onClickGoToHome()
            }

            Button("Deeplink to signIn then home") {
                model.onClickGoToSignInThenGoHome()
            }

            Button("link to google") {
                model.onClickGoogleUrl()
            }

            SampleTextField("Input parameter", model.deeplinkSubRequest)
            Text("result value : ${+model.deeplinkSubResult}")

            Button("navigate to screen by deeplink only") {
                model.onClickNavigateByDeeplinkOnly()
            }
        }

    }
}