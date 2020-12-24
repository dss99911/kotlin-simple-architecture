package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.DeeplinkSubViewModel

@Composable
fun DeeplinkSubScreen(model: DeeplinkSubViewModel) {
    Screen(model) {
        Column {
            SampleTextField("Input value", model.result)
            Button("OK") { model.onClickOk() }
        }
    }
}

// TODO reactive way.
//@Composable
//fun DeeplinkSubScreen2(model: DeeplinkSubViewModel2) {
//    Screen(model) {
//        Column {
//            SampleTextField("Input value", model.result)
//            Button("OK", model.click)
//        }
//    }
//}