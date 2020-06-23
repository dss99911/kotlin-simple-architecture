package com.example.sampleandroid.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent

abstract class BaseActivity : AppCompatActivity() {

    abstract val content: @Composable() () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            content()
        }
    }

    override fun onBackPressed() {
        ScreenStack.pop() ?: super.onBackPressed()
    }
}