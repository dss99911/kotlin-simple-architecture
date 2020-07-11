package com.example.sampleandroid.view.home

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Button
import androidx.ui.unit.dp
import com.example.sampleandroid.R
import com.example.sampleandroid.view.model.ModelScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.extension.resourceToString

class ModelTabScreen : Screen() {
    override val title: String = R.string.model.resourceToString()

    //todo loadInIdle
    // loadDebounce
    // paging
    // api cache in memory or pergist
    // socket communication. chatting, auto update by flow
    // how to support switchMap?
    // add error handle on ui.
    // FCM push
    // viewModel for complicated case
    @Composable
    override fun compose() {
        super.compose()
    }

    @Composable
    override fun view() {
        Column {
            ModelScreen.screens.forEach {
                Button(
                    onClick = { it.second().push() },
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(it.first)
                }
            }
        }
    }
}

