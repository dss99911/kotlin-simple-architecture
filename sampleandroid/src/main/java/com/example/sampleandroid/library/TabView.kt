package com.example.sampleandroid.library

import androidx.compose.Composable
import androidx.ui.graphics.vector.VectorAsset

abstract class TabView {
    abstract val title: String
    open val icon: VectorAsset? = null

    @Composable
    open fun view() {
    }

    inline fun compose() {
        title
    }
}