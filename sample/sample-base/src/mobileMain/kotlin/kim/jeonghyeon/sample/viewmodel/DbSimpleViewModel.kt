package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator

class DbSimpleViewModel(private val wordQueries: WordQueries) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.wordQueries)

    val wordList = dataFlow<List<Word>>(listOf())
    val newWord = dataFlow("")

    override fun onInit() {
        wordList.loadFlow(initStatus, wordQueries.selectAll().asListFlow())
    }

    fun onClickAdd() {
        wordQueries.insert(newWord.value)
    }
}