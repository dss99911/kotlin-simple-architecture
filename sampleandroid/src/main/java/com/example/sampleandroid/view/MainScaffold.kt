package com.example.sampleandroid.view

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.InnerPadding
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Menu
import com.example.sampleandroid.CommonState
import com.example.sampleandroid.view.drawer.HomeDrawer
import com.example.sampleandroid.view.drawer.SubDrawer
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.ModelScreen
import com.example.sampleandroid.view.view.ViewScreen
import kim.jeonghyeon.androidlibrary.compose.ScreenStack

@Composable
fun MainScaffold(content: @Composable() (InnerPadding) -> Unit) {
    Scaffold(
        scaffoldState = CommonState.scaffoldState,
        drawerContent = {
            when (ScreenStack.last()) {
                is HomeScreen -> HomeDrawer()
                is ModelScreen -> SubDrawer(ModelScreen.screens)
                is ViewScreen -> SubDrawer(ViewScreen.screens)
                else -> error("drawer doesn't exists for the screen")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = ScreenStack.last().title) },
                navigationIcon = {
                    IconButton(onClick = {
                        CommonState.openDrawer()
                    }) {
                        Icon(Icons.Filled.Menu)
                    }
                }
            )
        },
        bodyContent = {
            content(it)
        }
    )
}

