package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.extension.getString

class SwitchMapScreen : ModelScreen() {
    override val title: String = R.string.switch_map_call.getString()

    @Composable
    override fun view() {
        Text(title)
    }
}