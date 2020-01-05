package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 * able to remove all sources
 */
class BaseMediatorLiveData<T> : MediatorLiveData<T>() {
    private val sources = mutableListOf<LiveData<*>>()

    fun removeSources() {
        sources.forEach {
            removeSource(it)
        }
    }

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        super.addSource(source, onChanged)
        sources.add(source)
    }

    override fun <S : Any?> removeSource(toRemote: LiveData<S>) {
        super.removeSource(toRemote)
        sources.remove(toRemote)
    }
}