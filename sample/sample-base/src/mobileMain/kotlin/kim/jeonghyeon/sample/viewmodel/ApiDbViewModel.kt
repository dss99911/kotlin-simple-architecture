package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.*
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.sample.repository.WordRepository
import kim.jeonghyeon.type.Resource
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * shows how to use repository which has flow using api and database.
 */
class ApiDbViewModel(private val repository: WordRepository = serviceLocator.wordRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "DB Api together"

    val wordList by add {
        repository.word.toData(initStatus)
    }
    val newWord by add { flowViewModel<String>() }

    fun onClickAdd() {
        status.load {
            repository.insertWord(newWord.valueOrNull?: error("please input word"))
        }
    }
}

class ApiDbViewModel2(private val repository: WordRepository = serviceLocator.wordRepository) : ModelViewModel() {

    //todo [KSA-48] support localization on kotlin side
    override val title: String = "DB Api together"

    val newWord by add { flowViewModel<String>() }
    val clickAdd = flowViewModel<Unit>()

    val wordList by add {
        repository.word.toData(initStatus)
    }
    override val status: MutableSharedFlow<Status> by add {
        clickAdd.mapInIdle {
            repository.insertWord(newWord.valueOrNull?: error("please input word"))
        }.toStatus()
    }
}