package kim.jeonghyeon.androidlibrary.compose.screen

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import kim.jeonghyeon.androidlibrary.compose.Screen

abstract class TabsScreen : Screen() {
    abstract val tabs: List<TabView>
    abstract val initialIndex: Int
    var tabIndex by mutableStateOf(initialIndex)
    var currentTab
        get() = tabs[tabIndex]
        set(tab: TabView) {
            tabIndex = tabs.indexOf(tab)
        }
}