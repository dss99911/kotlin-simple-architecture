package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.currentTextStyle
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.DataFlow
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TextField(
    text: DataFlow<String>,
    label: @Composable () -> Unit = {},
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
    errorColor: Color = MaterialTheme.colors.error,
    backgroundColor: Color = MaterialTheme.colors.onSurface,
    shape: Shape =
        MaterialTheme.shapes.small.copy(bottomLeft = ZeroCornerSize, bottomRight = ZeroCornerSize)
) {
    androidx.compose.material.TextField(
        +text,
        { text.value = it },
        label,
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
        errorColor,
        backgroundColor,
        shape
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun BaseTextField(
    text: MutableStateFlow<String>,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unset,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    cursorColor: Color = contentColor()
) {
    val (textValue, updateText) = remember { mutableStateOf(TextFieldValue(text.value)) }
    androidx.compose.foundation.BaseTextField(
        textValue,
        updateText,
        modifier,
        textColor,
        textStyle,
        keyboardType,
        imeAction,
        onImeActionPerformed,
        visualTransformation,
        onTextLayout,
        onTextInputStarted,
        cursorColor
    )
}

@Composable
fun OutlinedTextField(
    text: DataFlow<String>,
    label: @Composable () -> Unit = {},
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
        label,
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