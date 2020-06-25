package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.extension.getString

class DbSimpleScreen : ModelScreen() {
    override val title: String = R.string.db_simple.getString()

    @Composable
    override fun view() {
        Text(title)
    }
}