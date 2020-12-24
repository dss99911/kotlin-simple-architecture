package com.example.sampleandroid.view.model

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sampleandroid.view.widget.SampleTextField
import kim.jeonghyeon.androidlibrary.compose.Screen
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
            Row(modifier = Modifier.padding(4.dp)) {
                SampleTextField(
                    "Input new row",
                    model.newWord,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    R.string.add.resourceToString(),
                    Modifier.align(CenterVertically)
                ) { model.onClick() }
            }

            SampleTextField("Search", model.keyword)

            ScrollableColumn(+model.list, Modifier.weight(1f).fillMaxWidth()) {
                Text(it)
            }
        }
    }
}

// TODO reactive way.
//@Composable
//fun ReactiveScreen2(model: ReactiveViewModel2) {
//    Screen(model) {
//        Column {
//            Row(modifier = Modifier.padding(4.dp)) {
//                SampleTextField(
//                    "Input new row",
//                    model.newWord,
//                    modifier = Modifier.weight(1f)
//                )
//                Button(R.string.add.resourceToString(), model.click, Modifier.align(CenterVertically))
//            }
//
//            SampleTextField("Search", model.keyword)
//
//            ScrollableColumn(+model.list, Modifier.weight(1f).fillMaxWidth()) {
//                Text(it)
//            }
//        }
//    }
//}