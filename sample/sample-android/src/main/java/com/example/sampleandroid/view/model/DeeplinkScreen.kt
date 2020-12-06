package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.DeeplinkViewModel

@Composable
fun DeeplinkScreen(model: DeeplinkViewModel) {
    Screen(model) {
        ScrollableColumn {
            Button("Deeplink on client") { model.onClickClientDeeplink() }
            Button("Deeplink from server") { model.onClickServerDeeplink() }
            Button("Deeplink to home") { model.onClickGoToHome() }
            Button("Deeplink to signIn then home") { model.onClickGoToSignInThenGoHome() }
            Button("link to google") { model.onClickGoogleUrl() }
            SampleTextField("Input parameter", model.deeplinkSubRequest)
            Text("result value : ${+model.deeplinkSubResult}")
            Button("navigate to screen by deeplink only") { model.onClickNavigateByDeeplinkOnly() }
        }
    }
}