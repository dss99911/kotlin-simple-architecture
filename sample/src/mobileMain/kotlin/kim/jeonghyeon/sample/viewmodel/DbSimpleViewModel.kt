package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator
import kotlinx.coroutines.flow.MutableStateFlow

class DbSimpleViewModel(private val wordQueries: WordQueries = serviceLocator.wordQueries) : BaseViewModel() {
    val wordList = MutableStateFlow<List<Word>>(listOf())
    val newWord = MutableStateFlow("")

    override fun onInitialized() {
        wordList.loadFlow(initStatus) { wordQueries.selectAll().asListFlow() }
    }

    fun onClickAdd() {
        wordQueries.insert(newWord.value)
    }
}