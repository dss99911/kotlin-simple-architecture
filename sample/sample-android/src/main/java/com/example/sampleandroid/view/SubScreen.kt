package com.example.sampleandroid.view

import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

abstract class SubScreen(viewModel: SampleViewModel = SampleViewModel()) : SampleScreen(viewModel) {
    abstract val parentTitle: String
}