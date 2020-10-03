package com.example.sampleandroid.view.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.screen.SimpleTabsScreen
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class HomeScreen : SimpleTabsScreen() {
    override val title: String = kim.jeonghyeon.sample.compose.R.string.home.resourceToString()
    override val tabs = listOf(
        Icons.Filled.Description to ModelTabScreen(),
        Icons.Filled.ViewModule to ViewTabScreen()
    )
    override val initialIndex: Int = 0

    override val isRoot = true

    @Composable
    override fun compose() {
        super.compose()
    }
}