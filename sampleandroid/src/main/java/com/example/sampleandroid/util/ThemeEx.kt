package com.example.sampleandroid.util

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme

//todo move to library
@Composable
inline val colors
    get() = MaterialTheme.colors
@Composable
inline val shapes
    get() = MaterialTheme.shapes
@Composable
inline val typography
    get() = MaterialTheme.typography