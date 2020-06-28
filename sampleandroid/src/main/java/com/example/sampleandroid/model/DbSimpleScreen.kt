package com.example.sampleandroid.model

import androidx.compose.Composable
import androidx.compose.mutableStateOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import com.example.sampleandroid.R
import kim.jeonghyeon.androidlibrary.compose.data
import kim.jeonghyeon.androidlibrary.compose.resourceStateOf
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator

class DbSimpleScreen(private val wordQueries: WordQueries = serviceLocator.wordQueries) : ModelScreen() {
    override val title: String = R.string.db_simple.resourceToString()

    private val wordList by lazy { resourceStateOf<List<Word>>() }
    private val newWord by lazy { mutableStateOf("") }

    override fun initialize() {
        wordList.load(initStatus, wordQueries.selectAll().asListFlow())
    }

    private fun onClickAdd() {
        wordQueries.insert(newWord.value)
    }

    @Composable
    override fun view() {

        //todo check if job is cancelled if composable leave
        Column {
            VerticalListView(wordList.data(), Modifier.weight(1f)) {
                Text(it.toString())
            }
            Row {
                TextField(newWord)
                Button(onClick = ::onClickAdd) {
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