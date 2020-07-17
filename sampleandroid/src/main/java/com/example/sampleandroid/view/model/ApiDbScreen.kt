package com.example.sampleandroid.view.model

import androidx.compose.Composable
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.material.Button
import kim.jeonghyeon.androidlibrary.compose.widget.TextField
import kim.jeonghyeon.androidlibrary.compose.widget.VerticalListView
import kim.jeonghyeon.androidlibrary.extension.resourceToString
import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.compose.R
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow

//todo move impl to service locator
class ApiDbScreen(private val viewModel: ApiDbViewModel = ApiDbViewModel()) : ModelScreen(viewModel) {

    override val title: String = R.string.db_api.resourceToString()

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

class ApiDbViewModel(private val repository: WordRepository = serviceLocator.wordRepository) : BaseViewModel() {
    val wordList = MutableStateFlow<List<Word>>(listOf())
    val newWord = MutableStateFlow("")

    override fun onInitialized() {
        wordList.load(initStatus, repository.getWord())
    }

    fun onClickAdd() {
        status.load { repository.insertWord(newWord.value) }

    }
}