package com.example.sampleandroid.library

import androidx.compose.Composable


interface Composing {
    //function is notworking. so, used property
    val view: @Composable() () -> Unit
}