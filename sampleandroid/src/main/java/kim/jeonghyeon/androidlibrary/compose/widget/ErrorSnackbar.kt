package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.contentColor
import androidx.ui.layout.padding
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Snackbar
import androidx.ui.unit.dp
import kim.jeonghyeon.androidlibrary.R
import kim.jeonghyeon.androidlibrary.extension.resourceToString


@Composable
fun ErrorSnackbar(
    text: String,
    actionText: String = R.string.retry.resourceToString(),
    modifier: Modifier = Modifier,
    onActionClick: () -> Unit = { }
) {
    Snackbar(
        modifier = modifier.padding(16.dp),
        text = { Text(text) },
        action = {
            Button(
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