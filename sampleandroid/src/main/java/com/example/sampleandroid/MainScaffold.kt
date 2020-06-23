package com.example.sampleandroid

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Menu
import com.example.sampleandroid.drawer.HomeDrawer
import com.example.sampleandroid.drawer.ModelDrawer
import com.example.sampleandroid.home.HomeScreen
import com.example.sampleandroid.library.ScreenStack

@Composable
fun MainScaffold(content: @Composable() (Modifier) -> Unit) {
    Scaffold(
        scaffoldState = CommonState.scaffoldState,
        drawerContent = {
            when (ScreenStack.last()) {
                is HomeScreen -> HomeDrawer()
                else -> ModelDrawer()
            }
        },
        topAppBar = {
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
        bodyContent = { modifier ->
            content(modifier)
        }
    )
}

