package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Description
import com.example.sampleandroid.R
import com.example.sampleandroid.common.Tab
import kim.jeonghyeon.androidlibrary.extension.getString

class ModelTab : Tab {
    override val icon: VectorAsset?
        get() = Icons.Filled.Description
    override val title: String get() = R.string.model.getString()

    override val compose: @Composable() () -> Unit = {
        Text("model")
    }
}