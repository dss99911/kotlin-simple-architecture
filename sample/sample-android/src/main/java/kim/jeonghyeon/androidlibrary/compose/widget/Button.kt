package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import kim.jeonghyeon.client.call
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * change parameter order to match with swiftui
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Button(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionState: InteractionState = remember { InteractionState() },
    elevation: ButtonElevation? = ButtonConstants.defaultElevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonConstants.defaultButtonColors(),
    contentPadding: PaddingValues = ButtonConstants.DefaultContentPadding,
    onClick: () -> Unit,
) {
    androidx.compose.material.Button(
        onClick,
        modifier,
        enabled,
        interactionState,
        elevation,
        shape,
        border,
        colors,
        contentPadding
    ) {
        Text(text)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Button(
    text: String,
    clickFlow: MutableSharedFlow<Unit>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionState: InteractionState = remember { InteractionState() },
    elevation: ButtonElevation? = ButtonConstants.defaultElevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonConstants.defaultButtonColors(),
    contentPadding: PaddingValues = ButtonConstants.DefaultContentPadding,
) {
    androidx.compose.material.Button(
        { clickFlow.call() },
        modifier,
        enabled,
        interactionState,
        elevation,
        shape,
        border,
        colors,
        contentPadding
    ) {
        Text(text)
    }
}