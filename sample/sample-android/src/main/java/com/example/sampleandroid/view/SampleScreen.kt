package com.example.sampleandroid.view

import com.example.sampleandroid.view.model.SignInScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.extension.toast
import kim.jeonghyeon.client.collectNotNull
import kim.jeonghyeon.net.DeeplinkInfo
import kim.jeonghyeon.net.error.errorDeeplink
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

abstract class SampleScreen(viewModel: SampleViewModel = SampleViewModel()) : Screen(viewModel)