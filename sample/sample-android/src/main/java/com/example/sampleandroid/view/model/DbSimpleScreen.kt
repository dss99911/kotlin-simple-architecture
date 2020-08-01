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
import kim.jeonghyeon.sample.viewmodel.DbSimpleViewModel

class DbSimpleScreen(private val viewModel: DbSimpleViewModel = DbSimpleViewModel()) : ModelScreen(viewModel) {
    override val title: String = R.string.db_simple.resourceToString()

    @Composable
    override fun view() {
        Column {
            VerticalListView(+viewModel.wordList, weight(1f)) {
                Text(it.toString())
            }
            Row {
                TextField(viewModel.newWord)
                Button(onClick = viewModel::onClickAdd) {
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