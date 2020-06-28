package com.example.sampleandroid.model

import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.extension.resourceToString

abstract class ModelScreen : Screen() {
    val parentTitle: String = R.string.model.resourceToString()

    companion object {
        val screens = listOf(
            ApiSingleScreen().title to { ApiSingleScreen() },
            ApiSequentialScreen().title to { ApiSequentialScreen() },
            ApiParallelScreen().title to { ApiParallelScreen() },
            ApiPollingScreen().title to { ApiPollingScreen()},
            DbSimpleScreen().title to { DbSimpleScreen() },
            ApiDbScreen().title to { ApiDbScreen() },
            PreferenceScreen().title to { PreferenceScreen() },
            SwitchMapScreen().title to { SwitchMapScreen() }
        )
    }
}