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
    modifier: Modifier = Modifier,
    textStyle: TextStyle = currentTextStyle(),
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isErrorValue: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction, SoftwareKeyboardController?) -> Unit = { _, _ -> },
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    activeColor: Color = MaterialTheme.colors.primary,
    inactiveColor: Color = MaterialTheme.colors.onSurface,
    errorColor: Color = MaterialTheme.colors.error
) {
    androidx.compose.material.OutlinedTextField(
        +text,
        { text.value = it },
        { Text(label) },
        modifier,
        textStyle,
        placeholder,
        leadingIcon,
        trailingIcon,
        isErrorValue,
        visualTransformation,
        keyboardType,
        imeAction,
        onImeActionPerformed,
        onTextInputStarted,
        activeColor,
        inactiveColor,
        errorColor
    )
}