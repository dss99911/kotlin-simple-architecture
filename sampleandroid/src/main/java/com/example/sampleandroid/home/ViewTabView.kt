package com.example.sampleandroid.home

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

    @Composable
    override fun view() {
        Text(title)
    }
}

