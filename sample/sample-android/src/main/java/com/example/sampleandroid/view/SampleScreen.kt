package com.example.sampleandroid.view

import com.example.sampleandroid.view.model.SignInScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.collectNotNull
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

abstract class SampleScreen(viewModel: SampleViewModel = SampleViewModel()) : Screen(viewModel) {
    init {
        if (viewModel != null) {
            launch {
                viewModel.goSignIn.collectNotNull { listener ->
                    //when two screen is in stack.
                    //and they observe user detail.
                    //and when it's sign out. call the user api again.
                    //and all observer will receive error.
                    //so, only showing screen redirect to sign-in screen
                    if (isShown()) {
                        toast("Please sign in for using the feature")
                        push(SignInScreen()) {
                            listener.onSignInResult(it)
                        }
                    }
                }
            }
        }

    }
}