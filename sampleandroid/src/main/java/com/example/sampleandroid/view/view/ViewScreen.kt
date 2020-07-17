package com.example.sampleandroid.view.view

import com.example.sampleandroid.view.SubScreen
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.compose.R

abstract class ViewScreen(vararg viewModels: BaseViewModel) : SubScreen(*viewModels) {
    override val parentTitle: String = R.string.view.resourceToString()

    companion object {
        val screens = listOf(
            DeeplinkScreen().title to { DeeplinkScreen() }
        )
    }
}