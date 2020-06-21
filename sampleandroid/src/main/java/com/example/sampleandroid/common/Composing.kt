package com.example.sampleandroid.common

import androidx.compose.Composable


interface Composing {
    //function is notworking. so, used property
    val compose: @Composable() () -> Unit
}