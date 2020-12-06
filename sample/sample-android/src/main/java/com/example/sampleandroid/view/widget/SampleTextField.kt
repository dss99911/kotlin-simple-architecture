package com.example.sampleandroid.view.widget

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.DataFlow

@Composable
fun SampleTextField(
    label: String,
    text: DataFlow<String>,
    modifier: Modifier = Modifier
) {
    androidx.compose.material.OutlinedTextField(
        +text ?: "",
        { text.setValue(it) },
        modifier,
        label = { Text(label) }
    )
}