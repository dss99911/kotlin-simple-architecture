package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import com.example.sampleandroid.R
import com.example.sampleandroid.library.resourceStateOf
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class ApiSequentialScreen : ModelScreen() {
    override val title: String = R.string.multiple_sequential_call.resourceToString()

    private val submitResult = resourceStateOf<String>()

    @Composable
    override fun view() {
//        submitResult.loadInComposition(work = )
        Text(title)
    }
}