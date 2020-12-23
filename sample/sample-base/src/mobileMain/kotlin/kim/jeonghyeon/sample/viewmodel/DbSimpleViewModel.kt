package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.flowViewModel
import kim.jeonghyeon.client.valueOrNull
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator
import kim.jeonghyeon.type.Status
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * get data from database
 */
class DbSimpleViewModel(private val wordQueries: WordQueries = serviceLocator.wordQueries) : ModelViewModel() {
    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Simple DB call"

    //todo data loading takes time. it's better to use background thread. and, load as resource. instead of data
    val wordList by add { wordQueries.selectAll().asListFlow() }

    val newWord by add { flowViewModel<String>() }

    fun onClickAdd() {
        wordQueries.insert(newWord.valueOrNull?: error("please input word"))
    }
}

class DbSimpleViewModel2(private val wordQueries: WordQueries = serviceLocator.wordQueries) : ModelViewModel() {
    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Simple DB call"

    val click = flowViewModel<Unit>()
    val newWord by add { flowViewModel<String>() }

    val wordList by add { wordQueries.selectAll().asListFlow() }

    override val status: MutableSharedFlow<Status> by add {
        click.mapInIdle {
            wordQueries.insert(newWord.valueOrNull?: error("please input word"))
        }.toStatus()
    }
}