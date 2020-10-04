package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(InternalLayoutApi::class)
@Composable
fun <T> ScrollableColumn(
    list: List<T>?,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(0f),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalGravity: Alignment.Horizontal = Alignment.Start,
    reverseScrollDirection: Boolean = false,
    isScrollEnabled: Boolean = true,
    contentPadding: InnerPadding = InnerPadding(0.dp),
    children: @Composable() ColumnScope.(T) -> Unit) {
    ScrollableColumn(modifier, scrollState, verticalArrangement, horizontalGravity, reverseScrollDirection, isScrollEnabled, contentPadding) {
        list?.forEach { children(it) }
    }
}