package com.example.sampleandroid.view

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ViewModule
import com.example.sampleandroid.R
import com.example.sampleandroid.library.TabView
import kim.jeonghyeon.androidlibrary.extension.getString

class ViewTabView : TabView() {
    override val icon: VectorAsset? = Icons.Filled.ViewModule
    override val title: String = R.string.view.getString()

    override val view: @Composable() () -> Unit = {
        Text("View")
    }
}