package com.example.sampleandroid.view.model

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.widget.Button
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
                SampleTextField(
                    "Input new row",
                    model.newWord,
                    modifier = weight(1f)
                )
                Button(R.string.add.resourceToString(), modifier = gravity(CenterVertically)) {
                    model.onClickAdd()
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