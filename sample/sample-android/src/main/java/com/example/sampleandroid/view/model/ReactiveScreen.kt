package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.dp
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.gravity
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.padding
import kim.jeonghyeon.androidlibrary.compose.ScreenUtil.weight
import kim.jeonghyeon.androidlibrary.compose.unaryPlus
import kim.jeonghyeon.androidlibrary.compose.widget.Button
import kim.jeonghyeon.androidlibrary.compose.widget.ScrollableColumn
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.viewmodel.ReactiveViewModel

@Composable
fun ReactiveScreen(model: ReactiveViewModel) {
    Screen(model) {
        Column {
            Button("No reactive example") { model.onClickNoReactiveSample() }

            Row(modifier = padding(4.dp)) {
                SampleTextField(
                    "Input new row",
                    model.newWord,
                    modifier = weight(1f)
                )
                Button(R.string.add.resourceToString(), model.click, gravity(CenterVertically))
            }

            SampleTextField("Search", model.keyword)

            ScrollableColumn(+model.list, weight(1f).fillMaxWidth()) {
                Text(it)
            }
        }
    }
}