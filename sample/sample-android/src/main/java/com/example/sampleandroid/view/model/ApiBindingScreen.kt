package com.example.sampleandroid.view.model

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.sample.viewmodel.ApiBindingViewModel

@Composable
fun ApiBindingScreen(model: ApiBindingViewModel) {
    Screen(model) {
        ScrollableColumn {
            Text("Result : ${+model.result}")
            Button("Bind 2 Api") { model.onClickBind2Api() }
            Button("Bind 3 Api") { model.onClickBind3Api() }
            Button("Bind Response to Parameter") { model.onClickBindResposneToParameter() }
            Button("Bind Response's Field to Parameter") { model.onClickBindResposneFieldToParameter() }
            Button("Handle Error") { model.onClickHandleError() }
            Button("Bind Api with Auth") { model.onClickBindApiAuthRequired() }
        }
    }
}

// TODO reactive way.
//@Composable
//fun ApiBindingScreen2(model: ApiBindingViewModel2) {
//    Screen(model) {
//        ScrollableColumn {
//            Text("Result : ${+model.result}")
//            Button("Bind 2 Api", model.clickBind2Api)
//            Button("Bind 3 Api", model.clickBind3Api)
//            Button("Bind Response to Parameter", model.clickBindResposneToParameter)
//            Button("Bind Response's Field to Parameter", model.clickBindResposneFieldToParameter)
//            Button("Handle Error", model.clickHandleError)
//            Button("Bind Api with Auth", model.clickBindApiAuthRequired)
//        }
//    }
//}