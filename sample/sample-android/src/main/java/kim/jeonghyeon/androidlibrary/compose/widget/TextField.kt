package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.input.ImeAction
import androidx.ui.input.KeyboardType
import androidx.ui.input.VisualTransformation
import androidx.ui.text.SoftwareKeyboardController
import androidx.ui.text.TextLayoutResult
import androidx.ui.text.TextStyle
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun TextField(
    text: MutableStateFlow<String>,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Unset,
    textStyle: TextStyle = currentTextStyle(),
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Unspecified,
    onFocusChange: (Boolean) -> Unit = {},
    onImeActionPerformed: (ImeAction) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    cursorColor: Color = contentColor()
) {
    androidx.ui.foundation.TextField(
        androidx.ui.input.TextFieldValue(+text),
        { text.value = it.text },
        modifier,
        textColor,
        textStyle,
        keyboardType,
        imeAction,
        onFocusChange,
        onImeActionPerformed,
        visualTransformation,
        onTextLayout,
        onTextInputStarted,
        cursorColor
    )
}