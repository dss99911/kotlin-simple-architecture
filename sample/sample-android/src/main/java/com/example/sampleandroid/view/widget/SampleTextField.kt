package com.example.sampleandroid.view.widget

import androidx.compose.foundation.Text
import androidx.compose.foundation.currentTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
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