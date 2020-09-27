package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository

class ApiDbViewModel(private val repository: WordRepository) : BaseViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.wordRepository)

    val wordList = dataFlow<List<Word>>(listOf())
    val newWord = dataFlow("")

    //todo without this, memory access error. https://hyun.myjetbrains.com/youtrack/issue/KSA-116
    val duplicatedList = dataFlow<List<Word>>(listOf()).withSource(wordList) {
        value = it
    }

    override fun onInitialized() {
        wordList.load(initStatus, repository.getWord())
    }

    fun onClickAdd() {
        status.load { repository.insertWord(newWord.value) }
    }
}