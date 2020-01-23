package kim.jeonghyeon.androidlibrary.architecture.livedata

import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * SingleLiveData.java
 *  - livedata.switchMap { SingleLiveData(it.value) }
 *  - this is limited to use on single event. and difficult to convert to normal live data
 * LiveData<Event>.kt
 *  - livedata.map { Event(it) }
 *  - extensions
 *  - but we have to use Event. and understand new concept of Event.
 *  - and sometimes event and not event both case can be happen.
 *
 * BaseLiveData<>
 * .asBase()
 *  - view side decide if it is event or not. viewModel doesn't need to know it.
 */
open class LiveObject<T>() : MediatorLiveData<T>() {
    constructor(value: T) : this() {
        setValue(value)
    }

    //region firstActive
    private val isFirst = AtomicBoolean(true)

    override fun onActive() {
        if (isFirst.getAndSet(false)) {
            onFirstActive()
        }
        super.onActive()
    }

    open fun onFirstActive() {}
    //endregion firstActive

    //region sources

    private val sources by lazy { mutableListOf<LiveData<*>>() }

    fun removeSources() {
        sources.forEach {
            removeSource(it)
        }
    }

    override fun <S : Any?> addSource(source: LiveData<S>, onChanged: Observer<in S>) {
        super.addSource(source, onChanged)
        sources.add(source)
    }

    fun addSources(vararg source: LiveData<Any?>, onChanged: () -> Unit) {
        source.forEach {
            addSource(it) {
                onChanged()
            }
        }
    }

    fun <S : Any?> replaceSource(source: LiveData<S>, onChanged: Observer<in S>) {
        removeSources()
        super.addSource(source, onChanged)
        sources.add(source)
    }


    override fun <S : Any?> removeSource(toRemote: LiveData<S>) {
        super.removeSource(toRemote)
        sources.remove(toRemote)
    }

    //endregion sources


    //region event
    var handled = true
        private set // Allow external read but not write

    @MainThread
    fun observeEvent(owner: LifecycleOwner, observer: Observer<in T>) {
        //only one observer will be notified of changes.

        // Observe the internal MutableLiveData
        super.observe(owner, Observer<T> { t ->
            if (!handled) {
                handled = true
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    final override fun setValue(t: T?) {
        handled = false
        super.setValue(t)
    }
    //endregion event
}

fun <T> LiveObject<T>.observeEvent(owner: LifecycleOwner, onChanged: (T) -> Unit) {
    observeEvent(owner, Observer {
        onChanged(it)
    })
}

operator fun <T> LiveObject<T>.plusAssign(other: LiveData<out T>) {
    addSource(other, ::setValue)
}

fun <T> LiveObject<T>.receive(other: () -> LiveData<T>) {
    addSource(other(), ::setValue)
}


@MainThread
inline fun <X, Y> LiveObject<X>.map(crossinline transform: (X) -> Y): LiveObject<Y> {
    val result = LiveObject<Y>()
    result.addSource(this) { x -> result.value = transform(x) }
    return result
}

@MainThread
inline fun <X, Y> LiveObject<X>.switchMap(
    crossinline transform: (X) -> LiveObject<Y>
): LiveObject<Y> {
    val result = LiveObject<Y>()
    result.addSource(this, object : Observer<X> {
        var mSource: LiveObject<Y>? = null
        override fun onChanged(x: X) {
            val newLiveData = transform(x)
            if (mSource === newLiveData) {
                return
            }
            if (mSource != null) {
                result.removeSource(mSource!!)
            }
            mSource = newLiveData
            if (mSource != null) {
                result.addSource(
                    mSource!!
                ) { y -> result.value = y }
            }
        }
    })
    return result
}

@Suppress("NOTHING_TO_INLINE")
inline fun <X> LiveObject<X>.distinct(): LiveObject<X> {
    val outputLiveData = LiveObject<X>()
    outputLiveData.addSource(this, object : Observer<X> {
        var mFirstTime = true
        override fun onChanged(currentValue: X) {
            val previousValue = outputLiveData.value
            if (mFirstTime
                || previousValue == null && currentValue != null
                || previousValue != null && previousValue != currentValue
            ) {
                mFirstTime = false
                outputLiveData.value = currentValue
            }
        }
    })
    return outputLiveData
}


/**
 * if return is true, remove observer
 */
fun <T> LiveObject<T>.observeUntil(@NonNull func: (T) -> Boolean) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T) {
            if (func(t)) {
                removeObserver(this)
            }
        }
    })
}

fun <T> LiveData<T>.asBase(): LiveObject<T> = LiveObject<T>().apply {
    plusAssign(this@asBase)
}

fun <T> LiveObject<T>.call(value: T) {
    postValue(value)
}

fun LiveObject<Unit>.call() {
    postValue(Unit)
}