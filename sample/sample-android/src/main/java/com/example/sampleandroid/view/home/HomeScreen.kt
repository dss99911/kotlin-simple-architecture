package com.example.sampleandroid.view.home

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.screen.SimpleTabsScreen
import kim.jeonghyeon.androidlibrary.compose.screen.TabData
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.Navigator
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.HomeViewModel
import kim.jeonghyeon.sample.viewmodel.ModelViewModel
import kim.jeonghyeon.sample.viewmodel.RetrofitViewModel
import kim.jeonghyeon.sample.viewmodel.ViewViewModel

val homeTabList = listOf(
    TabData(Icons.Filled.Description, R.string.model.resourceToString()) { ModelTabScreen() },
    TabData(Icons.Filled.ViewModule, R.string.view.resourceToString()) { ViewTabScreen() }
)

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    Screen(viewModel) {
        SimpleTabsScreen(
            viewModel.currentTabIndex,
            tabs = homeTabList
        )
    }
}

@Composable
fun ModelTabScreen() {
    ScrollableColumn {
        ModelViewModel.items.forEach {
            Button(it.generate().title, Modifier.fillMaxWidth().padding(10.dp)) {
                Navigator.navigate(it.generate())//just for simplicity, created viewModel. but recommend to show title for each button
            }
        }

        val retrofitViewModel = RetrofitViewModel()
        Button(retrofitViewModel.title, Modifier.fillMaxWidth().padding(10.dp)) {

            Navigator.navigate(retrofitViewModel)
        }
    }
}

@Composable
fun ViewTabScreen() {
    ScrollableColumn {
        ViewViewModel.items.forEach {
            Button(it.generate().title, Modifier.fillMaxWidth().padding(10.dp)) {
                Navigator.navigate(it.generate())//just for simplicity, created viewModel. but recommend to show title for each button
            }
        }
    }
}