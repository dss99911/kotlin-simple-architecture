package com.example.sampleandroid.view

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.drawer.HomeDrawer
import com.example.sampleandroid.view.drawer.SubDrawer
import com.example.sampleandroid.view.home.HomeScreen
import com.example.sampleandroid.view.model.ModelScreen
import com.example.sampleandroid.view.view.ViewScreen
import kim.jeonghyeon.androidlibrary.compose.ScreenStack

@Composable
fun MainScaffold(content: @Composable() (InnerPadding) -> Unit) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            when (ScreenStack.last()) {
                is HomeScreen -> HomeDrawer(closeDrawer = { scaffoldState.drawerState.close() })
                is ModelScreen -> SubDrawer(ModelScreen.screens, closeDrawer = { scaffoldState.drawerState.close() })
                is ViewScreen -> SubDrawer(ViewScreen.screens, closeDrawer = { scaffoldState.drawerState.close() })
                else -> error("drawer doesn't exists for the screen")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = ScreenStack.last().title) },
                navigationIcon = {
                    IconButton(onClick = {
                        scaffoldState.drawerState.open()
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

