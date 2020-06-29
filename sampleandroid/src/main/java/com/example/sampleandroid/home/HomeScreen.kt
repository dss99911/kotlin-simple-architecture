package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Description
import androidx.ui.material.icons.filled.ViewModule
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.screen.SimpleTabsScreen
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class HomeScreen : SimpleTabsScreen() {
    override val title: String = R.string.home.resourceToString()
    override val tabs = listOf(
        Icons.Filled.Description to ModelTabScreen(),
        Icons.Filled.ViewModule to ViewTabScreen()
    )
    override val initialIndex: Int = 0

    @Composable
    override fun compose() {
        super.compose()
    }
}