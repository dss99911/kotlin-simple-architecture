package com.example.sampleandroid.home

import androidx.compose.Composable
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.screen.SimpleTabsScreen
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class HomeScreen : SimpleTabsScreen() {
    override val title: String = R.string.home.resourceToString()
    override val tabs = listOf(ModelTabView(), ViewTabView())
    override val initialIndex: Int = 0

    @Composable
    override fun compose() {
        super.compose()
    }
}