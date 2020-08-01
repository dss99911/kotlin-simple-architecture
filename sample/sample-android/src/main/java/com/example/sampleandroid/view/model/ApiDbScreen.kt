package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ApiDbViewModel


class ApiDbScreen(private val model: ApiDbViewModel = ApiDbViewModel()) : ModelScreen(model) {

    override val title: String = R.string.db_api.resourceToString()

    @Composable
    override fun view() {
        Column {
            VerticalListView(+model.wordList, weight(1f)) {
                Text(it.toString())
            }
            Row {
                TextField(model.newWord)
                Button(onClick = model::onClickAdd) {
                    Text(R.string.add.resourceToString())
                }
            }
        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }

}