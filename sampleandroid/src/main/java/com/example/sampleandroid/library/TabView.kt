package com.example.sampleandroid.library

import androidx.ui.graphics.vector.VectorAsset

abstract class TabView : Composing {
    abstract val title: String
    open val icon: VectorAsset? = null
}