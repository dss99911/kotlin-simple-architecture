package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.material.Surface
import androidx.ui.material.Tab
import androidx.ui.material.TabRow

abstract class SimpleTabsScreen : TabsScreen() {

    override val view: @Composable() () -> Unit = {
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
                tabs[tabIndex].view()
            }
        }
    }
}