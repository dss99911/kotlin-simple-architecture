package com.example.sampleandroid.view.home

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sampleandroid.view.model.ModelScreen
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.push
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R

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
        ScrollableColumn {
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

