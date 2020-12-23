package com.example.sampleandroid.view.widget

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.value
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun SampleTextField(
    label: String,
    text: MutableSharedFlow<String>,
    modifier: Modifier = Modifier
) {
    androidx.compose.material.OutlinedTextField(
        +text ?: "",
        { text.value = it },
        modifier,
        label = { Text(label) }
    )
}