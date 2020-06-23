package com.example.sampleandroid.home

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.Button
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Description
import androidx.ui.unit.dp
import com.example.sampleandroid.R
import com.example.sampleandroid.library.TabView
import com.example.sampleandroid.library.push
import com.example.sampleandroid.model.ModelScreen
import kim.jeonghyeon.androidlibrary.extension.getString

class ModelTabView : TabView() {
    override val icon: VectorAsset? = Icons.Filled.Description
    override val title: String = R.string.model.getString()

    override val view: @Composable() () -> Unit = {
        Column {
            ModelScreen.screens.forEach {
                Button(
                    onClick = { it.push() },
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(it.title)
                }
            }
        }
    }
}