package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.client.call

/**
 * change parameter order to match with swiftui
 */
@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Dp = 2.dp,
    disabledElevation: Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colors.primary,
    disabledBackgroundColor: Color = ButtonConstants.defaultDisabledBackgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    disabledContentColor: Color = ButtonConstants.defaultDisabledContentColor,
    contentPadding: InnerPadding = ButtonConstants.DefaultContentPadding,
    onClick: () -> Unit,
) {
    androidx.compose.material.Button(onClick, modifier, enabled, elevation, disabledElevation, shape, border, backgroundColor, disabledBackgroundColor, contentColor, disabledContentColor, contentPadding) {
        Text(text)
    }
}

@Composable
fun Button(
    text: String,
    clickFlow: DataFlow<Unit>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: Dp = 2.dp,
    disabledElevation: Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colors.primary,
    disabledBackgroundColor: Color = ButtonConstants.defaultDisabledBackgroundColor,
    contentColor: Color = contentColorFor(backgroundColor),
    disabledContentColor: Color = ButtonConstants.defaultDisabledContentColor,
    contentPadding: InnerPadding = ButtonConstants.DefaultContentPadding,
) {
    androidx.compose.material.Button({ clickFlow.call() }, modifier, enabled, elevation, disabledElevation, shape, border, backgroundColor, disabledBackgroundColor, contentColor, disabledContentColor, contentPadding) {
        Text(text)
    }
}