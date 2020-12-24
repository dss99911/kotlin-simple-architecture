package kim.jeonghyeon.androidlibrary.compose.screen

import androidx.compose.material.Icon
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.client.ViewModelFlow

data class TabData(val icon: ImageVector?, val title: String, val view: @Composable () -> Unit)

@Composable
fun SimpleTabsScreen(tabIndexFlow: ViewModelFlow<Int>, tabs: List<TabData>) {
    val tabIndex = +tabIndexFlow ?: 0

    Column {
        TabRow(tabIndex) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        tabIndexFlow.value = index
                    },
                    icon = { tab.icon?.let { Icon(it) } },
                    text = { Text(tab.title) },
                )
            }
        }

        Surface {
            tabs[tabIndex].view()
        }
    }
}