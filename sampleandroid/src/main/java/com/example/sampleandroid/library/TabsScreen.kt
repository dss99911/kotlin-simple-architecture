package com.example.sampleandroid.library

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue

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