package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.Text
import androidx.compose.foundation.contentColor
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.extension.resourceToString


@Composable
fun ErrorSnackbar(
    text: String,
    actionText: String = R.string.retry.resourceToString(),
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = { }
) {
    Box(modifier = modifier.fillMaxWidth().wrapContentHeight()) {
        Crossfade(current = text) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                text = { Text(text, style = MaterialTheme.typography.body2) },
                action = {
                    TextButton(
                        onClick = onActionClick,
                        contentColor = contentColor()
                    ) {
                        Text(
                            text = actionText,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
            )
        }
    }
}