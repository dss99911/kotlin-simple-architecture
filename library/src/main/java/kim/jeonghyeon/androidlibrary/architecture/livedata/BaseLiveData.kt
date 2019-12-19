package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LiveData
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseLiveData<T> : LiveData<T>() {
    private val isFirst = AtomicBoolean(true)
    override fun onActive() {
        if (isFirst.getAndSet(false)) {
            onFirstActive()
        }
        super.onActive()

    }

    open fun onFirstActive() {}
}