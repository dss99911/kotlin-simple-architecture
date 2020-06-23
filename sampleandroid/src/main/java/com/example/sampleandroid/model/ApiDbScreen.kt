package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.extension.getString

class ApiDbScreen : ModelScreen() {
    override val title: String = R.string.db_api.getString()

    override val view: @Composable() () -> Unit
        get() = {
            Text(title)
        }
}