package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository

class ApiDbViewModel(private val repository: WordRepository) : SampleViewModel() {

    //todo required for ios to create instance, currently kotlin doesn't support predefined parameter
    // if it's supported, remove this
    constructor(): this(serviceLocator.wordRepository)

    val wordList by add {
        repository.getWord().toDataFlow(initStatus)
    }
    val newWord by add { DataFlow<String>() }

    fun onClickAdd() {
        status.load {
            repository.insertWord(newWord.value?: error("please input word"))
        }
    }
}