package kim.jeonghyeon.androidlibrary.compose.screen

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable

abstract class SimpleTabsScreen : TabsScreen() {
    @Composable
    override fun view() {
        Column {
            TabRow(tabIndex) {
                tabs.forEachIndexed { index, tab ->
                    tab.second.title
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = (index) },
                        icon = { tab.first?.let { Icon(it) } },
                        text = { Text(title) },
                    )
                }
            }

            Surface {
                tabs[tabIndex].second.compose()
            }
        }
    }

}