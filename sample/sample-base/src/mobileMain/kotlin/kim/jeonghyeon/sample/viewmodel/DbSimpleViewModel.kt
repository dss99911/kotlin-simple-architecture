package kim.jeonghyeon.sample.viewmodel

import kim.jeonghyeon.client.BaseViewModel
import kim.jeonghyeon.client.DataFlow
import kim.jeonghyeon.pergist.asListFlow
import kim.jeonghyeon.sample.Word
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sample.di.serviceLocator

/**
 * get data from database
 */
class DbSimpleViewModel(private val wordQueries: WordQueries = serviceLocator.wordQueries) : ModelViewModel() {
    //todo [KSA-48] support localization on kotlin side
    override val title: String = "Simple DB call"

    //todo data loading takes time. it's better to use background thread. and, load as resource. instead of data
    val wordList by add {
        wordQueries.selectAll()
            .asListFlow()
                //todo Default is not working on IOS
//            .flowOn(Dispatchers.Default)
            .toDataFlow()
    }

    val newWord by add { DataFlow<String>() }

    fun onClickAdd() {
        wordQueries.insert(newWord.value?: error("please input word"))
    }
}