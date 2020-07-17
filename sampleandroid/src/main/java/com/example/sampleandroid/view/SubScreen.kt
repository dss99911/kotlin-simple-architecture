package com.example.sampleandroid.view

import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.client.BaseViewModel

abstract class SubScreen(vararg viewModels: BaseViewModel) : Screen(*viewModels) {
    abstract val parentTitle: String
}