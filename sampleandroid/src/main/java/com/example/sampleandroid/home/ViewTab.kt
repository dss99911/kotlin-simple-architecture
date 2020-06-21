package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewModule
import com.example.sampleandroid.R
import com.example.sampleandroid.common.Tab
import kim.jeonghyeon.androidlibrary.extension.getString

class ViewTab : Tab {
    override val icon: VectorAsset?
        get() = Icons.Filled.ViewModule
    override val title: String get() = R.string.view.getString()

    override val compose: @Composable() () -> Unit = {
        Text("View")
    }
}