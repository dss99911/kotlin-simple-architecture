package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository

/**
 * shows how to use repository which has flow using api and database.
 */
class ApiDbViewModel(private val repository: WordRepository = serviceLocator.wordRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "DB Api together"

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