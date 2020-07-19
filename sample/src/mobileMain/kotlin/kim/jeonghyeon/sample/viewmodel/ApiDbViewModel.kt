package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository
import kotlinx.coroutines.flow.MutableStateFlow

class ApiDbViewModel(private val repository: WordRepository = serviceLocator.wordRepository) : BaseViewModel() {
    val wordList = MutableStateFlow<List<Word>>(listOf())
    val newWord = MutableStateFlow("")

    override fun onInitialized() {
        // TODO this is not working on IOS https://github.com/cashapp/sqldelight/issues/1845
//        wordList.loadFlow(initStatus) { repository.getWord() }

    }

    fun onClickAdd() {
        status.load { repository.insertWord(newWord.value) }
    }
}