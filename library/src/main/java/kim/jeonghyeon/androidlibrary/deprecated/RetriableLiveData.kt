package kim.jeonghyeon.androidlibrary.deprecated

import androidx.lifecycle.MediatorLiveData

open class RetriableLiveData<T>(val action: (T?) -> Boolean) : MediatorLiveData<T>() {

    /**
     * if failed to retry. return false
     */
    fun retry(): Boolean = action(value)
}