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
        wordList.load(initStatus, repository.getWord())
    }

    fun onClickAdd() {
        status.load { repository.insertWord(newWord.value) }
    }
}