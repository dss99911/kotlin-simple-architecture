package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class PreferenceScreen : ModelScreen() {
    override val title: String = R.string.preference.resourceToString()

    @Composable
    override fun view() {
        Text(title)
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}