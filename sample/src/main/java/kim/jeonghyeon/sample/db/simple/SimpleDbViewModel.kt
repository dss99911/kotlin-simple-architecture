package kim.jeonghyeon.sample.db.simple

import kim.jeonghyeon.androidlibrary.architecture.mvvm.BaseViewModel
import kim.jeonghyeon.sample.WordQueries
import kim.jeonghyeon.sqldelight.asListLiveObject

class SimpleDbViewModel(val wordQueries: WordQueries) : BaseViewModel() {
    val list = wordQueries.selectAll().asListLiveObject()

    fun addItem(text: String) {
        wordQueries.insert(text)
    }

}