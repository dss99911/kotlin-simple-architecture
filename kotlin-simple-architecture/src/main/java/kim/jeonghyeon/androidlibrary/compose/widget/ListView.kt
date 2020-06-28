package kim.jeonghyeon.androidlibrary.compose.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.VerticalScroller

@Composable
fun <T> VerticalListView(list: List<T>, modifier: Modifier = Modifier, children: @Composable() (T) -> Unit) {
    VerticalScroller(modifier = modifier) {
        list.forEach { children(it) }
    }
}