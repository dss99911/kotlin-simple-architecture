package com.example.sampleandroid

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.*
import androidx.ui.res.vectorResource
import kim.jeonghyeon.androidlibrary.extension.getString

@Composable
fun MainScaffold(content: @Composable() (Modifier) -> Unit) {
    val scaffoldState = remember { ScaffoldState() }

    Scaffold(
        scaffoldState = scaffoldState,
        drawerContent = {
            MainDrawer(closeDrawer = { scaffoldState.drawerState = DrawerState.Closed })
        },
        topAppBar = {
            TopAppBar(
                title = { Text(text = R.string.sample.getString()) },
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState = DrawerState.Opened }) {
                        Icon(vectorResource(R.drawable.ic_android))
                    }
                }
            )
        },
        bodyContent = { modifier ->
            content(modifier)
        }
    )
}