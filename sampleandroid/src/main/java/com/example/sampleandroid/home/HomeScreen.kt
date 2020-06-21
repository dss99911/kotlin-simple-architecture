package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow
import com.example.sampleandroid.MainScaffold
import com.example.sampleandroid.common.Screen
import com.example.sampleandroid.common.Tab

class HomeScreen : Screen {
    val tabs = listOf(ViewTab(), ModelTab())
    var tabIndex by mutableStateOf(0)
    var currentTab
        get() = tabs[tabIndex]
        set(tab: Tab) {
            tabIndex = tabs.indexOf(tab)
        }

    override val compose: @Composable() () -> Unit = {
        MainScaffold {
            Column {
                TabRow(
                    items = tabs.map { it.title }, selectedIndex = tabIndex
                ) { index, title ->
                    Tab(
                        icon = { tabs[index].icon?.let { Icon(it) } },
                        text = { Text(title) },
                        selected = tabIndex == index,
                        onSelected = {
                            tabIndex = (index)
                        })
                }
                Surface(modifier = Modifier.weight(1f)) {
                    tabs[tabIndex].compose()
                }
            }
        }
    }
}