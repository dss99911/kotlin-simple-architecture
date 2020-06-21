package com.example.sampleandroid.common

import androidx.ui.graphics.vector.VectorAsset

interface Tab : Composing {
    val title: String
    val icon: VectorAsset?
}