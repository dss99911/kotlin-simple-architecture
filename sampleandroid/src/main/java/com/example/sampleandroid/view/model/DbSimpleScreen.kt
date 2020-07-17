package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.BaseViewModel
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class DbSimpleScreen(private val viewModel: DbSimpleViewModel = DbSimpleViewModel()) : ModelScreen(viewModel) {
    override val title: String = R.string.db_simple.resourceToString()

    @Composable
    override fun view() {
        //todo check if job is cancelled if composable leave
        Column {
            VerticalListView(+viewModel.wordList, Modifier.weight(1f)) {
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

class DbSimpleViewModel(private val wordQueries: WordQueries = serviceLocator.wordQueries) : BaseViewModel() {
    val wordList = MutableStateFlow<List<Word>>(listOf())
    val newWord = MutableStateFlow("")

    override fun onInitialized() {
        wordList.load(initStatus, wordQueries.selectAll().asListFlow())
    }

    fun onClickAdd() {
        wordQueries.insert(newWord.value)
    }
}