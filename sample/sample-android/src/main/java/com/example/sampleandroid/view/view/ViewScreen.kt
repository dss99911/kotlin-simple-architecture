package com.example.sampleandroid.view.view

import com.example.sampleandroid.view.SubScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.SampleViewModel

/**
 * TODD add Design related example
 *  https://hyun.myjetbrains.com/youtrack/issue/KSA-139
 */
abstract class ViewScreen(viewModel: SampleViewModel = SampleViewModel()) : SubScreen(viewModel) {
    override val parentTitle: String = R.string.view.resourceToString()

    companion object {
        val screens = listOf<Pair<String, () -> ViewScreen>>(
//            DeeplinkScreen().title to { DeeplinkScreen() }
        )
    }
}