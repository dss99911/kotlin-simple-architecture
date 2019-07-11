package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseMutableLiveData<T> : MutableLiveData<T>() {
    private val isFirst = AtomicBoolean(true)
    override fun onActive() {
        if (isFirst.getAndSet(false)) {
            onFirstActive()
        }
        super.onActive()

    }

    open fun onFirstActive() {}
}