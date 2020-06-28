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
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository

//todo move impl to service locator
class ApiDbScreen(val repository: WordRepository = serviceLocator.wordRepository) : ModelScreen() {

    override val title: String = R.string.db_api.resourceToString()
    val wordList by lazy { resourceStateOf<List<Word>>()}
    private val newWord by lazy { mutableStateOf("") }

    override fun initialize() {
        wordList.load(initStatus, repository.getWord())
    }

    private fun onClickAdd() {
        status.load { repository.insertWord(newWord.value) }

    }

    @Composable
    override fun view() {
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