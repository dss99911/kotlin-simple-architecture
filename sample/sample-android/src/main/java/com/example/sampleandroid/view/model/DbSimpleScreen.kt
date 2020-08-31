package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import kim.jeonghyeon.androidlibrary.compose.widget.OutlinedTextField
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.DbSimpleViewModel

class DbSimpleScreen(private val model: DbSimpleViewModel = DbSimpleViewModel()) : ModelScreen(model) {
    override val title: String = R.string.db_simple.resourceToString()

    @Composable
    override fun view() {
        Column {
            Row(modifier = padding(4.dp)) {
                OutlinedTextField(
                    model.newWord,
                    label = { Text("Input new row")},
                    modifier = weight(1f)
                )
                Button(onClick = model::onClickAdd, modifier = gravity(CenterVertically)) {
                    Text(R.string.add.resourceToString())
                }
            }
            ScrollableColumn(+model.wordList, weight(1f).fillMaxWidth()) {
                Text(it.toString())
            }

        }
    }

    @Composable
    override fun compose() {
        super.compose()
    }
}