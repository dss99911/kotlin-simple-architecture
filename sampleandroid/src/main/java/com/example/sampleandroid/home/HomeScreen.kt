package com.example.sampleandroid.home

import com.example.sampleandroid.R
import com.example.sampleandroid.library.SimpleTabsScreen
import kim.jeonghyeon.androidlibrary.extension.getString

class HomeScreen : SimpleTabsScreen() {
    override val title: String = R.string.home.getString()
    override val tabs = listOf(ModelTabView(), ViewTabView())
    override val initialIndex: Int = 0
}