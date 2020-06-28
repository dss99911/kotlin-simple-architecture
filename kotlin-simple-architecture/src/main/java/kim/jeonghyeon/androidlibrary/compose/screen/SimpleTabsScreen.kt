package kim.jeonghyeon.androidlibrary.compose.screen

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow

abstract class SimpleTabsScreen : TabsScreen() {

    @Composable
    override fun view() {
        Column {
            TabRow(
                items = tabs.map { it.title }, selectedIndex = tabIndex
            ) { index, title -> //-756387548, 32
                Tab(
                    icon = { tabs[index].icon?.let { Icon(it) } },
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onSelected = {
                        tabIndex = (index)
                    })
            }

            Surface {
                tabs[tabIndex].compose()
            }
        }
    }

}