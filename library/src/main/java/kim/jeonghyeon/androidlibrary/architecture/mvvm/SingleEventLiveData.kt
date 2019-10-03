@file:Suppress("RemoveEmptyPrimaryConstructor")

package kim.jeonghyeon.androidlibrary.architecture.mvvm

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

/**
 *
 * why use this?
 * 1.
 *  - if referring to activity, fragment. it may cause memory leak.
 *  - if use WeakReference to prevent memory leak, and Activity.onCreate set activity to viewModel. it is fine.
 *  - but this is simpler, and doesn't depend on structure of MVVM. and doesn't need to consider life cycle of activity.
 *  - if refer to activity, if activity is null. can't process ui side task. but if use this. later on activity created. it will be processed.
 *
 *
 * 2. LiveData return data again and again when observer become active even if it should be processed only one time.
 *
 * 3. UIHandler can be stored at WeakReference, but ViewModel constructor parameter contains it. and though I thought it will be okay, if not set var or val keyword, yet lambda can access the parameter. it means that Though it is local parameter, it is not garbage collected.
 *
 *
 */
@Deprecated(
    "This is too complicated",
    ReplaceWith("kim.jeonghyeon.androidlibrary.architecture.mvvm.Event")
)
open class SingleEventLiveData<T> {
    private val liveData by lazy { MediatorLiveData<SingleEvent<T>>() }

    companion object {
        fun <X, Y> map(source: LiveData<X>, function: (X) -> Y): Event {
            val result = Event<Y>()
            result.liveData.addSource(source) { t ->
                result.call(function(t))
            }
            return result
        }
    }

    fun call(value: T) {
        liveData.postValue(SingleEvent(value))
    }

    fun setValue(value: T) {
        liveData.value = SingleEvent(value)
    }

    fun <S> addSource(source: LiveData<S>, onChanged:(s:S) -> Unit) {
        liveData.addSource(source, onChanged)
    }

    fun observe(@NonNull owner: LifecycleOwner, @NonNull action: (T) -> Unit) {
        if (liveData.hasObservers()) {
            error(Event::class.simpleName + " support only 1 observer")
        }

        liveData.observe(owner, SingleEventObserver(action))
    }

    fun observeForever(@NonNull action: (T) -> Unit) {
        if (liveData.hasObservers()) {
            error(Event::class.simpleName + " support only 1 observer")
        }

        liveData.observeForever(SingleEventObserver(action))
    }

    fun <Y> map(@NonNull func: (T) -> Y?): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource<SingleEvent<T>>(liveData) { event ->
            if (!event.hasBeenHandled) {
                result.value = func(event.popContent())
            }
        }
        return result
    }

    fun <Y> eventMap(@NonNull func: (T) -> Y): Event {
        val result = Event<Y>()
        result.addSource<SingleEvent<T>>(liveData) { event ->
            if (!event.hasBeenHandled) {
                result.setValue(func(event.popContent()))
            }
        }
        return result
    }

    fun <Y> switchMap(@NonNull func: (T) -> LiveData<Y>): LiveData<Y> {
        val result = MediatorLiveData<Y>()
        result.addSource(liveData, object : Observer<SingleEvent<T>> {
            var mSource: LiveData<Y>? = null

            override fun onChanged(x: SingleEvent<T>?) {
                if (x?.hasBeenHandled != false) {
                    return
                }

                val newLiveData = func(x.popContent())
                if (mSource === newLiveData) {
                    return
                }
                if (mSource != null) {
                    result.removeSource(mSource!!)
                }
                mSource = newLiveData
                if (mSource != null) {
                    result.addSource(mSource!!) { y -> result.value = y }
                }
            }
        })
        return result
    }
}

fun <X, Y> LiveData<X>.eventMap(function: (X) -> Y): Event =
    Event.map(this, function)


internal class SingleEventObserver<T>(val action: (T) -> Unit) : Observer<SingleEvent<T>> {
    override fun onChanged(event: SingleEvent<T>) {
        if (!event.hasBeenHandled) {
            action(event.popContent())
        }

    }
}

internal class SingleEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    fun popContent(): T {
        hasBeenHandled = true
        return content
    }
}

class EmptySingleEventLiveData : Event() {
    fun call() {
        call(null)
    }
}