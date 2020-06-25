package com.example.sampleandroid.model

import com.example.sampleandroid.R
import com.example.sampleandroid.library.Screen
import kim.jeonghyeon.androidlibrary.extension.getString

abstract class ModelScreen : Screen() {
    val parentTitle: String = R.string.model.getString()

    companion object {
        val screens = listOf(
            ApiSingleScreen(),
            ApiSequentialScreen(),
            ApiParallelScreen(),
            ApiPollingScreen(),
            DbSimpleScreen(),
            DbObserveScreen(),
            ApiDbScreen(),
            PreferenceScreen(),
            SwitchMapScreen()
        )
    }
}